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
package org.kuali.rice.kns.document.authorization;

import org.kuali.rice.krad.document.Document;

import java.util.Set;

/**
 * The DocumentPresentationController class is used for non-user related lock down 
 * 
 * 
 */
public interface TransactionalDocumentPresentationController extends DocumentPresentationController {
	 /**
     * @param document
     * @return Set of operations that allow to take on that document.
     */
    public Set<String> getEditModes(Document document);
    
	
}

