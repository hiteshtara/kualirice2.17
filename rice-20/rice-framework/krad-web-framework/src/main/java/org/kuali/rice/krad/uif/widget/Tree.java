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
package org.kuali.rice.krad.uif.widget;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.uif.component.Component;

/**
 * Widget component for rendering a Tree
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "tree-bean", parent = "Uif-Tree")
public class Tree extends WidgetBase {
    private static final long serialVersionUID = -6852455590809381513L;

    public Tree() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#copy()
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);
        Tree treeCopy = (Tree) component;
    }
}
