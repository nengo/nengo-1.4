.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions ReversableAction

.. java:import:: ca.nengo.ui.lib.util.menus AbstractMenuBuilder

.. java:import:: ca.nengo.ui.models.icons EnsembleIcon

.. java:import:: ca.nengo.ui.models.nodes.widgets UISpikeProbe

.. java:import:: ca.nengo.ui.models.viewers EnsembleViewer

.. java:import:: ca.nengo.ui.models.viewers NodeViewer

UIEnsemble
==========

.. java:package:: ca.nengo.ui.models.nodes
   :noindex:

.. java:type:: public class UIEnsemble extends UINodeViewable

   UI Wrapper for an Ensemble

   :author: Shu

Constructors
------------
UIEnsemble
^^^^^^^^^^

.. java:constructor:: public UIEnsemble(Ensemble model)
   :outertype: UIEnsemble

Methods
-------
collectSpikes
^^^^^^^^^^^^^

.. java:method:: public void collectSpikes(boolean collect)
   :outertype: UIEnsemble

constructDataCollectionMenu
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructDataCollectionMenu(AbstractMenuBuilder menu)
   :outertype: UIEnsemble

createViewerInstance
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected NodeViewer createViewerInstance()
   :outertype: UIEnsemble

getDimensionality
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getDimensionality()
   :outertype: UIEnsemble

getModel
^^^^^^^^

.. java:method:: @Override public Ensemble getModel()
   :outertype: UIEnsemble

getNodesCount
^^^^^^^^^^^^^

.. java:method:: @Override public int getNodesCount()
   :outertype: UIEnsemble

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: UIEnsemble

modelUpdated
^^^^^^^^^^^^

.. java:method:: @Override protected void modelUpdated()
   :outertype: UIEnsemble

saveContainerConfig
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void saveContainerConfig()
   :outertype: UIEnsemble

