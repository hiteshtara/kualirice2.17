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
import org.kuali.rice.krad.uif.view.View;

/**
 * Widget that decorates a control transforming into a spinner
 *
 * <p>
 * Spinners allow the incrementing or decrementing of the controls value with an arrow up and down icon on
 * the right side of the control. How the value is incremented, min/max values, and so on can be configured
 * through the {@link org.kuali.rice.krad.uif.component.Component#getTemplateOptions()} property
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "spinner-bean", parent = "Uif-Spinner")
public class Spinner extends WidgetBase {
    private static final long serialVersionUID = -659830874214415990L;

    public Spinner() {
        super();
    }

    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);
    }
}
