import java
from java.awt import *
from javax.swing import *

import ca.nengo
import sys

import template

SelectionHandler = ca.nengo.ui.lib.world.handlers.SelectionHandler

from ca.nengo.math.impl import WeightedCostApproximator
from ca.nengo.util.impl import NEFGPUInterface, NodeThreadPool
from ca.nengo.ui.configurable.panels import BooleanPanel, IntegerPanel
from ca.nengo.ui.configurable.descriptors import *
from ca.nengo.ui.configurable import *


class GpuCountPanel(IntegerPanel):

    def initPanel(self):
        IntegerPanel.initPanel(self)

        num_avail_devices = NEFGPUInterface.getNumAvailableDevices()

        if num_avail_devices < 1:
            self.tf.setEnabled(False)
            self.tf.setEditable(False)

            error_message = " %s" % (NEFGPUInterface.getErrorMessage())
            error_message_label = JLabel(error_message)
            error_message_label.setForeground(Color.red)
            self.add(error_message_label)


class GpuUsePanel(BooleanPanel):

    def initPanel(self):
        BooleanPanel.initPanel(self)

        can_use_GPU = WeightedCostApproximator.canUseGPU()

        if not can_use_GPU:
            self.checkBox.setEnabled(False)
            self.label.setEnabled(False)

            error_message = " %s" % (
                WeightedCostApproximator.getGPUErrorMessage())
            error_message_label = JLabel(error_message)
            error_message_label.setForeground(Color.red)
            self.add(error_message_label)


class PGpuCount(PInt):
    def __init__(self, name):
        default = NEFGPUInterface.getRequestedNumDevices()
        maximum = NEFGPUInterface.getNumAvailableDevices()

        PInt.__init__(self, name, default, 0, maximum)

    def createInputPanel(self):
        return GpuCountPanel(self)


class PGpuUse(PBoolean):
    def __init__(self, name):
        PBoolean.__init__(self, name, WeightedCostApproximator.getUseGPU())

    def createInputPanel(self):
        return GpuUsePanel(self)


from ca.nengo.ui.configurable import ConfigException


class ParallelizationConfiguration(IConfigurable):
    num_java_threads = NodeThreadPool.getNumJavaThreads()
    num_sim_GPU = NEFGPUInterface.getRequestedNumDevices()
    use_GPU_for_creation = WeightedCostApproximator.getUseGPU()

    p_num_java_threads = PInt('Number of Java Threads', num_java_threads,
                              1, NodeThreadPool.getMaxNumJavaThreads())
    p_num_sim_GPU = PGpuCount('Number of GPU\'s for Simulation')
    p_use_GPU_for_creation = PGpuUse('Use GPU for Ensemble Creation')

    properties = [p_num_java_threads, p_num_sim_GPU, p_use_GPU_for_creation]

    def __init__(self):
        self.button = make_button('parallelization', self.do_configure,
                                  'Configure Parallelization')
        self.button.enabled = True

    def do_configure(self, event):
        self.p_num_java_threads.setDefaultValue(
            NodeThreadPool.getNumJavaThreads())
        self.p_num_sim_GPU.setDefaultValue(
            NEFGPUInterface.getRequestedNumDevices())
        self.p_use_GPU_for_creation.setDefaultValue(
            WeightedCostApproximator.getUseGPU())

        uc = ca.nengo.ui.configurable.managers.UserTemplateConfigurer(self)

        try:
            uc.configureAndWait()
        except ConfigException, e:
            e.defaultHandleBehavior()

    def completeConfiguration(self, props):
        self.num_java_threads = props.getValue(self.p_num_java_threads)
        self.num_sim_GPU = props.getValue(self.p_num_sim_GPU)
        self.use_GPU_for_creation = props.getValue(self.p_use_GPU_for_creation)

        NodeThreadPool.setNumJavaThreads(self.num_java_threads)
        NEFGPUInterface.setRequestedNumDevices(self.num_sim_GPU)
        WeightedCostApproximator.setUseGPU(self.use_GPU_for_creation)

    def preConfiguration(self, props):
        pass

    def getSchema(self):
        return ConfigSchemaImpl(self.properties, [])

    def getTypeName(self):
        return 'ParallelizationConfiguration'

    def getDescription(self):
        return 'Configure parallelization'


def make_button(icon, func, tip, **args):
    return JButton(
        icon=ImageIcon('python/images/%s.png' % icon), 
        rolloverIcon=ImageIcon('python/images/%s-pressed.png' % icon),
        actionPerformed=func, toolTipText=tip,
        borderPainted=False, focusPainted=False, 
        contentAreaFilled=False, margin=java.awt.Insets(0, 0, 0, 0),
        verticalTextPosition=AbstractButton.BOTTOM, 
        horizontalTextPosition=AbstractButton.CENTER, **args)

