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
package org.kuali.rice.krad.uif.view;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.util.KRADConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Holds a configuration of CSS and JS assets that provides the base for one or more views
 *
 * <p>
 * The list of CSS and JS files that are sourced in for a view come from its theme, along with any
 * additional files configured for the specific view. Generally an application will have one theme for the
 * entire application.
 *
 * The theme has logic for 'dev' mode versus 'test/prod' mode. This is controlled through the
 * {@code rice.krad.dev.mode} configuration variable. In development mode it will source in all the CSS
 * and JS files individually (to allow for easier debugging). In non-development mode it will source in a
 * minified file. The path for the minified files can be specified by setting {@link #getMinCssFile()} and
 * {@link #getMinScriptFile()}. If not set, it will be formed by using the {@link #getName()},
 * {@link #getMinVersionSuffix()}, and min suffix (this is the file name generated by the theme builder). To
 * indicate the min file should not be sourced in regardless of the environment, set the property
 * {@link #isIncludeMinFiles()} to false
 *
 * The path to the minified file is determined by {@link #getDirectory()}. It this is not set, it is defaulted to
 * be '/themes' plus the name of the theme (eg '/themes/kboot')
 * </p>
 *
 * <p>
 * There are two ways the theme can be configured, manual or by convention. If you want to manually configured the
 * view theme, set {@link #isUsesThemeBuilder()} to false. For dev mode, you must then set the {@link
 * #getMinCssSourceFiles()} and {@link #getMinScriptSourceFiles()} lists to the theme files. For configuration
 * by convention, only the theme {@link #getName()} is required. The directory will be assumed to be '/themes/{name}'.
 * Furthermore the list of min CSS and JS files will be retrieved from the theme.properties file created by the
 * theme builder
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "viewTheme-bean", parent = "Uif-ViewTheme"),
        @BeanTag(name = "kbootTheme-bean", parent = "Uif-KbootTheme")})
public class ViewTheme extends UifDictionaryBeanBase implements Serializable {
    private static final long serialVersionUID = 7063256242857896580L;
    private static final Logger LOG = Logger.getLogger(ViewTheme.class);

    private String name;

    private String directory;
    private String imageDirectory;

    private String minVersionSuffix;
    private boolean includeMinFiles;
    private String minCssFile;
    private String minScriptFile;
    private List<String> minCssSourceFiles;
    private List<String> minScriptSourceFiles;

    private List<String> cssFiles;
    private List<String> scriptFiles;

    private boolean usesThemeBuilder;

    public ViewTheme() {
        super();

        this.includeMinFiles = true;
        this.minCssSourceFiles = new ArrayList<String>();
        this.minScriptSourceFiles = new ArrayList<String>();

        this.cssFiles = new ArrayList<String>();
        this.scriptFiles = new ArrayList<String>();

        this.usesThemeBuilder = true;
    }

    /**
     * Invoked by View#performApplyModel method to setup defaults for the theme
     *
     * <p>
     * Checks whether we are in dev mode, if so it adds all the CSS and JS files as resources. If
     * {@link #isUsesThemeBuilder()} is true, retrieve the theme-derived.properties file in the theme
     * directory to get the listing of CSS and JS files for theme
     *
     * When not in dev mode, builds the min file name and path for CSS and JS, which is added to
     * the list that is sourced in
     * </p>
     */
    public void configureThemeDefaults() {
        // in development mode, use the min source files directly (for debugging)
        if (inDevMode()) {
            if (this.usesThemeBuilder) {
                setMinFileLists();
            }

            this.cssFiles.addAll(0, this.minCssSourceFiles);
            this.scriptFiles.addAll(0, this.minScriptSourceFiles);
        }
        // when not in development mode and min files are to be sourced in, build the min file
        // names and push to top of css and script file lists
        else if (this.includeMinFiles) {
            if (StringUtils.isBlank(this.minVersionSuffix)) {
                this.minVersionSuffix = getConfigurationService().getPropertyValueAsString(
                        KRADConstants.ConfigParameters.APPLICATION_VERSION);
            }

            String themeDirectory = getThemeDirectory();
            if (StringUtils.isBlank(this.minCssFile)) {
                String minCssFileName = this.name
                        + "."
                        + this.minVersionSuffix
                        + UifConstants.FileExtensions.MIN
                        + UifConstants.FileExtensions.CSS;

                this.minCssFile =
                        themeDirectory + "/" + UifConstants.DEFAULT_STYLESHEETS_DIRECTORY + "/" + minCssFileName;
            }

            if (StringUtils.isBlank(this.minScriptFile)) {
                String minScriptFileName = this.name
                        + "."
                        + this.minVersionSuffix
                        + UifConstants.FileExtensions.MIN
                        + UifConstants.FileExtensions.JS;

                this.minScriptFile =
                        themeDirectory + "/" + UifConstants.DEFAULT_SCRIPTS_DIRECTORY + "/" + minScriptFileName;
            }

            this.cssFiles.add(0, this.minCssFile);
            this.scriptFiles.add(0, this.minScriptFile);
        }
    }

    /**
     * Retrieves the directory associated with the theme
     *
     * <p>
     * If {@link #getDirectory()} is not configured, the theme directory is assumed to be located in the
     * 'themes' folder of the web root. The directory name is assumed to be the name of the theme
     * </p>
     *
     * @return String path to theme directory relative to the web root
     */
    public String getThemeDirectory() {
        String themeDirectory = "";

        if (StringUtils.isNotBlank(this.directory)) {
            if (this.directory.startsWith("/")) {
                this.directory = this.directory.substring(1);
            }

            themeDirectory = this.directory;
        } else {
            themeDirectory = UifConstants.DEFAULT_THEMES_DIRECTORY.substring(1) + "/" + this.name;
        }

        return themeDirectory;
    }

    /**
     * Sets the {@link #getMinScriptSourceFiles()} and {@link #getMinCssSourceFiles()} lists from the
     * corresponding properties in the theme properties file
     */
    protected void setMinFileLists() {
        Properties themeProperties = null;
        try {
            themeProperties = getThemeProperties();
        } catch (IOException e) {
            throw new RuntimeException("Unable to retrieve theme properties for theme: " + this.name);
        }

        if (themeProperties == null) {
            LOG.warn("No theme properties file found for theme with name: " + this.name);

            return;
        }

        String[] cssFiles = getPropertyValue(themeProperties, UifConstants.THEME_CSS_FILES);

        if (cssFiles != null) {
            for (String cssFile : cssFiles) {
                this.minCssSourceFiles.add(cssFile);
            }
        }

        String[] jsFiles = getPropertyValue(themeProperties, UifConstants.THEME_JS_FILES);

        if (jsFiles != null) {
            for (String jsFile : jsFiles) {
                this.minScriptSourceFiles.add(jsFile);
            }
        }
    }

    /**
     * Retrieves the theme properties associated with the theme
     *
     * <p>
     * The theme builder creates a file named {@link org.kuali.rice.krad.uif.UifConstants#THEME_DERIVED_PROPERTY_FILE}
     * located in the theme directory. Here the path is formed and loaded into a properties object
     * </p>
     *
     * @return Properties object containing theme properties, or null if the properties file was not found
     * @throws IOException
     */
    protected Properties getThemeProperties() throws IOException {
        Properties themeProperties = null;

        String appUrl = getConfigurationService().getPropertyValueAsString(
                KRADConstants.ConfigParameters.APPLICATION_URL);
        String propertiesUrlPath = appUrl + "/" + getThemeDirectory() + "/" + UifConstants.THEME_DERIVED_PROPERTY_FILE;

        InputStream inputStream = null;
        try {
            URL propertiesUrl = new URL(propertiesUrlPath);
            inputStream = propertiesUrl.openStream();

            themeProperties = new Properties();
            themeProperties.load(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return themeProperties;
    }

    /**
     * Helper method to retrieve the value of a property from the given Properties object as a
     * string array (string is parsed using comma delimiter)
     *
     * @param properties properties object to pull property value from
     * @param key key for the property to retrieve
     * @return string array parsed from the property value, or null if property was not found or empty
     */
    protected String[] getPropertyValue(Properties properties, String key) {
        String[] propertyValueArray = null;

        if (properties.containsKey(key)) {
            String propertyValueString = properties.getProperty(key);

            if (propertyValueString != null) {
                propertyValueArray = propertyValueString.split(",");
            }
        }

        return propertyValueArray;
    }

    /**
     * Indicates whether operation is in development mode by checking the KRAD configuration parameter
     *
     * @return true if in dev mode, false if not
     */
    protected boolean inDevMode() {
        return getConfigurationService().getPropertyValueAsBoolean(KRADConstants.ConfigParameters.KRAD_DEV_MODE);
    }

    /**
     * A name that identifies the view theme, when using the theme builder this should be the same as
     * the directory (for example, if directory is '/themes/kboot', the theme name will be 'kboot')
     *
     * <p>
     * <b>When using the theme builder (config by convention), the name is required configuration</b>
     * </p>
     *
     * @return name for the theme
     */
    @BeanTagAttribute(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Setter for the theme name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Path to the directory (relative to the web root) that holds the assets for the theme
     *
     * <p>
     * When using the theme builder the directory is not required and will default to '/themes/{name}'
     * </p>
     *
     * @return path to theme directory
     */
    @BeanTagAttribute(name = "directory")
    public String getDirectory() {
        return directory;
    }

    /**
     * Setter for the theme directory path
     *
     * @param directory
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * Path to the directory (relative to the web root) that contains images for the theme
     *
     * <p>
     * Configured directory will populate the {@link org.kuali.rice.krad.uif.UifConstants.ContextVariableNames#THEME_IMAGES}
     * context variable which can be referenced with an expression for an image source
     * </p>
     *
     * <p>
     * When using the theme builder the image directory is not required and will default to a sub directory of the
     * theme directory with name 'images'
     * </p>
     *
     * @return theme image directory
     */
    @BeanTagAttribute(name = "imageDirectory")
    public String getImageDirectory() {
        if (StringUtils.isBlank(this.imageDirectory)) {
            String appUrl = getConfigurationService().getPropertyValueAsString(
                    KRADConstants.ConfigParameters.APPLICATION_URL);

            this.imageDirectory =
                    appUrl + "/" + getThemeDirectory() + "/" + UifConstants.DEFAULT_IMAGES_DIRECTORY + "/";
        }

        return imageDirectory;
    }

    /**
     * Setter for the directory that contains images for the theme
     *
     * @param imageDirectory
     */
    public void setImageDirectory(String imageDirectory) {
        this.imageDirectory = imageDirectory;
    }

    /**
     * When the min file paths are not set, the min file names will be generated using the theme
     * name, version, and the min suffix. This property is set to indicate the version number to use
     *
     * <p>
     * For application themes this can be set to the config parameter ${app.version}
     * </p>
     *
     * @return version string for the min file name
     */
    @BeanTagAttribute(name = "minVersionSuffix")
    public String getMinVersionSuffix() {
        return minVersionSuffix;
    }

    /**
     * Setter for the min file version string
     *
     * @param minVersionSuffix
     */
    public void setMinVersionSuffix(String minVersionSuffix) {
        this.minVersionSuffix = minVersionSuffix;
    }

    /**
     * Indicates the min files should be sourced into the CSS and JS lists when not in development mode (this
     * is regardless of whether theme builder is being used or not)
     *
     * <p>
     * Default is true for including min files
     * </p>
     *
     * @return true if min files should be sourced in, false if not
     */
    public boolean isIncludeMinFiles() {
        return includeMinFiles;
    }

    /**
     * Setter for including min files in the CSS and JS lists
     *
     * @param includeMinFiles
     */
    public void setIncludeMinFiles(boolean includeMinFiles) {
        this.includeMinFiles = includeMinFiles;
    }

    /**
     * File path for the minified CSS file
     *
     * <p>
     * When min file is not set it will be generated by using the theme directory, name, version, and min prefix.
     * This corresponds to the min file names generated by the theme builder
     *
     * For example, with name 'kboot' and version '2.3.0' the min file name will be
     * '/themes/kboot/stylesheets/kboot.2.3.0.min.css'
     * </p>
     *
     * @return path of min css file
     */
    @BeanTagAttribute(name = "minCssFile")
    public String getMinCssFile() {
        return minCssFile;
    }

    /**
     * Setter for the min CSS file path
     *
     * @param minCssFile
     */
    public void setMinCssFile(String minCssFile) {
        this.minCssFile = minCssFile;
    }

    /**
     * File path for the minified JS file
     *
     * <p>
     * When min file is not set it will be generated by using the theme directory, name, version, and min prefix.
     * This corresponds to the min file names generated by the theme builder
     *
     * For example, with name 'kboot' and version '2.3.0' the min file name will be
     * '/themes/kboot/scripts/kboot.2.3.0.min.js'
     * </p>
     *
     * @return path of min css file
     */
    @BeanTagAttribute(name = "minScriptFile")
    public String getMinScriptFile() {
        return minScriptFile;
    }

    /**
     * Setter for the min JS file path
     *
     * @param minScriptFile
     */
    public void setMinScriptFile(String minScriptFile) {
        this.minScriptFile = minScriptFile;
    }

    /**
     * List of file paths (relative to web root) or URLs that make up the minified CSS file
     *
     * <p>
     * In development mode, instead of sourcing in the min CSS file, the list of files specified here will
     * be included. This is to facilitate easier debugging. When using the theme builder this list is automatically
     * retrieved and populated from the theme properties
     * </p>
     *
     * @return list of min CSS file paths or URLs
     */
    @BeanTagAttribute(name = "minCssSourceFiles", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getMinCssSourceFiles() {
        return minCssSourceFiles;
    }

    /**
     * Setter for the min file CSS list
     *
     * @param minCssSourceFiles
     */
    public void setMinCssSourceFiles(List<String> minCssSourceFiles) {
        this.minCssSourceFiles = minCssSourceFiles;
    }

    /**
     * List of file paths (relative to web root) or URLs that make up the minified JS file
     *
     * <p>
     * In development mode, instead of sourcing in the min JS file, the list of files specified here will
     * be included. This is to facilitate easier debugging. When using the theme builder this list is automatically
     * retrieved and populated from the theme properties
     * </p>
     *
     * @return list of min JS file paths or URLs
     */
    @BeanTagAttribute(name = "minScriptSourceFiles", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getMinScriptSourceFiles() {
        return minScriptSourceFiles;
    }

    /**
     * Setter for the min file JS list
     *
     * @param minScriptSourceFiles
     */
    public void setMinScriptSourceFiles(List<String> minScriptSourceFiles) {
        this.minScriptSourceFiles = minScriptSourceFiles;
    }

    /**
     * List of file paths (relative to the web root) or URLs that will be sourced into the view
     * as CSS files
     *
     * <p>
     * Generally this list should be left empty, and the min file lists configured instead (or none with
     * theme builder). However if there are resources that are not part of the minified CSS file that should
     * be included with the theme they can be added here
     *
     * The minified file path (or list of individual files that make up the minification) will be added
     * to the beginning of this list. Therefore any entries explicitly added through configuration will be
     * sourced in last
     * </p>
     *
     * @return list of file paths or URLs for CSS
     */
    @BeanTagAttribute(name = "cssFiles", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getCssFiles() {
        return cssFiles;
    }

    /**
     * Setter for the list of CSS files that should be sourced in along with the minified files
     *
     * @param cssFiles
     */
    public void setCssFiles(List<String> cssFiles) {
        this.cssFiles = cssFiles;
    }

    /**
     * List of file paths (relative to the web root) or URLs that will be sourced into the view
     * as JS files
     *
     * <p>
     * Generally this list should be left empty, and the min file lists configured instead (or none with
     * theme builder). However if there are resources that are not part of the minified JS file that should
     * be included with the theme they can be added here
     *
     * The minified file path (or list of individual files that make up the minification) will be added
     * to the beginning of this list. Therefore any entries explicitly added through configuration will be
     * sourced in last
     * </p>
     *
     * @return list of file paths or URLs for JS
     */
    @BeanTagAttribute(name = "scriptFiles", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getScriptFiles() {
        return scriptFiles;
    }

    /**
     * Setter for the list of JS files that should be sourced in along with the minified files
     *
     * @param scriptFiles
     */
    public void setScriptFiles(List<String> scriptFiles) {
        this.scriptFiles = scriptFiles;
    }

    /**
     * Indicates whether the theme has been built (or will be built) using the theme builder and therefore
     * the theme configuration can be defaulted according to the conventions used by the builder
     *
     * <p>
     * When set to true, only the {@link #getName()} property is required to be configured for the theme. All
     * other configuration will be determined based on convention. When manually configuring the theme, this flag
     * should be turned off (by default this flag is on)
     * </p>
     *
     * @return true if the theme uses the theme builder, false if not
     */
    @BeanTagAttribute(name = "usesThemeBuilder")
    public boolean isUsesThemeBuilder() {
        return usesThemeBuilder;
    }

    /**
     * Setter the indicates whether the theme uses the theme builder
     *
     * @param usesThemeBuilder
     */
    public void setUsesThemeBuilder(boolean usesThemeBuilder) {
        this.usesThemeBuilder = usesThemeBuilder;
    }

    /**
     * Helper method to retrieve an instance of {@link org.kuali.rice.core.api.config.property.ConfigurationService}
     *
     * @return instance of ConfigurationService
     */
    public ConfigurationService getConfigurationService() {
        return CoreApiServiceLocator.getKualiConfigurationService();
    }

    /**
     * Returns a clone of the View Theme.
     *
     * @return ViewTheme instance
     */
    public <T> T copy() {
        T copiedClass = null;
        try {
            copiedClass = (T) this.getClass().newInstance();
        } catch (Exception exception) {
            throw new RuntimeException();
        }

        copyProperties(copiedClass);

        return copiedClass;
    }

    /**
     * Copies the properties over for the copy method.
     *
     * @param viewTheme ViewTheme instance to copy properties to
     */
    protected <T> void copyProperties(T viewTheme) {
        super.copyProperties(viewTheme);

        ViewTheme viewThemeCopy = (ViewTheme) viewTheme;

        viewThemeCopy.setName(this.name);
        viewThemeCopy.setDirectory(this.directory);
        viewThemeCopy.setImageDirectory(this.imageDirectory);
        viewThemeCopy.setMinVersionSuffix(this.minVersionSuffix);
        viewThemeCopy.setIncludeMinFiles(this.includeMinFiles);
        viewThemeCopy.setMinCssFile(this.minCssFile);
        viewThemeCopy.setMinScriptFile(this.minScriptFile);

        if (this.minCssSourceFiles != null) {
            viewThemeCopy.setMinCssSourceFiles(new ArrayList<String>(this.minCssSourceFiles));
        }

        if (this.minScriptSourceFiles != null) {
            viewThemeCopy.setMinScriptSourceFiles(new ArrayList<String>(this.minScriptSourceFiles));
        }

        if (this.cssFiles != null) {
            viewThemeCopy.setCssFiles(new ArrayList<String>(this.cssFiles));
        }

        if (this.scriptFiles != null) {
            viewThemeCopy.setScriptFiles(new ArrayList<String>(this.scriptFiles));
        }

        viewThemeCopy.setUsesThemeBuilder(this.usesThemeBuilder);
    }
}
