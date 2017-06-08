.. java:import:: java.awt Dialog

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable IConfigurable

.. java:import:: ca.nengo.ui.configurable.managers UserConfigurer

.. java:import:: ca.nengo.ui.lib.util UserMessages

AbstractFn
==========

.. java:package:: ca.nengo.ui.configurable.descriptors.functions
   :noindex:

.. java:type:: public abstract class AbstractFn implements IConfigurable, ConfigurableFunction

   Describes how to configure a function through the IConfigurable interface.

   :author: Shu Wu

Constructors
------------
AbstractFn
^^^^^^^^^^

.. java:constructor:: public AbstractFn(String typeName, Class<? extends Function> functionType)
   :outertype: AbstractFn

   :param typeName:
   :param functionType:

Methods
-------
completeConfiguration
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void completeConfiguration(ConfigResult props) throws ConfigException
   :outertype: AbstractFn

configureFunction
^^^^^^^^^^^^^^^^^

.. java:method:: public Function configureFunction(Dialog parent)
   :outertype: AbstractFn

createFunction
^^^^^^^^^^^^^^

.. java:method:: protected abstract Function createFunction(ConfigResult props) throws ConfigException
   :outertype: AbstractFn

getDescription
^^^^^^^^^^^^^^

.. java:method:: public String getDescription()
   :outertype: AbstractFn

getExtendedDescription
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getExtendedDescription()
   :outertype: AbstractFn

getFunction
^^^^^^^^^^^

.. java:method:: public Function getFunction()
   :outertype: AbstractFn

   :return: The function created

getFunctionType
^^^^^^^^^^^^^^^

.. java:method:: public Class<? extends Function> getFunctionType()
   :outertype: AbstractFn

getTypeName
^^^^^^^^^^^

.. java:method:: public String getTypeName()
   :outertype: AbstractFn

preConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public void preConfiguration(ConfigResult props) throws ConfigException
   :outertype: AbstractFn

setFunction
^^^^^^^^^^^

.. java:method:: public final void setFunction(Function function)
   :outertype: AbstractFn

   :param function: function wrapper

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: AbstractFn

