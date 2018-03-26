IConfigurable
=============

.. java:package:: ca.nengo.ui.configurable
   :noindex:

.. java:type:: public interface IConfigurable

   Describes a object which can be configured by a IConfigurationManager

   :author: Shu Wu

Methods
-------
completeConfiguration
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void completeConfiguration(ConfigResult props) throws ConfigException
   :outertype: IConfigurable

   Called when configuration parameters have been set

   :param props: A set of properties
   :throws ConfigException: Exception thrown if there is an error during pre-configuration.

getDescription
^^^^^^^^^^^^^^

.. java:method:: public String getDescription()
   :outertype: IConfigurable

   :return: Name given to this type of object

getExtendedDescription
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getExtendedDescription()
   :outertype: IConfigurable

   :return: an html-formatted extended description/instructions of what will be created

getSchema
^^^^^^^^^

.. java:method:: public ConfigSchema getSchema()
   :outertype: IConfigurable

   :return: An array of objects which describe what needs to be configured in this object

getTypeName
^^^^^^^^^^^

.. java:method:: public String getTypeName()
   :outertype: IConfigurable

   :return: Name given to this type of object

preConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public void preConfiguration(ConfigResult props) throws ConfigException
   :outertype: IConfigurable

   Called before full configuration to initialize and find errors.

   :param props: A set of properties
   :throws ConfigException: Exception thrown if there is an error during pre-configuration.

