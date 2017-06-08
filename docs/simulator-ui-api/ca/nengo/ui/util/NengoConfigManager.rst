.. java:import:: java.io FileInputStream

.. java:import:: java.io FileOutputStream

.. java:import:: java.io IOException

.. java:import:: java.util Properties

.. java:import:: ca.nengo.ui.lib.util Util

NengoConfigManager
==================

.. java:package:: ca.nengo.ui.util
   :noindex:

.. java:type:: public class NengoConfigManager

Fields
------
NENGO_CONFIG_FILE
^^^^^^^^^^^^^^^^^

.. java:field:: public static final String NENGO_CONFIG_FILE
   :outertype: NengoConfigManager

USER_CONFIG_COMMENTS
^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String USER_CONFIG_COMMENTS
   :outertype: NengoConfigManager

USER_CONFIG_FILE
^^^^^^^^^^^^^^^^

.. java:field:: public static final String USER_CONFIG_FILE
   :outertype: NengoConfigManager

Methods
-------
getNengoConfig
^^^^^^^^^^^^^^

.. java:method:: public static Properties getNengoConfig()
   :outertype: NengoConfigManager

getUserBoolean
^^^^^^^^^^^^^^

.. java:method:: public static boolean getUserBoolean(UserProperties property, boolean defaultvalue)
   :outertype: NengoConfigManager

getUserInteger
^^^^^^^^^^^^^^

.. java:method:: public static int getUserInteger(UserProperties property, int defaultvalue)
   :outertype: NengoConfigManager

getUserProperty
^^^^^^^^^^^^^^^

.. java:method:: public static String getUserProperty(UserProperties property)
   :outertype: NengoConfigManager

saveUserConfig
^^^^^^^^^^^^^^

.. java:method:: public static void saveUserConfig()
   :outertype: NengoConfigManager

setUserProperty
^^^^^^^^^^^^^^^

.. java:method:: public static void setUserProperty(UserProperties property, String value)
   :outertype: NengoConfigManager

setUserProperty
^^^^^^^^^^^^^^^

.. java:method:: public static void setUserProperty(UserProperties property, boolean value)
   :outertype: NengoConfigManager

setUserProperty
^^^^^^^^^^^^^^^

.. java:method:: public static void setUserProperty(UserProperties property, int value)
   :outertype: NengoConfigManager

