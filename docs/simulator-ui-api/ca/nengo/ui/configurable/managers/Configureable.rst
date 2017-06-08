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

Configureable
=============

.. java:package:: ca.nengo.ui.configurable.managers
   :noindex:

.. java:type::  class Configureable implements IConfigurable

Constructors
------------
Configureable
^^^^^^^^^^^^^

.. java:constructor:: public Configureable(ConfigSchema configSchema, String typeName, String description)
   :outertype: Configureable

Methods
-------
completeConfiguration
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void completeConfiguration(ConfigResult props) throws ConfigException
   :outertype: Configureable

getDescription
^^^^^^^^^^^^^^

.. java:method:: public String getDescription()
   :outertype: Configureable

getExtendedDescription
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getExtendedDescription()
   :outertype: Configureable

getProperties
^^^^^^^^^^^^^

.. java:method:: public ConfigResult getProperties()
   :outertype: Configureable

getSchema
^^^^^^^^^

.. java:method:: public ConfigSchema getSchema()
   :outertype: Configureable

getTypeName
^^^^^^^^^^^

.. java:method:: public String getTypeName()
   :outertype: Configureable

preConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public void preConfiguration(ConfigResult props) throws ConfigException
   :outertype: Configureable

