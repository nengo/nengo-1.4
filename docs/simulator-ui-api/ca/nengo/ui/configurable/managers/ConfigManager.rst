.. java:import:: java.awt Container

.. java:import:: java.io File

.. java:import:: java.io FileInputStream

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io FileOutputStream

.. java:import:: java.io FilenameFilter

.. java:import:: java.io IOException

.. java:import:: java.io InvalidClassException

.. java:import:: java.io NotSerializableException

.. java:import:: java.io ObjectInputStream

.. java:import:: java.io ObjectOutputStream

.. java:import:: javax.swing.text MutableAttributeSet

.. java:import:: javax.swing.text SimpleAttributeSet

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchema

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable IConfigurable

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

ConfigManager
=============

.. java:package:: ca.nengo.ui.configurable.managers
   :noindex:

.. java:type:: public abstract class ConfigManager

   Configuration Manager used to configure IConfigurable objects

   :author: Shu Wu

Fields
------
DEV_DIST_DIR
^^^^^^^^^^^^

.. java:field:: static final String DEV_DIST_DIR
   :outertype: ConfigManager

SAVED_CONFIG_DIR
^^^^^^^^^^^^^^^^

.. java:field:: static final String SAVED_CONFIG_DIR
   :outertype: ConfigManager

   Name of directory where to store saved configuration

Constructors
------------
ConfigManager
^^^^^^^^^^^^^

.. java:constructor:: public ConfigManager(IConfigurable configurable)
   :outertype: ConfigManager

   :param configurable: Object to be configured

Methods
-------
configure
^^^^^^^^^

.. java:method:: public static Object configure(Property prop, String typeName, Container parent) throws ConfigException
   :outertype: ConfigManager

   :param prop: TODO
   :param typeName: TODO
   :param parent: TODO
   :throws ConfigException: TODO
   :return: TODO

configure
^^^^^^^^^

.. java:method:: public static ConfigResult configure(Property[] schema, String typeName, Container parent, ConfigMode configMode) throws ConfigException
   :outertype: ConfigManager

   Convenient function to automatically wrap the PropertyDescriptors with a default Config schema

   :param schema: TODO
   :param typeName: TODO
   :param parent: TODO
   :param configMode: TODO
   :throws ConfigException: TODO
   :return: TODO

configure
^^^^^^^^^

.. java:method:: public static ConfigResult configure(ConfigSchema schema, String typeName, String description, Container parent, ConfigMode configMode) throws ConfigException
   :outertype: ConfigManager

   :param schema: TODO
   :param typeName: TODO
   :param description: TODO
   :param parent: TODO
   :param configMode: TODO
   :throws ConfigException: TODO
   :return: TODO

configureAndWait
^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void configureAndWait() throws ConfigException
   :outertype: ConfigManager

   Configures the IConfigurable object and waits until the configuration finishes

deletePropertiesFile
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void deletePropertiesFile(String name)
   :outertype: ConfigManager

   :param name: filename prefix

getConfigurable
^^^^^^^^^^^^^^^

.. java:method:: protected IConfigurable getConfigurable()
   :outertype: ConfigManager

   :return: Object to be configured

getFileNamePrefix
^^^^^^^^^^^^^^^^^

.. java:method:: protected static String getFileNamePrefix(IConfigurable obj)
   :outertype: ConfigManager

getProperties
^^^^^^^^^^^^^

.. java:method:: protected MutableAttributeSet getProperties()
   :outertype: ConfigManager

   :return: Set of properties to be set during the configuration process

getProperty
^^^^^^^^^^^

.. java:method:: protected Object getProperty(String name)
   :outertype: ConfigManager

   :param name: Name of property
   :return: Value of property

getPropertyFiles
^^^^^^^^^^^^^^^^

.. java:method:: protected String[] getPropertyFiles()
   :outertype: ConfigManager

   :return: List of fileNames which point to saved configuration files

loadPropertiesFromFile
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void loadPropertiesFromFile(String name)
   :outertype: ConfigManager

   :param name: Name of the properties set to be loaded

savePropertiesFile
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void savePropertiesFile(String name)
   :outertype: ConfigManager

   :param name: name of the properties set to be saved

setProperty
^^^^^^^^^^^

.. java:method:: protected void setProperty(String name, Object value)
   :outertype: ConfigManager

