.. java:import:: ca.nengo.ui.lib.exceptions UIException

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

ActionException
===============

.. java:package:: ca.nengo.ui.lib.actions
   :noindex:

.. java:type:: public class ActionException extends UIException

   Exception thrown during an action

   :author: Shu Wu

Constructors
------------
ActionException
^^^^^^^^^^^^^^^

.. java:constructor:: public ActionException(Exception e)
   :outertype: ActionException

ActionException
^^^^^^^^^^^^^^^

.. java:constructor:: public ActionException(String description)
   :outertype: ActionException

ActionException
^^^^^^^^^^^^^^^

.. java:constructor:: public ActionException(String description, Exception e)
   :outertype: ActionException

ActionException
^^^^^^^^^^^^^^^

.. java:constructor:: public ActionException(String description, boolean showWarningPopup)
   :outertype: ActionException

ActionException
^^^^^^^^^^^^^^^

.. java:constructor:: public ActionException(String description, boolean showWarningPopup, Exception targetException)
   :outertype: ActionException

   :param description: Description of the exception
   :param showWarningPopup: If true, a warning should be shown to the user
   :param targetException: Target exception

Methods
-------
defaultHandleBehavior
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void defaultHandleBehavior()
   :outertype: ActionException

getMessage
^^^^^^^^^^

.. java:method:: @Override public String getMessage()
   :outertype: ActionException

getTargetException
^^^^^^^^^^^^^^^^^^

.. java:method:: public Exception getTargetException()
   :outertype: ActionException

   :return: Target Exception. Null, if it dosen't exist

