.. java:import:: java.awt.geom Point2D

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.models UINeoNode

CopyAction
==========

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class CopyAction extends StandardAction

   TODO

   :author: TODO

Constructors
------------
CopyAction
^^^^^^^^^^

.. java:constructor:: public CopyAction(String description, Collection<UINeoNode> nodeUIs)
   :outertype: CopyAction

   TODO

   :param description: TODO
   :param nodeUI: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected final void action() throws ActionException
   :outertype: CopyAction

processNodeUI
^^^^^^^^^^^^^

.. java:method:: protected void processNodeUI(UINeoNode nodeUI)
   :outertype: CopyAction

