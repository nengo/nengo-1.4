.. java:import:: ca.nengo.ui.configurable.descriptors PBoolean

.. java:import:: ca.nengo.ui.configurable.descriptors PFloat

.. java:import:: ca.nengo.ui.configurable.descriptors PInt

.. java:import:: ca.nengo.ui.configurable.descriptors PString

.. java:import:: ca.nengo.ui.configurable.managers UserConfigurer

UserDialogs
===========

.. java:package:: ca.nengo.ui.configurable
   :noindex:

.. java:type:: public class UserDialogs

   Creates various dialogs and returns user results

   :author: Shu Wu

Methods
-------
showDialog
^^^^^^^^^^

.. java:method:: public static Object showDialog(String dialogName, Property descriptor) throws ConfigException
   :outertype: UserDialogs

   :param dialogName: TODO
   :param descriptor: TODO
   :throws ConfigException: TODO
   :return: TODO

showDialog
^^^^^^^^^^

.. java:method:: public static ConfigResult showDialog(String dialogName, Property[] descriptors) throws ConfigException
   :outertype: UserDialogs

   :param dialogName: TODO
   :param descriptors: TODO
   :throws ConfigException: TODO
   :return: TODO

showDialogBoolean
^^^^^^^^^^^^^^^^^

.. java:method:: public static Boolean showDialogBoolean(String dialogName, Boolean defaultValue) throws ConfigException
   :outertype: UserDialogs

   :param dialogName: TODO
   :param defaultValue: TODO
   :throws ConfigException: TODO
   :return: TODO

showDialogFloat
^^^^^^^^^^^^^^^

.. java:method:: public static Float showDialogFloat(String dialogName, Float defaultValue) throws ConfigException
   :outertype: UserDialogs

   :param dialogName: TODO
   :param defaultValue: TODO
   :throws ConfigException: TODO
   :return: TODO

showDialogInteger
^^^^^^^^^^^^^^^^^

.. java:method:: public static Integer showDialogInteger(String dialogName, int defaultValue) throws ConfigException
   :outertype: UserDialogs

   :param dialogName: TODO
   :param defaultValue: TODO
   :throws ConfigException: TODO
   :return: TODO

showDialogString
^^^^^^^^^^^^^^^^

.. java:method:: public static String showDialogString(String dialogName, String defaultValue) throws ConfigException
   :outertype: UserDialogs

   :param dialogName: TODO
   :param defaultValue: TODO
   :throws ConfigException: TODO
   :return: TODO

