.. java:import:: java.awt Component

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing JScrollPane

.. java:import:: javax.swing JTextArea

.. java:import:: ca.nengo.ui.lib.exceptions UIException

UserMessages
============

.. java:package:: ca.nengo.ui.lib.util
   :noindex:

.. java:type:: public class UserMessages

   Displays messages to the user through a popup dialog.

   :author: Shu Wu

Methods
-------
askDialog
^^^^^^^^^

.. java:method:: public static String askDialog(String dialogMessage) throws DialogException
   :outertype: UserMessages

showDialog
^^^^^^^^^^

.. java:method:: public static void showDialog(String title, String msg)
   :outertype: UserMessages

showDialog
^^^^^^^^^^

.. java:method:: public static void showDialog(String title, String msg, Component parent)
   :outertype: UserMessages

showError
^^^^^^^^^

.. java:method:: public static void showError(String msg)
   :outertype: UserMessages

showError
^^^^^^^^^

.. java:method:: public static void showError(String msg, Component parent)
   :outertype: UserMessages

showTextDialog
^^^^^^^^^^^^^^

.. java:method:: public static void showTextDialog(String title, String msg)
   :outertype: UserMessages

showTextDialog
^^^^^^^^^^^^^^

.. java:method:: public static void showTextDialog(String title, String msg, int messageType)
   :outertype: UserMessages

showWarning
^^^^^^^^^^^

.. java:method:: public static void showWarning(String msg)
   :outertype: UserMessages

showWarning
^^^^^^^^^^^

.. java:method:: public static void showWarning(String msg, Component parent)
   :outertype: UserMessages

