.. java:import:: java.lang.reflect Constructor

.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: java.util List

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchema

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable Property

FnReflective
============

.. java:package:: ca.nengo.ui.configurable.descriptors.functions
   :noindex:

.. java:type:: public class FnReflective extends AbstractFn

   Function instances are created through reflection.

   :author: Shu Wu

Constructors
------------
FnReflective
^^^^^^^^^^^^

.. java:constructor:: public FnReflective(Class<? extends Function> functionClass, String typeName, Property[] properties, String[] getterNames)
   :outertype: FnReflective

   :param functionClass: Type of function to construct
   :param typeName: Friendly name of function
   :param properties: A ordered list of properties which map to the function constructor arguments
   :param getterNames: A ordered list of getter function names which map to the constructor arguments

Methods
-------
createFunction
^^^^^^^^^^^^^^

.. java:method:: @Override protected Function createFunction(ConfigResult props) throws ConfigException
   :outertype: FnReflective

getSchema
^^^^^^^^^

.. java:method:: public ConfigSchema getSchema()
   :outertype: FnReflective

