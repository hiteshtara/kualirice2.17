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
package org.kuali.rice.kim.api.role;

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

/**
 * This is the contract for a KimPermission.  
 *  
 */
public interface RolePermissionContract extends Versioned, GloballyUnique, Identifiable, Inactivatable {
    
    /**
     * The Role ID referenced by the Role Permission.
     * 
     * @return roleId
     */
	String getRoleId();  
	
    /**
     * The Permission ID referenced by the Role Permission.
     * 
     * @return permissionId
     */
	String getPermissionId();
}
