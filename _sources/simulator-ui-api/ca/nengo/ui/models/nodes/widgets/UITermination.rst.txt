.. java:import:: java.awt Color

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.nef.impl DecodedTermination

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.objects.lines ILineTermination

.. java:import:: ca.nengo.ui.lib.objects.lines LineConnector

.. java:import:: ca.nengo.ui.lib.objects.lines LineTerminationIcon

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.icons ModelIcon

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

UITermination
=============

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type:: public abstract class UITermination extends Widget implements ILineTermination

   UI Wrapper for a Termination

   :author: Shu Wu

Constructors
------------
UITermination
^^^^^^^^^^^^^

.. java:constructor:: protected UITermination(UINeoNode nodeParent, Termination term)
   :outertype: UITermination

Methods
-------
connect
^^^^^^^

.. java:method:: protected boolean connect(UIOrigin source, boolean modifyModel)
   :outertype: UITermination

   :param target: Target to be connected with
   :return: true is successfully connected

constructTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructTooltips(TooltipBuilder tooltips)
   :outertype: UITermination

createTerminationUI
^^^^^^^^^^^^^^^^^^^

.. java:method:: public static UITermination createTerminationUI(UINeoNode uiNodeParent, Termination termination)
   :outertype: UITermination

   Factory method for creating a UI Wrapper around a termination

   :param uiNodeParent: UINeoNode to attach the UITermination object to the right parent.
   :param termination:
   :return: UI Termination Wrapper

destroyTerminationModel
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void destroyTerminationModel()
   :outertype: UITermination

   Destroys the termination model

disconnect
^^^^^^^^^^

.. java:method:: public void disconnect()
   :outertype: UITermination

   :param term: Termination to be disconnected from
   :return: True if successful

exposeModel
^^^^^^^^^^^

.. java:method:: @Override protected void exposeModel(UINetwork networkUI, String exposedName)
   :outertype: UITermination

getColor
^^^^^^^^

.. java:method:: public Color getColor()
   :outertype: UITermination

getConnector
^^^^^^^^^^^^

.. java:method:: public UIProjection getConnector()
   :outertype: UITermination

getExposedName
^^^^^^^^^^^^^^

.. java:method:: @Override protected String getExposedName(Network network)
   :outertype: UITermination

getModel
^^^^^^^^

.. java:method:: @Override public Termination getModel()
   :outertype: UITermination

getModelName
^^^^^^^^^^^^

.. java:method:: @Override protected String getModelName()
   :outertype: UITermination

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: UITermination

prepareToDestroyModel
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected final void prepareToDestroyModel()
   :outertype: UITermination

setExposed
^^^^^^^^^^

.. java:method:: @Override public void setExposed(boolean isExposed)
   :outertype: UITermination

   :return: Termination weights matrix

unExpose
^^^^^^^^

.. java:method:: @Override protected void unExpose(Network network)
   :outertype: UITermination

