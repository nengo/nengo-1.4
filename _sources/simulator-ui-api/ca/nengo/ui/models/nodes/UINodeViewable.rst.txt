.. java:import:: java.io File

.. java:import:: java.io IOException

.. java:import:: java.lang.ref WeakReference

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.ui.brainView BrainViewer

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window.WindowState

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

.. java:import:: ca.nengo.ui.models.viewers NodeViewer

UINodeViewable
==============

.. java:package:: ca.nengo.ui.models.nodes
   :noindex:

.. java:type:: public abstract class UINodeViewable extends UINeoNode

   UI Wrapper for Node Containers such as Ensembles and Networks.

   :author: Shu

Constructors
------------
UINodeViewable
^^^^^^^^^^^^^^

.. java:constructor:: public UINodeViewable(Node model)
   :outertype: UINodeViewable

Methods
-------
closeViewer
^^^^^^^^^^^

.. java:method:: public void closeViewer()
   :outertype: UINodeViewable

   Closes the viewer Window

constructTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructTooltips(TooltipBuilder tooltips)
   :outertype: UINodeViewable

createBrainViewer
^^^^^^^^^^^^^^^^^

.. java:method:: public void createBrainViewer()
   :outertype: UINodeViewable

   Opens a new instance of Brain View

createViewerInstance
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract NodeViewer createViewerInstance()
   :outertype: UINodeViewable

   Creates a new Viewer

   :return: Viewer created

detachViewFromModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void detachViewFromModel()
   :outertype: UINodeViewable

doubleClicked
^^^^^^^^^^^^^

.. java:method:: @Override public void doubleClicked()
   :outertype: UINodeViewable

getDimensionality
^^^^^^^^^^^^^^^^^

.. java:method:: public abstract int getDimensionality()
   :outertype: UINodeViewable

   :return: Number of dimensions in this population contained by the Model

getNodesCount
^^^^^^^^^^^^^

.. java:method:: public abstract int getNodesCount()
   :outertype: UINodeViewable

   :return: Number of nodes contained by the Model

getViewer
^^^^^^^^^

.. java:method:: public NodeViewer getViewer()
   :outertype: UINodeViewable

   :return: Container Viewer

getViewerWindow
^^^^^^^^^^^^^^^

.. java:method:: protected Window getViewerWindow()
   :outertype: UINodeViewable

   :return: Viewer Window

initialize
^^^^^^^^^^

.. java:method:: @Override protected void initialize()
   :outertype: UINodeViewable

isViewerWindowVisible
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isViewerWindowVisible()
   :outertype: UINodeViewable

moveViewerWindowToFront
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void moveViewerWindowToFront()
   :outertype: UINodeViewable

openViewer
^^^^^^^^^^

.. java:method:: public NodeViewer openViewer()
   :outertype: UINodeViewable

   Opens the Container Viewer

   :return: the Container viewer

prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareForDestroy()
   :outertype: UINodeViewable

saveContainerConfig
^^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract void saveContainerConfig()
   :outertype: UINodeViewable

   Saves the configuration of this node container

saveModel
^^^^^^^^^

.. java:method:: @Override public void saveModel(File file) throws IOException
   :outertype: UINodeViewable

