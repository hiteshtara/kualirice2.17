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
package org.kuali.rice.krad.uif.element;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.LightTable;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.layout.StackedLayoutManager;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ValidationMessages for logic and options specific to groups
 */
@BeanTag(name = "groupValidationMessages-bean", parent = "Uif-GroupValidationMessages")
public class GroupValidationMessages extends ValidationMessages {
    private static final long serialVersionUID = -5389990220206079052L;

    private boolean displayFieldLabelWithMessages;
    private boolean collapseAdditionalFieldLinkMessages;
    private boolean displayHeaderMessageSummary;

    private static final String SECTION_TOKEN = "s$";
    private static final String FIELDGROUP_TOKEN = "f$";
    private static final String TABLE_COLLECTION_TOKEN = "c$";

    @Override
    /**
     * Calls super and adds dataAttributes that are appropriate for group level validationMessages
     * data.  This data is used by the validation framework clientside.
     *
     * Some special handling at this level includes retrieving the groups and fields generated by
     * different collection layouts and handling page and fieldGroup scenarios slightly differently
     * due to the nature of how they are built out in the js.
     *
     * @see krad.validate.js
     */
    public void generateMessages(boolean reset, View view, Object model, Component parent) {
        super.generateMessages(reset, view, model, parent);

        Object parentContainer = parent.getContext().get(UifConstants.ContextVariableNames.PARENT);

        List<? extends Component> items = ((Container) parent).getItems();
        boolean skipSections = false;
        boolean isTableCollection = false;

        // Handle the special CollectionGroup case by getting the StackedGroups and DataFields generated by them
        if (parent instanceof CollectionGroup && ((CollectionGroup) parent).getLayoutManager() instanceof StackedLayoutManager) {
            items = ((StackedLayoutManager) ((CollectionGroup) parent).getLayoutManager()).getStackedGroups();
        } else if ((parent instanceof CollectionGroup
                && ((CollectionGroup) parent).getLayoutManager() instanceof TableLayoutManager)
                || parent instanceof LightTable) {
            // order is not needed  so null items
            items = null;
            skipSections = true;
            isTableCollection = true;
        }

        List<String> sectionIds = new ArrayList<String>();
        List<String> fieldOrder = new ArrayList<String>();
        collectIdsFromItems(items, sectionIds, fieldOrder, skipSections);

        boolean pageLevel = false;
        boolean forceShow = false;
        boolean showPageSummaryHeader = true;
        if (parent instanceof PageGroup) {
            pageLevel = true;
            forceShow = true;
            parent.addDataAttribute(UifConstants.DataAttributes.SERVER_MESSAGES, Boolean.toString(
                    GlobalVariables.getMessageMap().hasMessages()));
            if (this instanceof PageValidationMessages) {
                showPageSummaryHeader = ((PageValidationMessages) this).isShowPageSummaryHeader();
            }
        } else if (parentContainer instanceof FieldGroup) {
            String role = ((FieldGroup) parentContainer).getDataAttributes().get(UifConstants.DataAttributes.ROLE);
            if (StringUtils.isNotBlank(role) && role.equals("detailsFieldGroup")){
                forceShow = false;
            }
            else{
                //note this means container of the parent is a FieldGroup
                forceShow = true;
            }
        }

        boolean hasMessages = false;
        if (!this.getErrors().isEmpty() || !this.getWarnings().isEmpty() || !this.getInfos().isEmpty()) {
            hasMessages = true;
        }

        HashMap<String, Object> validationMessagesDataAttributes = new HashMap<String, Object>();

        Map<String, String> dataDefaults =
                (Map<String, String>) (KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryObject(
                        "Uif-GroupValidationMessages-DataDefaults"));

        //add necessary data attributes to map
        //display related
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.SUMMARIZE, true);
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.DISPLAY_MESSAGES, this.isDisplayMessages());
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.COLLAPSE_FIELD_MESSAGES, collapseAdditionalFieldLinkMessages);
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.SHOW_PAGE_SUMMARY_HEADER, showPageSummaryHeader);
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.DISPLAY_LABEL, displayFieldLabelWithMessages);
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.DISPLAY_HEADER_SUMMARY, displayHeaderMessageSummary);
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
            UifConstants.DataAttributes.IS_TABLE_COLLECTION, isTableCollection);

        //options
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.HAS_OWN_MESSAGES, hasMessages);
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.PAGE_LEVEL, pageLevel);
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.FORCE_SHOW, forceShow);

        //order related
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.SECTIONS, sectionIds);
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.ORDER, fieldOrder);

        //server messages
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.SERVER_ERRORS, ScriptUtils.escapeHtml(this.getErrors()));
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.SERVER_WARNINGS, ScriptUtils.escapeHtml(this.getWarnings()));
        this.addValidationDataSettingsValue(validationMessagesDataAttributes, dataDefaults,
                UifConstants.DataAttributes.SERVER_INFO, ScriptUtils.escapeHtml(this.getInfos()));

        if (!validationMessagesDataAttributes.isEmpty()){
            parent.addDataAttribute(UifConstants.DataAttributes.VALIDATION_MESSAGES, ScriptUtils.translateValue(
                    validationMessagesDataAttributes));
        }
    }

    /**
     * Collects all the ids from the items passed into this method.
     *
     * <p>Puts the ids of items determined to be sections
     * into the sectionIds list, and orders all items by the order they appear on the page in the order list with
     * special identifiers
     * to determine the type of item they are (used by the client js).  When skipSections is true do not
     * include sectionIds found in the lists.</p>
     *
     * @param items items of the group
     * @param sectionIds list to put section ids into
     * @param order list to put order of ids into (both fields and sections)
     * @param skipSections skip adding sections
     */
    protected void collectIdsFromItems(List<? extends Component> items, List<String> sectionIds, List<String> order,
            boolean skipSections) {

        if (items != null) {
            for (Component component : items) {
                String id = component.getId().replace("@id@", "");
                if (component instanceof Container || component instanceof FieldGroup) {
                    if (component instanceof FieldGroup) {
                        if (!skipSections &&
                                ((FieldGroup) component).getFieldLabel().isRender() &&
                                !((FieldGroup) component).getFieldLabel().isHidden() &&
                                (StringUtils.isNotEmpty(((FieldGroup) component).getLabel()) || StringUtils.isNotEmpty(
                                        ((FieldGroup) component).getFieldLabel().getLabelText()))) {
                            sectionIds.add(id);
                            order.add(FIELDGROUP_TOKEN + id);
                            continue;
                        } else {
                            component = ((FieldGroup) component).getGroup();
                            if (component == null) {
                                continue;
                            }
                        }
                    }

                    id = component.getId().replace("@id@", "");
                    //If any kind of header text is showing consider this group a section
                    if (!skipSections
                            && ((Container) component).getHeader() != null
                            && ((Container) component).getHeader().isRender()
                            && (StringUtils.isNotBlank(((Container) component).getHeader().getHeaderText()) || StringUtils
                            .isNotBlank(component.getTitle()))) {
                        sectionIds.add(id);
                        order.add(SECTION_TOKEN + id);
                    } else if ((component instanceof CollectionGroup
                                    && ((CollectionGroup) component).getLayoutManager() instanceof TableLayoutManager)
                                    || component instanceof LightTable){
                        order.add(TABLE_COLLECTION_TOKEN + id);
                    } else {
                        collectIdsFromItems(((Container) component).getItems(), sectionIds, order, skipSections);
                    }
                } else if (component instanceof InputField) {
                    order.add(id);
                }
            }
        }
    }

    /**
     * If true, the error messages will display the an InputField's title
     * alongside the error, warning, and info messages related to it. This
     * setting has no effect on messages which do not relate directly to a
     * single InputField.
     *
     * @return the displayFieldLabelWithMessages
     */
    @BeanTagAttribute(name = "displayFieldLabelWithMessages")
    public boolean isDisplayFieldLabelWithMessages() {
        return this.displayFieldLabelWithMessages;
    }

    /**
     * If true, the error messages will display the an InputField's title
     * alongside the error, warning, and info messages related to it. This
     * setting has no effect on messages which do not relate directly to a
     * single InputField.
     *
     * @param displayFieldLabelWithMessages the displayFieldLabelWithMessages to set
     */
    public void setDisplayFieldLabelWithMessages(boolean displayFieldLabelWithMessages) {
        this.displayFieldLabelWithMessages = displayFieldLabelWithMessages;
    }

    /**
     * When collapseAdditionalFieldLinkMessages is set to true, the messages generated on field links will be
     * summarized to limit the space they take up with an appendage similar to [+n message type] appended for
     * additional
     * messages that are omitted.  When this flag is false, all messages will be part of the link separated by
     * a comma.
     *
     * @return if field link messages are being collapsed
     */
    @BeanTagAttribute(name = "collapseAdditionalFieldLinkMessages")
    public boolean isCollapseAdditionalFieldLinkMessages() {
        return collapseAdditionalFieldLinkMessages;
    }

    /**
     * Set collapseAdditionalFieldLinkMessages
     *
     * @param collapseAdditionalFieldLinkMessages - true if field link messages are being collapsed
     */
    public void setCollapseAdditionalFieldLinkMessages(boolean collapseAdditionalFieldLinkMessages) {
        this.collapseAdditionalFieldLinkMessages = collapseAdditionalFieldLinkMessages;
    }

    /**
     * If true, the header message summary will display (this is the message count message appended to section
     * headers).
     *
     * @return true if the summary will display, false otherwise
     */
    @BeanTagAttribute(name = "displayHeaderMessageSummary")
    public boolean isDisplayHeaderMessageSummary() {
        return displayHeaderMessageSummary;
    }

    /**
     * Sets whether the header message summary will display or not for this section/page.
     *
     * @param displayHeaderMessageSummary
     */
    public void setDisplayHeaderMessageSummary(boolean displayHeaderMessageSummary) {
        this.displayHeaderMessageSummary = displayHeaderMessageSummary;
    }
}
