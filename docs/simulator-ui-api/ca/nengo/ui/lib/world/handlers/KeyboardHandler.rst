.. java:import:: java.awt.event KeyEvent

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.world Destroyable

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

KeyboardHandler
===============

.. java:package:: ca.nengo.ui.lib.world.handlers
   :noindex:

.. java:type:: public class KeyboardHandler extends PBasicInputEventHandler implements Destroyable

   Handles key inputs

   :author: Shu Wu

Constructors
------------
KeyboardHandler
^^^^^^^^^^^^^^^

.. java:constructor:: public KeyboardHandler(WorldImpl world)
   :outertype: KeyboardHandler

Methods
-------
destroy
^^^^^^^

.. java:method:: public void destroy()
   :outertype: KeyboardHandler

keyPressed
^^^^^^^^^^

.. java:method:: @Override public void keyPressed(PInputEvent event)
   :outertype: KeyboardHandler

keyReleased
^^^^^^^^^^^

.. java:method:: @Override public void keyReleased(PInputEvent event)
   :outertype: KeyboardHandler

mousePressed
^^^^^^^^^^^^

.. java:method:: @Override public void mousePressed(PInputEvent event)
   :outertype: KeyboardHandler

