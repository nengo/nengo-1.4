.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: ca.nengo.config Configurable

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config SingleValuedProperty

.. java:import:: ca.nengo.config.impl ConfigurationImpl

.. java:import:: ca.nengo.config.impl ListPropertyImpl

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

MockConfigurable
================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class MockConfigurable implements Configurable

   A dummy Configurable class for testing purposes. TODO: can we get rid of list methods if we don't need listeners?

   :author: Bryan Tripp

Constructors
------------
MockConfigurable
^^^^^^^^^^^^^^^^

.. java:constructor:: public MockConfigurable(Configuration immutableProperties) throws StructuralException
   :outertype: MockConfigurable

Methods
-------
addMultiValuedField
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addMultiValuedField(String val)
   :outertype: MockConfigurable

addMultiValuedField
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addMultiValuedField(int index, String val)
   :outertype: MockConfigurable

getBooleanField
^^^^^^^^^^^^^^^

.. java:method:: public boolean getBooleanField()
   :outertype: MockConfigurable

getConfigurableField
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Configurable getConfigurableField()
   :outertype: MockConfigurable

getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: MockConfigurable

   **See also:** :java:ref:`ca.nengo.config.Configurable.getConfiguration()`

getConstructionTemplate
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static Configuration getConstructionTemplate()
   :outertype: MockConfigurable

getFixedCardinalityField
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String[] getFixedCardinalityField()
   :outertype: MockConfigurable

getFloatArrayArrayField
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public float[][] getFloatArrayArrayField()
   :outertype: MockConfigurable

getFloatArrayField
^^^^^^^^^^^^^^^^^^

.. java:method:: public float[] getFloatArrayField()
   :outertype: MockConfigurable

getFloatField
^^^^^^^^^^^^^

.. java:method:: public float getFloatField()
   :outertype: MockConfigurable

getImmutableField
^^^^^^^^^^^^^^^^^

.. java:method:: public String getImmutableField()
   :outertype: MockConfigurable

getIntField
^^^^^^^^^^^

.. java:method:: public int getIntField()
   :outertype: MockConfigurable

getMultiValuedField
^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<String> getMultiValuedField()
   :outertype: MockConfigurable

getSimulationModeField
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public SimulationMode getSimulationModeField()
   :outertype: MockConfigurable

getStringField
^^^^^^^^^^^^^^

.. java:method:: public String getStringField()
   :outertype: MockConfigurable

getUnitsField
^^^^^^^^^^^^^

.. java:method:: public Units getUnitsField()
   :outertype: MockConfigurable

removeMultiValuedField
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeMultiValuedField(int index)
   :outertype: MockConfigurable

setBooleanField
^^^^^^^^^^^^^^^

.. java:method:: public void setBooleanField(boolean val)
   :outertype: MockConfigurable

setConfigurableField
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setConfigurableField(Configurable val)
   :outertype: MockConfigurable

setFixedCardinalityField
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setFixedCardinalityField(int index, String val)
   :outertype: MockConfigurable

setFloatArrayArrayField
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setFloatArrayArrayField(float[][] val)
   :outertype: MockConfigurable

setFloatArrayField
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setFloatArrayField(float[] val)
   :outertype: MockConfigurable

setFloatField
^^^^^^^^^^^^^

.. java:method:: public void setFloatField(float val)
   :outertype: MockConfigurable

setIntField
^^^^^^^^^^^

.. java:method:: public void setIntField(int val)
   :outertype: MockConfigurable

setMultiValuedField
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setMultiValuedField(int index, String val)
   :outertype: MockConfigurable

setSimulationModeField
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setSimulationModeField(SimulationMode val)
   :outertype: MockConfigurable

setStringField
^^^^^^^^^^^^^^

.. java:method:: public void setStringField(String val)
   :outertype: MockConfigurable

setUnitsField
^^^^^^^^^^^^^

.. java:method:: public void setUnitsField(Units val)
   :outertype: MockConfigurable

