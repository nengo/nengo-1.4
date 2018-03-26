.. java:import:: ca.nengo.ui.lib.objects.lines ILineTermination

.. java:import:: ca.nengo.ui.lib.objects.lines LineConnector

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXEdge

.. java:import:: ca.nengo.ui.models UINeoNode

UIProjection
============

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type:: public class UIProjection extends LineConnector

   Line Ends for this origin

   :author: Shu Wu

Constructors
------------
UIProjection
^^^^^^^^^^^^

.. java:constructor:: public UIProjection(UIProjectionWell well)
   :outertype: UIProjection

Methods
-------
disconnectFromTermination
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void disconnectFromTermination()
   :outertype: UIProjection

getOriginUI
^^^^^^^^^^^

.. java:method:: public UIOrigin getOriginUI()
   :outertype: UIProjection

getTermination
^^^^^^^^^^^^^^

.. java:method:: @Override public UITermination getTermination()
   :outertype: UIProjection

initTarget
^^^^^^^^^^

.. java:method:: @Override protected boolean initTarget(ILineTermination target, boolean modifyModel)
   :outertype: UIProjection

