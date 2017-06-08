.. java:import:: java.awt Color

.. java:import:: java.awt.event MouseEvent

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.util PPickPath

LineWell
========

.. java:package:: ca.nengo.ui.lib.objects.lines
   :noindex:

.. java:type:: public abstract class LineWell extends WorldObjectImpl

Constructors
------------
LineWell
^^^^^^^^

.. java:constructor:: public LineWell()
   :outertype: LineWell

Methods
-------
createProjection
^^^^^^^^^^^^^^^^

.. java:method:: protected LineConnector createProjection()
   :outertype: LineWell

   :return: The new LineEnd which has been created and added to the LineEndWell

getColor
^^^^^^^^

.. java:method:: public Color getColor()
   :outertype: LineWell

setColor
^^^^^^^^

.. java:method:: public void setColor(Color color)
   :outertype: LineWell

