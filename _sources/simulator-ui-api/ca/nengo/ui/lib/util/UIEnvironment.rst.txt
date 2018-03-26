.. java:import:: ca.nengo.ui.lib AppFrame

UIEnvironment
=============

.. java:package:: ca.nengo.ui.lib.util
   :noindex:

.. java:type:: public class UIEnvironment

   Holds user interface instance variables.

   :author: Shu Wu

Fields
------
ANIMATION_TARGET_FRAME_RATE
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final double ANIMATION_TARGET_FRAME_RATE
   :outertype: UIEnvironment

SEMANTIC_ZOOM_LEVEL
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final double SEMANTIC_ZOOM_LEVEL
   :outertype: UIEnvironment

debugEnabled
^^^^^^^^^^^^

.. java:field:: static boolean debugEnabled
   :outertype: UIEnvironment

Methods
-------
getInstance
^^^^^^^^^^^

.. java:method:: public static AppFrame getInstance()
   :outertype: UIEnvironment

   :return: UI Instance

isDebugEnabled
^^^^^^^^^^^^^^

.. java:method:: public static boolean isDebugEnabled()
   :outertype: UIEnvironment

setDebugEnabled
^^^^^^^^^^^^^^^

.. java:method:: public static void setDebugEnabled(boolean debugEnabled)
   :outertype: UIEnvironment

setInstance
^^^^^^^^^^^

.. java:method:: public static void setInstance(AppFrame instance)
   :outertype: UIEnvironment

   :param instance: UI Instance

