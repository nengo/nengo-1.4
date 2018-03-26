.. java:import:: java.awt Color

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.icons ProbeIcon

UIProbe
=======

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type:: public abstract class UIProbe extends ModelObject

Constructors
------------
UIProbe
^^^^^^^

.. java:constructor:: public UIProbe(UINeoNode nodeAttachedTo, Object probeModel)
   :outertype: UIProbe

Methods
-------
dragOffset
^^^^^^^^^^

.. java:method:: @Override public void dragOffset(double dx, double dy)
   :outertype: UIProbe

getProbeParent
^^^^^^^^^^^^^^

.. java:method:: public UINeoNode getProbeParent()
   :outertype: UIProbe

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public abstract String getTypeName()
   :outertype: UIProbe

prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareForDestroy()
   :outertype: UIProbe

setProbeColor
^^^^^^^^^^^^^

.. java:method:: public void setProbeColor(Color color)
   :outertype: UIProbe