def make_label_button(text, func, tip, **args):
    return JButton(text=text,
                   actionPerformed=func, toolTipText=tip, **args)
                   # borderPainted=False,focusPainted=False,
                   # contentAreaFilled=False,margin=java.awt.Insets(0,0,0,0),
                   # verticalTextPosition=AbstractButton.BOTTOM,
                   # horizontalTextPosition=AbstractButton.CENTER,**args)


class ToolBar(
    ca.nengo.ui.lib.world.handlers.SelectionHandler.SelectionListener,
    ca.nengo.ui.lib.world.WorldObject.ChildListener, java.lang.Runnable):

    def __init__(self):
        #color = Color(.25,.25,.25,1)
        self.ng = ca.nengo.ui.NengoGraphics.getInstance()
        self.toolbar = JToolBar("Nengo actions", floatable=False)
        #self.toolbar.setBackground(color)

        ### Make and add buttons
        self.toolbar.add(make_button('open', self.do_open, 'open file'))
        self.toolbar.add(make_button(
                'savefile', self.do_save, 'save selected network'))
        self.button_scriptgen = make_button(
            'scriptgen', self.do_scriptgen,
            'generate selected network script', enabled=False)
        self.toolbar.add(self.button_scriptgen)
        self.toolbar.add(make_button('clear', self.do_clear_all, 'clear all'))

        self.toolbar.add(Box.createHorizontalGlue())

        self.toolbar.add(make_button('inspect', self.do_inspect, 'inspect'))
        self.button_run = make_button(
            'interactive', self.do_run, 'interactive plots', enabled=False)
        self.toolbar.add(self.button_run)

        SelectionHandler.addSelectionListener(self)
        self.ng.getWorld().getGround().addChildrenListener(self)

        self.ng.setToolbar(self.toolbar)

    def selectionChanged(self, objs):
        self.update()

    def childAdded(self, obj):
        self.update()

    def childRemoved(self, obj):
        self.update()

    def update(self):
        net = self.get_selected_network()
        self.button_scriptgen.enabled = net is not None

        topnet = self.get_selected_network(top_parent=True)
        self.button_run.enabled = topnet is not None

    def get_selected_network(self, top_parent=False):
        network = SelectionHandler.getActiveNetwork(top_parent)
        if network is not None:
            return network
        else:
            # see if the main world has only one network; if so, return it
            found_candidate = False
            for wo in self.ng.world.ground.children:
                if isinstance(wo, ca.nengo.ui.models.nodes.UINetwork):
                    if not found_candidate:
                        network = wo
                        found_candidate = True
                    else:
                        network = None
                        break
        return network

    def get_current_network_viewer(self):
        viewer = None
        net = self.get_selected_network(top_parent=False)
        if net is not None and hasattr(net, 'getViewer'):
            viewer = net.getViewer()
            if viewer is None or viewer.isDestroyed():
                if (hasattr(net, 'networkParent')
                    and net.networkParent is not None):
                    net = net.networkParent
                    viewer = net.getViewer()
        elif net is not None and hasattr(net, 'networkParent'):
            net = net.networkParent
            if net is not None and hasattr(net, 'getViewer'):
                viewer = net.getViewer()

        if (viewer is not None and 
            (viewer.isDestroyed() or
             not isinstance(viewer, ca.nengo.ui.models.viewers.NetworkViewer))):
            return None
        else:
            return viewer

    def do_open(self, event):
        ca.nengo.ui.actions.OpenNeoFileAction(self.ng).doAction()

    def do_save(self, event):
        network = self.get_selected_network()
        if network is not None:
            ca.nengo.ui.actions.SaveNodeAction(network).doAction()

    def do_scriptgen(self, event):
        network = self.get_selected_network()
        if network is not None:
            ca.nengo.ui.actions.GeneratePythonScriptAction(network).doAction()

    def do_clear_all(self, event):
        ca.nengo.ui.actions.ClearAllAction("Clear all").doAction()

    def do_inspect(self, event):
        self.ng.toggleConfigPane()

    def do_run(self, event):
        network = self.get_selected_network(top_parent=True)
        if network is not None:
            ca.nengo.ui.actions.RunInteractivePlotsAction(network).doAction()

    def do_interrupt(self, event):
        self.ng.progressIndicator.interrupt()


################################################################################
### Main
toolbar = ToolBar()
