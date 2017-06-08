.. java:import:: java.awt Color

.. java:import:: java.awt Cursor

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

AbstractButton
==============

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type:: public abstract class AbstractButton extends WorldObjectImpl

   Button which executes an action on click

   :author: Shu Wu

Constructors
------------
AbstractButton
^^^^^^^^^^^^^^

.. java:constructor:: public AbstractButton(Runnable action)
   :outertype: AbstractButton

   :param action: Action to execute when the button is pressed

Methods
-------
doAction
^^^^^^^^

.. java:method:: protected void doAction()
   :outertype: AbstractButton

getAction
^^^^^^^^^

.. java:method:: public Runnable getAction()
   :outertype: AbstractButton

getDefaultColor
^^^^^^^^^^^^^^^

.. java:method:: public Color getDefaultColor()
   :outertype: AbstractButton

getHighlightColor
^^^^^^^^^^^^^^^^^

.. java:method:: public Color getHighlightColor()
   :outertype: AbstractButton

getSelectedColor
^^^^^^^^^^^^^^^^

.. java:method:: public Color getSelectedColor()
   :outertype: AbstractButton

getState
^^^^^^^^

.. java:method:: protected ButtonState getState()
   :outertype: AbstractButton

setAction
^^^^^^^^^

.. java:method:: public void setAction(Runnable action)
   :outertype: AbstractButton

setButtonState
^^^^^^^^^^^^^^

.. java:method:: public void setButtonState(ButtonState pState)
   :outertype: AbstractButton

setDefaultColor
^^^^^^^^^^^^^^^

.. java:method:: public void setDefaultColor(Color btnDefaultColor)
   :outertype: AbstractButton

setHighlightColor
^^^^^^^^^^^^^^^^^

.. java:method:: public void setHighlightColor(Color btnHighlightColor)
   :outertype: AbstractButton

setSelectedColor
^^^^^^^^^^^^^^^^

.. java:method:: public void setSelectedColor(Color btnSelectedColor)
   :outertype: AbstractButton

stateChanged
^^^^^^^^^^^^

.. java:method:: public abstract void stateChanged()
   :outertype: AbstractButton

