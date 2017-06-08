.. java:import:: java.awt Color

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.icons ModelIcon

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

UIOrigin
========

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type:: public abstract class UIOrigin extends Widget

   :author: Shu

Constructors
------------
UIOrigin
^^^^^^^^

.. java:constructor:: protected UIOrigin(UINeoNode nodeParent, Origin origin)
   :outertype: UIOrigin

Methods
-------
connectTo
^^^^^^^^^

.. java:method:: public void connectTo(UITermination term)
   :outertype: UIOrigin

   Connect to a Termination

   :param term: Termination to connect to

connectTo
^^^^^^^^^

.. java:method:: public void connectTo(UITermination term, boolean modifyModel)
   :outertype: UIOrigin

   :param term: Termination to connect to
   :param modifyModel: if true, the Network model will be updated to reflect this connection

constructTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructTooltips(TooltipBuilder tooltips)
   :outertype: UIOrigin

createOriginUI
^^^^^^^^^^^^^^

.. java:method:: public static UIOrigin createOriginUI(UINeoNode uiNodeParent, Origin origin)
   :outertype: UIOrigin

   Factory method for creating a UI Wrapper around a origin

   :param uiNodeParent: UINeoNode to attach the UITermination object to the right parent.
   :param origin:
   :return: UI Origin Wrapper

destroyOriginModel
^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void destroyOriginModel()
   :outertype: UIOrigin

   Destroys the Origin model

exposeModel
^^^^^^^^^^^

.. java:method:: @Override protected void exposeModel(UINetwork networkUI, String exposedName)
   :outertype: UIOrigin

getColor
^^^^^^^^

.. java:method:: public Color getColor()
   :outertype: UIOrigin

getExposedName
^^^^^^^^^^^^^^

.. java:method:: @Override protected String getExposedName(Network network)
   :outertype: UIOrigin

getModel
^^^^^^^^

.. java:method:: @Override public Origin getModel()
   :outertype: UIOrigin

getModelName
^^^^^^^^^^^^

.. java:method:: @Override protected String getModelName()
   :outertype: UIOrigin

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: UIOrigin

prepareToDestroyModel
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected final void prepareToDestroyModel()
   :outertype: UIOrigin

setExposed
^^^^^^^^^^

.. java:method:: @Override public void setExposed(boolean isExposed)
   :outertype: UIOrigin

setVisible
^^^^^^^^^^

.. java:method:: @Override public void setVisible(boolean isVisible)
   :outertype: UIOrigin

unExpose
^^^^^^^^

.. java:method:: @Override protected void unExpose(Network network)
   :outertype: UIOrigin

