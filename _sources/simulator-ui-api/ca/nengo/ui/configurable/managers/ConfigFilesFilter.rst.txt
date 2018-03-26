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

ConfigFilesFilter
=================

.. java:package:: ca.nengo.ui.configurable.managers
   :noindex:

.. java:type::  class ConfigFilesFilter implements FilenameFilter

   Filters files needed by ConfigManager

   :author: Shu

Fields
------
parent
^^^^^^

.. java:field::  IConfigurable parent
   :outertype: ConfigFilesFilter

Constructors
------------
ConfigFilesFilter
^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigFilesFilter(IConfigurable parent)
   :outertype: ConfigFilesFilter

Methods
-------
accept
^^^^^^

.. java:method:: public boolean accept(File file, String name)
   :outertype: ConfigFilesFilter

