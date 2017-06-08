.. java:import:: java.awt.geom Point2D

.. java:import:: java.util ArrayList

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.models NodeContainer

.. java:import:: ca.nengo.ui.models NodeContainer.ContainerException

.. java:import:: ca.nengo.ui.util NengoClipboard

PasteAction
===========

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class PasteAction extends StandardAction

   TODO

   :author: TODO

Constructors
------------
PasteAction
^^^^^^^^^^^

.. java:constructor:: public PasteAction(String description, NodeContainer nodeContainer, boolean fromTopMenu)
   :outertype: PasteAction

   :param description: TODO
   :param nodeContainer: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: PasteAction

setPosition
^^^^^^^^^^^

.. java:method:: public void setPosition(Double x, Double y)
   :outertype: PasteAction

