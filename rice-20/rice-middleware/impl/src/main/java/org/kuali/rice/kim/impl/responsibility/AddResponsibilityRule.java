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
package org.kuali.rice.kim.impl.responsibility;

import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface AddResponsibilityRule extends BusinessRule {
    boolean processAddResponsibility(AddResponsibilityEvent addResponsibilityEvent);
    boolean hasPermissionToGrantResponsibility(Responsibility kimResponsibilityInfo, IdentityManagementRoleDocument document);
}
