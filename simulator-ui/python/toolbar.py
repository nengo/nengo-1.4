from javax.swing import *
from java.awt import *
import java

import ca.nengo
import sys

if 'lib/itextpdf-5.3.4.jar' not in sys.path:
    sys.path.append('lib/itextpdf-5.3.4.jar')

import template

from ca.nengo.model import SimulationMode
class SimulationModeComboBox(JComboBox):
    def __init__(self):
        JComboBox.__init__(self,['spiking','rate','direct'],toolTipText='set simulation mode')
        self.node=None
        self.enabled=False
        self.addActionListener(self)
        self.maximumSize=self.preferredSize
    def set_node(self,node):
        self.node=None
        if node is not None and (not hasattr(node, 'model')
                                 or not hasattr(node.model,'mode')):
            node=None
        self.enabled=node is not None
        
        if node is not None:
            mode=node.model.mode
            
            if mode in [SimulationMode.DEFAULT,SimulationMode.PRECISE]:
                self.setSelectedIndex(0)
            elif mode in [SimulationMode.RATE]:
                self.setSelectedIndex(1)
            elif mode in [SimulationMode.DIRECT,SimulationMode.APPROXIMATE]:
                self.setSelectedIndex(2)

            self.node=node

        
    def actionPerformed(self,event):
        if self.node is None: return
        mode=self.getSelectedItem()
        if mode=='rate': self.node.model.mode=SimulationMode.RATE
        elif mode=='direct': self.node.model.mode=SimulationMode.DIRECT
        else: self.node.model.mode=SimulationMode.DEFAULT
        self.set_node(self.node)

class LayoutComboBox(JComboBox):
    def __init__(self):
        JComboBox.__init__(self,['last saved','feed-forward','sort by name'],
                           toolTipText='set network layout')
        self.addActionListener(self)
        self.maximumSize=self.preferredSize
        self.viewer = None
    
    def set_viewer(self,viewer):
        self.viewer=viewer
        if self.viewer is not None and self.viewer.justOpened:
            self.setSelectedIndex(0)
            self.viewer.justOpened = False
        
    def actionPerformed(self,event):
        if self.viewer is None: return
        layout = self.getSelectedItem()
        
        if layout=='last saved':
            self.viewer.restoreNodeLayout()
        elif layout=='feed-forward':
            self.viewer.doFeedForwardLayout()
        elif layout=='sort by name':
            self.viewer.doSortByNameLayout()

from ca.nengo.ui.configurable.descriptors import *
from ca.nengo.ui.configurable import *

class ParisianTransform(IConfigurable):
    p_num_interneurons=PInt('Number of interneurons')
    p_tau_interneurons=PFloat('Post-synaptic time constant of interneurons')
    p_excitatory=PBoolean('Excitatory')
    p_optimize=PBoolean('Optimize bias function')
    properties=[p_num_interneurons,p_tau_interneurons,p_excitatory,p_optimize]

    def __init__(self):
        self.button=make_button('parisian',self.do_transform,'create interneurons')
        self.projection=None
        self.button.enabled=False

    def set_projection(self,projection):
        self.projection=projection
        self.button.enabled=projection is not None

    def do_transform(self,event):
        uc=ca.nengo.ui.configurable.managers.UserTemplateConfigurer(self)
        uc.configureAndWait()
    
    def completeConfiguration(self,props):
        if self.projection is not None:
            self.projection.addBias(props.getValue(self.p_num_interneurons),props.getValue(self.p_tau_interneurons),self.projection.termination.tau,props.getValue(self.p_excitatory),props.getValue(self.p_optimize))
    def preConfiguration(self,props):
        pass
    def getSchema(self):
        return ConfigSchemaImpl(self.properties,[])
    def getTypeName(self):
        return 'ParisianTransform'
    def getDescription(self):
        return 'Create Interneurons'



