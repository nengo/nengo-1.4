.. java:import:: java.util Enumeration

.. java:import:: java.util HashMap

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.nodes UIEnsemble

.. java:import:: ca.nengo.ui.models.nodes UINeuron

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.util Probe

EnsembleViewer
==============

.. java:package:: ca.nengo.ui.models.viewers
   :noindex:

.. java:type:: public class EnsembleViewer extends NodeViewer

   Viewer for peeking into an Ensemble

   :author: Shu

Constructors
------------
EnsembleViewer
^^^^^^^^^^^^^^

.. java:constructor:: public EnsembleViewer(UIEnsemble ensembleUI)
   :outertype: EnsembleViewer

   :param ensembleUI: Parent Ensemble UI Wrapper

Methods
-------
applyDefaultLayout
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void applyDefaultLayout()
   :outertype: EnsembleViewer

canRemoveChildModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean canRemoveChildModel(Node node)
   :outertype: EnsembleViewer

getModel
^^^^^^^^

.. java:method:: @Override public Ensemble getModel()
   :outertype: EnsembleViewer

getViewerParent
^^^^^^^^^^^^^^^

.. java:method:: @Override public UIEnsemble getViewerParent()
   :outertype: EnsembleViewer

removeChildModel
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void removeChildModel(Node node)
   :outertype: EnsembleViewer

updateViewFromModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public synchronized void updateViewFromModel(boolean isFirstUpdate)
   :outertype: EnsembleViewer

