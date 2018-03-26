Environment
===========

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public abstract class Environment

   Provides information about the context in which the code is running.

   :author: Bryan Tripp

Fields
------
USER_INTERFACE
^^^^^^^^^^^^^^

.. java:field:: public static String USER_INTERFACE
   :outertype: Environment

   Name of system property underlying inUserInterface()

WORKING_DIRECTORY
^^^^^^^^^^^^^^^^^

.. java:field:: public static String WORKING_DIRECTORY
   :outertype: Environment

   Name of String property that contains path of user's working directory

Methods
-------
inUserInterface
^^^^^^^^^^^^^^^

.. java:method:: public static boolean inUserInterface()
   :outertype: Environment

   :return: True if the system is running within a user interface (default is false; can be configured with system property "user-interface" = "true")

setUserInterface
^^^^^^^^^^^^^^^^

.. java:method:: public static void setUserInterface(boolean inUI)
   :outertype: Environment

