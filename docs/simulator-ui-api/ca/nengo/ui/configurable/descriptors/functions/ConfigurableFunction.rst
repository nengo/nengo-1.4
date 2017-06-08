.. java:import:: java.awt Dialog

.. java:import:: ca.nengo.math Function

ConfigurableFunction
====================

.. java:package:: ca.nengo.ui.configurable.descriptors.functions
   :noindex:

.. java:type:: public interface ConfigurableFunction

   TODO

   :author: TODO

Methods
-------
configureFunction
^^^^^^^^^^^^^^^^^

.. java:method:: public Function configureFunction(Dialog parent)
   :outertype: ConfigurableFunction

   Configures the function

   :param parent: Component to attach the configuration dialog to
   :return: Configured function

getFunction
^^^^^^^^^^^

.. java:method:: public Function getFunction()
   :outertype: ConfigurableFunction

   :return: TODO

getFunctionType
^^^^^^^^^^^^^^^

.. java:method:: public Class<? extends Function> getFunctionType()
   :outertype: ConfigurableFunction

   :return: Type of function to be created

setFunction
^^^^^^^^^^^

.. java:method:: public void setFunction(Function function)
   :outertype: ConfigurableFunction

   This method is optional

   :param function: Function to be configured.

