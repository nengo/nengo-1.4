.. java:import:: java.awt Font

.. java:import:: java.awt Paint

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Path

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Text

TextButton
==========

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type:: public class TextButton extends AbstractButton

   A Button whose represntation is a text label

   :author: Shu Wu

Constructors
------------
TextButton
^^^^^^^^^^

.. java:constructor:: public TextButton(String textLabel, Runnable action)
   :outertype: TextButton

   :param textLabel: Button label
   :param action: Action to execute when the button is pressed

Methods
-------
getFrame
^^^^^^^^

.. java:method:: public Path getFrame()
   :outertype: TextButton

getText
^^^^^^^

.. java:method:: public Text getText()
   :outertype: TextButton

setFont
^^^^^^^

.. java:method:: public void setFont(Font font)
   :outertype: TextButton

setStrokePaint
^^^^^^^^^^^^^^

.. java:method:: public void setStrokePaint(Paint paint)
   :outertype: TextButton

setText
^^^^^^^

.. java:method:: public void setText(String textLabel)
   :outertype: TextButton

stateChanged
^^^^^^^^^^^^

.. java:method:: @Override public void stateChanged()
   :outertype: TextButton

updateBounds
^^^^^^^^^^^^

.. java:method:: public void updateBounds()
   :outertype: TextButton

