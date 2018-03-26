.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Container

.. java:import:: java.awt Dimension

.. java:import:: java.awt.event ComponentEvent

.. java:import:: java.awt.event ComponentListener

.. java:import:: java.awt.event FocusAdapter

.. java:import:: java.awt.event FocusEvent

.. java:import:: java.awt.event MouseEvent

.. java:import:: java.awt.event MouseListener

.. java:import:: javax.swing BorderFactory

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JSplitPane

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

AuxillarySplitPane
==================

.. java:package:: ca.nengo.ui.lib
   :noindex:

.. java:type:: public class AuxillarySplitPane extends JSplitPane

   Customized split pane implementation which holds main and an auxillary Container which can be hidden /shown.

   :author: Shu Wu

Fields
------
MINIMUM_HEIGHT
^^^^^^^^^^^^^^

.. java:field:: public static final int MINIMUM_HEIGHT
   :outertype: AuxillarySplitPane

MINIMUM_WIDTH
^^^^^^^^^^^^^

.. java:field:: public static final int MINIMUM_WIDTH
   :outertype: AuxillarySplitPane

Constructors
------------
AuxillarySplitPane
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AuxillarySplitPane(Container mainPanel, Container auxPanel, String auxTitle, Orientation orientation)
   :outertype: AuxillarySplitPane

AuxillarySplitPane
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AuxillarySplitPane(Container mainPanel, Container auxPanel, String auxTitle, Orientation orientation, Dimension minsize, boolean showTitle)
   :outertype: AuxillarySplitPane

   :param mainPanel: TODO
   :param auxPanel: TODO
   :param auxTitle: TODO
   :param orientation: TODO

Methods
-------
getAuxPaneWrapper
^^^^^^^^^^^^^^^^^

.. java:method:: public JPanel getAuxPaneWrapper()
   :outertype: AuxillarySplitPane

   Get the panel holding the auxillary pane

getAuxTitle
^^^^^^^^^^^

.. java:method:: public String getAuxTitle()
   :outertype: AuxillarySplitPane

   Get the auxillary panel title

isAuxVisible
^^^^^^^^^^^^

.. java:method:: public boolean isAuxVisible()
   :outertype: AuxillarySplitPane

   Is the auxillary panel visible

isResizable
^^^^^^^^^^^

.. java:method:: public boolean isResizable()
   :outertype: AuxillarySplitPane

   Is the auxillary panel resizable

setAuxPane
^^^^^^^^^^

.. java:method:: public void setAuxPane(Container auxPane, String title)
   :outertype: AuxillarySplitPane

   :param auxPane: TODO
   :param title: TODO

setAuxPanelSize
^^^^^^^^^^^^^^^

.. java:method:: public void setAuxPanelSize(int size)
   :outertype: AuxillarySplitPane

setAuxVisible
^^^^^^^^^^^^^

.. java:method:: public void setAuxVisible(boolean isVisible)
   :outertype: AuxillarySplitPane

   Set whether the auxillary panel is visible

setAuxVisible
^^^^^^^^^^^^^

.. java:method:: public void setAuxVisible(boolean isVisible, boolean resetDividerLocation)
   :outertype: AuxillarySplitPane

   Set whether the auxillary panel is visible

   :param isVisible: Whether the panel should be made visible
   :param resetDividerLocation: Whether to reset the panel size to the default minimum

setDividerLocation
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setDividerLocation(int location)
   :outertype: AuxillarySplitPane

setResizable
^^^^^^^^^^^^

.. java:method:: public void setResizable(boolean newResizable)
   :outertype: AuxillarySplitPane

   Set whether the auxillary panel is resizable