from ca.nengo.math.impl import WeightedCostApproximator
from ca.nengo.util.impl import NEFGPUInterface, NodeThreadPool
from ca.nengo.ui.configurable.panels import BooleanPanel, IntegerPanel


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

        error_message = " %s" % (WeightedCostApproximator.getGPUErrorMessage())
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
    def __init__(self,name):
        PBoolean.__init__(self, name, WeightedCostApproximator.getUseGPU())

    def createInputPanel(self):
        return GpuUsePanel(self)

from ca.nengo.ui.configurable import ConfigException
class ParallelizationConfiguration(IConfigurable):
  num_java_threads=NodeThreadPool.getNumJavaThreads()
  num_sim_GPU=NEFGPUInterface.getRequestedNumDevices()
  use_GPU_for_creation=WeightedCostApproximator.getUseGPU()

  p_num_java_threads=PInt('Number of Java Threads', num_java_threads, 1, NodeThreadPool.getMaxNumJavaThreads())
  p_num_sim_GPU=PGpuCount('Number of GPU\'s for Simulation')
  p_use_GPU_for_creation=PGpuUse('Use GPU for Ensemble Creation')

  properties=[p_num_java_threads, p_num_sim_GPU, p_use_GPU_for_creation]

  def __init__(self):
      self.button=make_button('parallelization', self.do_configure, 'Configure Parallelization')
      self.button.enabled=True
  
  def do_configure(self, event):
      self.p_num_java_threads.setDefaultValue(NodeThreadPool.getNumJavaThreads())
      self.p_num_sim_GPU.setDefaultValue(NEFGPUInterface.getRequestedNumDevices())
      self.p_use_GPU_for_creation.setDefaultValue(WeightedCostApproximator.getUseGPU())

      uc=ca.nengo.ui.configurable.managers.UserTemplateConfigurer(self)

      try:
        uc.configureAndWait()
      except ConfigException, e:
        e.defaultHandleBehavior()

  def completeConfiguration(self,props):
      self.num_java_threads=props.getValue(self.p_num_java_threads)
      self.num_sim_GPU=props.getValue(self.p_num_sim_GPU)
      self.use_GPU_for_creation=props.getValue(self.p_use_GPU_for_creation)

      NodeThreadPool.setNumJavaThreads(self.num_java_threads)
      NEFGPUInterface.setRequestedNumDevices(self.num_sim_GPU)
      WeightedCostApproximator.setUseGPU(self.use_GPU_for_creation)

  def preConfiguration(self,props):
      pass
  def getSchema(self):
      return ConfigSchemaImpl(self.properties,[])
  def getTypeName(self):
      return 'ParallelizationConfiguration'
  def getDescription(self):
      return 'Configure parallelization'





def make_button(icon,func,tip,**args):
    return JButton(icon=ImageIcon('python/images/%s.png'%icon),rolloverIcon=ImageIcon('python/images/%s-pressed.png'%icon),
                   actionPerformed=func,toolTipText=tip,
                   borderPainted=False,focusPainted=False,contentAreaFilled=False,margin=java.awt.Insets(0,0,0,0),
                   verticalTextPosition=AbstractButton.BOTTOM,horizontalTextPosition=AbstractButton.CENTER,**args)
def make_label_button(text,func,tip,**args):
    return JButton(text=text,
                   actionPerformed=func,toolTipText=tip,**args)
#                   borderPainted=False,focusPainted=False,contentAreaFilled=False,margin=java.awt.Insets(0,0,0,0),
#                   verticalTextPosition=AbstractButton.BOTTOM,horizontalTextPosition=AbstractButton.CENTER,**args)
    


