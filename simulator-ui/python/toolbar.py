from javax.swing import *
from java.awt import *
import java

import ca.nengo
import sys

if 'lib/iText-5.0.5.jar' not in sys.path:
    sys.path.append('lib/iText-5.0.5.jar')

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
        if node is not None and not hasattr(node.model,'mode'): node=None
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
        
        self.mode_combobox=SimulationModeComboBox()
        self.toolbar.add(self.mode_combobox)
        self.parisian=ParisianTransform()
        self.toolbar.add(self.parisian.button)
        self.feedforward=make_label_button('layout',self.do_feedforward_layout,'reorganize components to flow from left to right',enabled=False)
        self.toolbar.add(self.feedforward)

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
        #try:
        #    self.button_stop.enabled=self.ng.progressIndicator.visible
        #except:
        #    pass    
    
        selected=self.ng.getSelectedObj()
        self.mode_combobox.set_node(selected)

        projection=None
        if selected is not None and isinstance(selected.model,ca.nengo.model.nef.impl.DecodedTermination):
            term=selected.model
            network=selected.nodeParent.networkParent.model
            for p in network.getProjections():
                if p.termination==term:
                    if isinstance(p.origin,ca.nengo.model.nef.impl.DecodedOrigin):
                        projection=p
                    break
        self.parisian.set_projection(projection)

        net=self.get_current_network()
        if net is None:
            self.button_run.enabled=False
            self.button_run.toolTipText='run'
        else:
            self.button_run.enabled=True
            self.button_run.toolTipText='run '+net.name
            
        viewer=self.get_current_network_viewer()
        self.feedforward.enabled=viewer is not None
        
        
        
    def get_current_network(self,top_parent=True):
        network=self.ng.getSelectedObj()
        if network is not None:
            while hasattr(network,'nodeParent') and network.nodeParent is not None:
                network=network.nodeParent
            while top_parent and hasattr(network,'networkParent') and network.networkParent is not None:
                network=network.networkParent
        else:
            for wo in ng.world.ground.children:
                if isinstance(wo,ca.nengo.ui.models.nodes.UINetwork):
                    network=wo
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


    def do_inspect(self,event):
        self.ng.toggleConfigPane()
        
    def do_feedforward_layout(self,event):
        viewer=self.get_current_network_viewer()
        viewer.doFeedForwardLayout()
        
    def do_open(self,event):
        ca.nengo.ui.actions.OpenNeoFileAction(ng).doAction()
    def do_run(self,event):
        network=self.get_current_network()
        if network is not None:         
            ca.nengo.ui.actions.RunInteractivePlotstAction(network).doAction()
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
        
            doc=Document()
            writer=PdfWriter.getInstance(doc,java.io.FileOutputStream(f))
            doc.open()
            cb=writer.getDirectContent()
            w=universe.size.width
            h=universe.size.height
            pw=550
            ph=800
            tp=cb.createTemplate(pw,ph)
            g2=tp.createGraphicsShapes(pw,ph)
            at = java.awt.geom.AffineTransform()        
            s=min(float(pw)/w,float(ph)/h)        
            at.scale(s,s)
            g2.transform(at)
            #self.view.area.pdftemplate=tp,s
            ca.nengo.ui.lib.world.piccolo.primitives.Text.setUseGreekThreshold(False)
            universe.paint(g2)
            #self.view.area.pdftemplate=None
            g2.dispose()

            cb.addTemplate(tp,20,0)
            doc.close()
            ca.nengo.ui.lib.world.piccolo.primitives.Text.setUseGreekThreshold(True)
     
ng=ca.nengo.ui.NengoGraphics.getInstance()
toolbar=ToolBar()
ng.contentPane.revalidate()

