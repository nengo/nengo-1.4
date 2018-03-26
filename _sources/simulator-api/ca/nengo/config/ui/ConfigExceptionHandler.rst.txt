.. java:import:: java.awt Component

.. java:import:: javax.swing JOptionPane

.. java:import:: org.apache.log4j Logger

ConfigExceptionHandler
======================

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public class ConfigExceptionHandler

   Handles UI-generated exceptions consistently.

   :author: Bryan Tripp

Fields
------
DEFAULT_BUG_MESSAGE
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static String DEFAULT_BUG_MESSAGE
   :outertype: ConfigExceptionHandler

   Show this message if a better one isn't defined

Methods
-------
handle
^^^^^^

.. java:method:: public static void handle(Exception e, String userMessage, Component parentComponent)
   :outertype: ConfigExceptionHandler

   :param e: Exeption to handle
   :param userMessage: A message that can be shown to the user
   :param parentComponent: UI component to which exception is related (can be null)