class ToolBar(ca.nengo.ui.lib.world.handlers.SelectionHandler.SelectionListener,ca.nengo.ui.lib.world.WorldObject.ChildListener,java.lang.Runnable):
    def __init__(self):
        self.ng=ca.nengo.ui.NengoGraphics.getInstance()
        self.toolbar=JToolBar("Nengo actions",floatable=False)
        self.toolbar.add(make_button('open',self.do_open,'open file'))
        self.toolbar.add(make_button('clear', self.do_clear_all, 'clear all'))
        self.toolbar.add(make_button('pdf',self.do_pdf,'save as pdf'))

        self.toolbar.add(Box.createHorizontalGlue())
        
        self.toolbar.add(JLabel("mode:"))
        self.mode_combobox=SimulationModeComboBox()
        self.toolbar.add(self.mode_combobox)
        self.parisian=ParisianTransform()
        self.toolbar.add(self.parisian.button)
        self.toolbar.add(JLabel("layout:"))
        self.layoutcombo=LayoutComboBox()
        self.toolbar.add(self.layoutcombo)
        self.layoutsave=make_button('save',self.do_save_layout,"save the current network layout",enabled=False)
        self.toolbar.add(self.layoutsave)
        self.zoom=make_button('zoom',self.do_zoom_to_fit,"zoom to fit")
        self.toolbar.add(self.zoom)
        self.toolbar.add(Box.createHorizontalGlue())
        
        #self.button_stop=make_button('stop',self.do_interrupt,'Stop the currently running simulation',enabled=False)
        #self.toolbar.add(self.button_stop)
        self.parallelization=ParallelizationConfiguration()
        self.toolbar.add(self.parallelization.button)
        #self.toolbar.add(make_button('templates',lambda event: template.template.toggle_visible(),'toggle templates'))
        self.toolbar.add(make_button('console',self.do_console,'toggle console'))
        self.toolbar.add(make_button('inspect',self.do_inspect,'inspect'))
        self.button_run=make_button('interactive',self.do_run,'interactive plots',enabled=False)
        self.toolbar.add(self.button_run)
        

        ca.nengo.ui.lib.world.handlers.SelectionHandler.addSelectionListener(self)
        self.ng.getWorld().getGround().addChildrenListener(self)

        self.ng.getContentPane().add(self.toolbar,BorderLayout.PAGE_START)

        java.lang.Thread(self).start()

    
    def run(self):
        while True:
            self.update()
            java.lang.Thread.sleep(500)


    def childAdded(self,obj):
        self.update()
    def childRemoved(self,obj):
        self.update()
    def objectFocused(self,obj):
        self.update()
        
    def update(self):
    
        selected=self.ng.getSelectedObj()
        self.mode_combobox.set_node(selected)

        projection=None
        if selected is not None and (hasattr(selected, 'model') and 
                isinstance(selected.model,ca.nengo.model.nef.impl.DecodedTermination)):
            term=selected.model
            network=selected.nodeParent.networkParent.model
            for p in network.getProjections():
                if p.termination==term:
                    if isinstance(p.origin,ca.nengo.model.nef.impl.DecodedOrigin):
                        projection=p
                    break
        self.parisian.set_projection(projection)

        net=self.get_current_network()
        self.button_run.enabled=net is not None
        if net is None:
            self.button_run.toolTipText='run'
        else:
            self.button_run.toolTipText='run '+net.name
            
        viewer=self.get_current_network_viewer()
        self.layoutcombo.set_viewer(viewer)
        self.layoutcombo.enabled=viewer is not None
        self.layoutsave.enabled=viewer is not None
        
        
    def get_current_network(self,top_parent=True):
        network=self.ng.getSelectedObj()
        if network is not None:
            while hasattr(network,'termination') and network.termination is not None:
                network=network.termination
            while hasattr(network,'nodeParent') and network.nodeParent is not None:
                network=network.nodeParent
            while top_parent and hasattr(network,'networkParent') and network.networkParent is not None:
                network=network.networkParent
        else:
            found_candidate=False
            for wo in ng.world.ground.children:
                if isinstance(wo,ca.nengo.ui.models.nodes.UINetwork):
                    if not found_candidate:
                        network=wo
                        found_candidate=True
                    else:
                        network=None
                        break
        return network      
        
    def get_current_network_viewer(self):
        viewer=None
        net=self.get_current_network(top_parent=False)
        if net is not None and hasattr(net,'getViewer'):
            viewer=net.getViewer()
            if viewer is None or viewer.isDestroyed():
                if hasattr(net,'networkParent') and net.networkParent is not None:
                    net=net.networkParent
                    viewer=net.getViewer()
        elif net is not None and hasattr(net,'networkParent'):
            net=net.networkParent
            if net is not None and hasattr(net,'getViewer'):
                viewer=net.getViewer()
        if viewer is not None and (viewer.isDestroyed() or
            not isinstance(viewer, ca.nengo.ui.models.viewers.NetworkViewer)):
            return None
        return viewer
          
        
    def do_console(self,event):
        pane=self.ng.scriptConsolePane
        pane.auxVisible=not pane.auxVisible

    def do_clear_all(self,event):
        response=JOptionPane.showConfirmDialog(
            ca.nengo.ui.lib.util.UIEnvironment.getInstance(),
            "Are you sure you want to remove all objects from Nengo?",
            "Clear all?",
            JOptionPane.YES_NO_OPTION)
        if response==0:    
            for c in list(self.ng.world.ground.children): 
                ng.removeNodeModel(c.model)    

    def do_zoom_to_fit(self,event):
        self.ng.world.zoomToFit()

    def do_inspect(self,event):
        self.ng.toggleConfigPane()
        
    def do_save_layout(self,event):
        viewer = self.get_current_network_viewer()
        viewer.saveNodeLayout()
        self.layoutcombo.setSelectedIndex(0)
        
    def do_open(self,event):
        ca.nengo.ui.actions.OpenNeoFileAction(ng).doAction()

    def do_run(self,event):
        network=self.get_current_network()
        if network is not None:         
            ca.nengo.ui.actions.RunInteractivePlotsAction(network).doAction()

    def do_interrupt(self,event):
        self.ng.progressIndicator.interrupt()

    def do_pdf(self,event):
        from com.itextpdf.text.pdf import PdfWriter
        from com.itextpdf.text import Document
        network=self.get_current_network()
        if network is None: name='Nengo'
        else: name=network.name
        fileChooser=JFileChooser()
        fileChooser.setDialogTitle('Save layout as PDF')
        fileChooser.setSelectedFile(java.io.File('%s.pdf'%name))
        if fileChooser.showSaveDialog(self.ng)==JFileChooser.APPROVE_OPTION:
            f=fileChooser.getSelectedFile()

            universe=self.ng.universe
            w=universe.size.width
            h=universe.size.height

            if( False ):
                # basic method: make a PDF page the same size as the Nengo window.
                #   This method preserves all details visible in the GUI
                pw = w
                ph = h

                # create PDF document and writer
                doc = Document( Rectangle(pw,ph), 0, 0, 0, 0 )
                writer = PdfWriter.getInstance(doc,java.io.FileOutputStream(f))
                doc.open()
                cb = writer.getDirectContent()

                # create a template, print the image to it, and add it to the page
                tp = cb.createTemplate(pw,ph)
                g2 = tp.createGraphicsShapes(pw,ph)
                universe.paint(g2)
                g2.dispose()
                cb.addTemplate(tp,0,0)

                # clean up everything
                doc.close()

            else:
                # Top of page method: prints to the top of the page
                pw = 550
                ph = 800
        
                # create PDF document and writer
                doc = Document()
                writer = PdfWriter.getInstance(doc,java.io.FileOutputStream(f))
                doc.open()
                cb = writer.getDirectContent()

                # create a template
                tp = cb.createTemplate(pw,ph)
                g2 = tp.createGraphicsShapes(pw,ph)

                # scale the template to fit the page
                at = java.awt.geom.AffineTransform()        
                s = min(float(pw)/w,float(ph)/h)        
                at.scale(s,s)
                g2.transform(at)

                # print the image to the template
                # turing off setUseGreekThreshold allows small text to print
                ca.nengo.ui.lib.world.piccolo.primitives.Text.setUseGreekThreshold(False)
                universe.paint(g2)
                ca.nengo.ui.lib.world.piccolo.primitives.Text.setUseGreekThreshold(True)
                g2.dispose()

                # add the template
                cb.addTemplate(tp,20,0)

                # clean up everything
                doc.close()
     
################################################################################
### Main
ng = ca.nengo.ui.NengoGraphics.getInstance()
toolbar = ToolBar()
ng.contentPane.revalidate()

