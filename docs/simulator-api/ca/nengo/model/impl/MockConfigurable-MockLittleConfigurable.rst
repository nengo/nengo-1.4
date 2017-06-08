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

MockConfigurable.MockLittleConfigurable
=======================================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public static class MockLittleConfigurable implements Configurable
   :outertype: MockConfigurable

   A simple dummy Configurable for nesting in MockConfigurable.

   :author: Bryan Tripp

Constructors
------------
MockLittleConfigurable
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public MockLittleConfigurable()
   :outertype: MockConfigurable.MockLittleConfigurable

Methods
-------
getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: MockConfigurable.MockLittleConfigurable

getField
^^^^^^^^

.. java:method:: public String getField()
   :outertype: MockConfigurable.MockLittleConfigurable

setField
^^^^^^^^

.. java:method:: public void setField(String value)
   :outertype: MockConfigurable.MockLittleConfigurable

