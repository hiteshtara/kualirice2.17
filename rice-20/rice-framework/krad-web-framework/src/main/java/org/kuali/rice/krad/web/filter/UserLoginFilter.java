/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.web.filter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.MDC;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.AuthenticationService;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.exception.AuthenticationException;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

/**
 * A filter for processing user logins and creating a {@link org.kuali.rice.krad.UserSession}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.UserSession
 */
public class UserLoginFilter implements Filter {

    private static final String MDC_USER = "user";

    private IdentityService identityService;
    private PermissionService permissionService;
    private ConfigurationService kualiConfigurationService;
    private ParameterService parameterService;

    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.filterConfig = config;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {
            establishUserSession(request);
            establishSessionCookie(request, response);
            establishBackdoorUser(request);

            addToMDC(request);

            chain.doFilter(request, response);
        } finally {
            removeFromMDC();
        }
    }

    @Override
    public void destroy() {
        filterConfig = null;
    }

    /**
     * Checks if a user can be authenticated and if so establishes a UserSession for that user.
     */
    private void establishUserSession(HttpServletRequest request) {
        if (!isUserSessionEstablished(request)) {
            String principalName = ((AuthenticationService) GlobalResourceLoader.getResourceLoader().getService(
                    new QName("kimAuthenticationService"))).getPrincipalName(request);
            if (StringUtils.isBlank(principalName)) {
                throw new AuthenticationException("Blank User from AuthenticationService - This should never happen.");
            }

            Principal principal = getIdentityService().getPrincipalByPrincipalName(principalName);
            if (principal == null) {
                throw new AuthenticationException("Unknown User: " + principalName);
            }

            if (!isAuthorizedToLogin(principal.getPrincipalId())) {
                throw new AuthenticationException(
                        "You cannot log in, because you are not an active Kuali user.\nPlease ask someone to activate your account if you need to use Kuali Systems.\nThe user id provided was: "
                                + principalName + ".\n");
            }

            final UserSession userSession = new UserSession(principalName);
            if (userSession.getPerson() == null) {
                throw new AuthenticationException("Invalid User: " + principalName);
            }

            request.getSession().setAttribute(KRADConstants.USER_SESSION_KEY, userSession);
        }
    }

    /**
     * checks if the passed in principalId is authorized to log in.
     */
    private boolean isAuthorizedToLogin(String principalId) {
        return getPermissionService().isAuthorized(principalId, KimConstants.KIM_TYPE_DEFAULT_NAMESPACE,
                KimConstants.PermissionNames.LOG_IN, Collections.singletonMap("principalId", principalId));
    }

    /**
     * Creates a session id cookie if one does not exists.  Write the cookie out to the response with that session id.
     * Also, sets the cookie on the established user session.
     */
    private void establishSessionCookie(HttpServletRequest request, HttpServletResponse response) {
        String kualiSessionId = this.getKualiSessionId(request.getCookies());
        if (kualiSessionId == null) {
            kualiSessionId = UUID.randomUUID().toString();
            response.addCookie(new Cookie(KRADConstants.KUALI_SESSION_ID, kualiSessionId));
        }
        KRADUtils.getUserSessionFromRequest(request).setKualiSessionId(kualiSessionId);
    }

    /**
     * gets the kuali session id from an array of cookies.  If a session id does not exist returns null.
     */
    private String getKualiSessionId(final Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (KRADConstants.KUALI_SESSION_ID.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * establishes the backdoor user on the established user id if backdoor capabilities are valid.
     */
    private void establishBackdoorUser(HttpServletRequest request) {
        final String backdoor = request.getParameter(KRADConstants.BACKDOOR_PARAMETER);
        if (StringUtils.isNotBlank(backdoor)) {
            if (!getKualiConfigurationService().getPropertyValueAsString(KRADConstants.PROD_ENVIRONMENT_CODE_KEY)
                    .equalsIgnoreCase(getKualiConfigurationService().getPropertyValueAsString(
                            KRADConstants.ENVIRONMENT_KEY))) {
                if (getParameterService().getParameterValueAsBoolean(KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                        KRADConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, KewApiConstants.SHOW_BACK_DOOR_LOGIN_IND)) {
                    try {
                        KRADUtils.getUserSessionFromRequest(request).setBackdoorUser(backdoor);
                    } catch (RiceRuntimeException re) {
                        //Ignore so BackdoorAction can redirect to invalid_backdoor_portal
                    }
                }
            }
        }
    }

    private void addToMDC(HttpServletRequest request) {
        MDC.put(MDC_USER, KRADUtils.getUserSessionFromRequest(request).getPrincipalName());
    }

    private void removeFromMDC() {
        MDC.remove(MDC_USER);
    }

    /**
     * Checks if the user who made the request has a UserSession established
     *
     * @param request the HTTPServletRequest object passed in
     * @return true if the user session has been established, false otherwise
     */
    private boolean isUserSessionEstablished(HttpServletRequest request) {
        return (request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY) != null);
    }

    private IdentityService getIdentityService() {
        if (this.identityService == null) {
            this.identityService = KimApiServiceLocator.getIdentityService();
        }

        return this.identityService;
    }

    private PermissionService getPermissionService() {
        if (this.permissionService == null) {
            this.permissionService = KimApiServiceLocator.getPermissionService();
        }

        return this.permissionService;
    }

    private ConfigurationService getKualiConfigurationService() {
        if (this.kualiConfigurationService == null) {
            this.kualiConfigurationService = CoreApiServiceLocator.getKualiConfigurationService();
        }

        return this.kualiConfigurationService;
    }

    private ParameterService getParameterService() {
        if (this.parameterService == null) {
            this.parameterService = CoreFrameworkServiceLocator.getParameterService();
        }

        return this.parameterService;
    }
}
