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
package org.kuali.rice.core.api.uif;

import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * An abstract widget that all widgets inherit from.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableAbstractWidget.Constants.TYPE_NAME)
public abstract class RemotableAbstractWidget extends AbstractDataTransferObject implements RemotableWidgetContract {

    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    RemotableAbstractWidget() {

    }

    public abstract static class Builder implements RemotableWidgetContract, ModelBuilder {
        Builder() {
            super();
        }

        //todo make ModelBuilder generic so I don't have to do this.
        public abstract RemotableAbstractWidget build();
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String TYPE_NAME = "AbstractWidgetType";
    }
}
