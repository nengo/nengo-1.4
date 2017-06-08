.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable IConfigurable

FileConfigurer
==============

.. java:package:: ca.nengo.ui.configurable.managers
   :noindex:

.. java:type:: public class FileConfigurer extends ConfigManager

   Configuration manager which loads configuration from a saved file

   :author: Shu Wu

Constructors
------------
FileConfigurer
^^^^^^^^^^^^^^

.. java:constructor:: public FileConfigurer(IConfigurable configurable)
   :outertype: FileConfigurer

   Configures an object using default properties

   :param configurable: Object to be configured

FileConfigurer
^^^^^^^^^^^^^^

.. java:constructor:: public FileConfigurer(IConfigurable configurable, String configFileName)
   :outertype: FileConfigurer

   :param configurable: Object to be configured
   :param configFileName: Name of configuration to use

Methods
-------
configureAndWait
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void configureAndWait() throws ConfigException
   :outertype: FileConfigurer

