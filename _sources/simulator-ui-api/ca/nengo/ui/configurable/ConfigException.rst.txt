.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.ui.lib.exceptions UIException

.. java:import:: ca.nengo.ui.lib.util UserMessages

ConfigException
===============

.. java:package:: ca.nengo.ui.configurable
   :noindex:

.. java:type:: public class ConfigException extends UIException

   Exception to be thrown is if there is an error during Configuration

   :author: Shu Wu

Constructors
------------
ConfigException
^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigException(String message)
   :outertype: ConfigException

   :param message: Message describing the exception

ConfigException
^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigException(String message, boolean isRecoverable)
   :outertype: ConfigException

   :param message: Message describing the exception
   :param isRecoverable: if true, the Configurer will try to recover from the exception

Methods
-------
defaultHandleBehavior
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void defaultHandleBehavior()
   :outertype: ConfigException

isRecoverable
^^^^^^^^^^^^^

.. java:method:: public boolean isRecoverable()
   :outertype: ConfigException

   :return: TODO

