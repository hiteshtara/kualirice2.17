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
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.ClientSideState;

/**
 * Decorates a group with collapse/expand functionality
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "disclosure-bean", parent = "Uif-Disclosure")
public class Disclosure extends WidgetBase {
    private static final long serialVersionUID = 1238789480161901850L;

    private String collapseImageSrc;
    private String expandImageSrc;

    private int animationSpeed;

    @ClientSideState(variableName = "open")
    private boolean defaultOpen;

    private boolean renderImage;

    public Disclosure() {
        super();

        defaultOpen = true;
        renderImage = true;
    }

    /**
     * Path to the images that should be displayed to collapse the group
     *
     * @return image path
     */
    @BeanTagAttribute(name="CollapseImageSrc")
    public String getCollapseImageSrc() {
        return this.collapseImageSrc;
    }

    /**
     * Setter for the collapse image path
     *
     * @param collapseImageSrc
     */
    public void setCollapseImageSrc(String collapseImageSrc) {
        this.collapseImageSrc = collapseImageSrc;
    }

    /**
     * Path to the images that should be displayed to expand the group
     *
     * @return image path
     */
    @BeanTagAttribute(name="expandImageSrc")
    public String getExpandImageSrc() {
        return this.expandImageSrc;
    }

    /**
     * Setter for the expand image path
     *
     * @param expandImageSrc
     */
    public void setExpandImageSrc(String expandImageSrc) {
        this.expandImageSrc = expandImageSrc;
    }

    /**
     * Gives the speed for the open/close animation, a smaller int will result
     * in a faster animation
     *
     * @return animation speed
     */
    @BeanTagAttribute(name="animationSpeed")
    public int getAnimationSpeed() {
        return this.animationSpeed;
    }

    /**
     * Setter for the open/close animation speed
     *
     * @param animationSpeed
     */
    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    /**
     * Indicates whether the group should be initially open
     *
     * @return true if group should be initially open, false if it
     *         should be closed
     */
    @BeanTagAttribute(name="defaultOpen")
    public boolean isDefaultOpen() {
        return this.defaultOpen;
    }

    /**
     * Setter for the default open indicator
     *
     * @param defaultOpen
     */
    public void setDefaultOpen(boolean defaultOpen) {
        this.defaultOpen = defaultOpen;
    }

    /**
     * Indicates whether the expand/collapse image should be rendered for the closure, if set to false only
     * the group title will be clickable
     *
     * @return true to render the expand/colapse image false to not
     */
    @BeanTagAttribute(name="renderImage")
    public boolean isRenderImage() {
        return renderImage;
    }

    /**
     * Setter for the render expand/collapse image indicator
     *
     * @param renderImage
     */
    public void setRenderImage(boolean renderImage) {
        this.renderImage = renderImage;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#copy()
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);
        Disclosure disclosureCopy = (Disclosure) component;
        disclosureCopy.setAnimationSpeed(this.animationSpeed);
        disclosureCopy.setCollapseImageSrc(this.collapseImageSrc);
        disclosureCopy.setDefaultOpen(this.defaultOpen);
        disclosureCopy.setExpandImageSrc(this.expandImageSrc);
        disclosureCopy.setRenderImage(this.renderImage);
    }
}
