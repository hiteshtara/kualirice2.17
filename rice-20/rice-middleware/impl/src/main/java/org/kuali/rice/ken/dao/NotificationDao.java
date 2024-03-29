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
package org.kuali.rice.ken.dao;

import org.kuali.rice.core.framework.persistence.dao.GenericDao;
import org.kuali.rice.ken.bo.NotificationBo;

import java.sql.Timestamp;
import java.util.Collection;
/**
 * This is a description of what this class does - g1zhang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface NotificationDao {
	
	public Collection findMatchedNotificationsForResolution(Timestamp tm,  GenericDao dao);
	
	public Collection findMatchedNotificationsForUnlock(NotificationBo not, GenericDao dao);
	
}
