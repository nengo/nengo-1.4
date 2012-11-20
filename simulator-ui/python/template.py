from javax.swing import *
from java.awt import *
import java

from ca.nengo.ui.configurable.descriptors import *
from ca.nengo.ui.configurable import *
from ca.nengo.ui.configurable.managers import ConfigDialogClosedException

import ca.nengo
import inspect
import threading

################################################################################
class Properties:
    pmap = {}
    pmap[str] = PString
    pmap[int] = PInt
    pmap[float] = PFloat
    pmap[bool] = PBoolean
    
    def __init__(self,params):
        self._params = params
        self._list = []
        self._properties = {}
        for p in params:
            if len(p) == 3:
                key,text,type = p
                help = ''
            else:
                key,text,type,help = p
            if isinstance(type,Property):
                p = type
            # checking isinstance(type, Property.__class__) will not find Jython children of Property
            # search for a known attribute of the interface instead
            elif hasattr(type, 'createInputPanel'):
                p = type(text)
            else:
                p = self.pmap[type](text)
            p.setDescription(help)    
            self._list.append(p)
            self._properties[key] = p

################################################################################
class TemplateConstructor(IConfigurable):
    def __init__(self,template):
        self.template = template
        self.network = None
        self.node = None
        self.properties = None

    def completeConfiguration(self,props):
        args = {}
        for k,p in self.properties._properties.items():
            args[k] = props.getValue(p)
        make_func = getattr(self.template,'make')
        a,v,kw,d = inspect.getargspec(make_func)
        if len(a)-len(d) == 1:
            make_func(self.network,**args)
        elif len(a)-len(d) == 2:
            make_func(self.network,self.node,**args)
        else:
            print 'Invalid make function:',make_func
    def preConfiguration(self,props):
        args = {}
        for k,p in self.properties._properties.items():
            args[k] = props.getValue(p)
        test_func = getattr(self.template,'test_params')
        a,v,kw,d = inspect.getargspec(test_func)
        if len(a) == 3:
            error = test_func(self.network,self.node,args)
        else:
            error = test_func(self.network,args)
        if error is not None:
            raise ca.nengo.ui.configurable.ConfigException(error)
    def getSchema(self):
        return ConfigSchemaImpl(self.properties._list,[])
    def getTypeName(self):
        return self.template.__name__
    def getDescription(self):
        return self.template.title
    def getExtendedDescription(self):
        return getattr(self.template,'description',None)

    def set_network(self,network,node):
        if network is None: self.network = None
        else: self.network = nef.Network(network)
        self.node = node

        params = self.template.params
        if callable(params):
            params = params(self.network,self.node)
        self.properties = Properties(params)
    
################################################################################
import nef.templates

class TemplateBar(TransferHandler):
    def __init__(self):
        self.panel = JPanel()
        self.panel.layout = BoxLayout(self.panel,BoxLayout.Y_AXIS)

        #self.panel.minimumSize = 55,-1
        self.panel.background = Color(0.1,0.1,0.1)
        self.max_icon_width = 40
        self.templates = {}
        self.labels = []
        self.panels = []

        
        self.ng = ca.nengo.ui.NengoGraphics.getInstance()
        dh = DropHandler()
        self.ng.universe.transferHandler = dh
        self.ng.universe.getDropTarget().addDropTargetListener(dh)
        
        self.add_template('Network','ca.nengo.ui.models.constructors.CNetwork','images/nengoIcons/network.gif')
        self.add_template('Ensemble','ca.nengo.ui.models.constructors.CNEFEnsemble','images/nengoIcons/ensemble.gif')
        self.add_template('Input','ca.nengo.ui.models.constructors.CFunctionInput','images/nengoIcons/input.png')
        #self.add_template('Origin','ca.nengo.ui.models.constructors.CDecodedOrigin','images/nengoIcons/origin.png')
        self.add_template('Origin','nef.templates.origin','images/nengoIcons/origin.png')
        self.add_template('Termination','nef.templates.termination','images/nengoIcons/termination.png')
        
        self.panel.add(JSeparator(JSeparator.HORIZONTAL,maximumSize = (200,1),foreground = Color(0.3,0.3,0.3),background = Color(0.1,0.1,0.1)))

        for template in nef.templates.templates:
            self.add_template(getattr(template,'label'),template.__name__,'images/nengoIcons/'+getattr(template,'icon'))

        # get NengoGraphics to add me
        self.ng.setTemplatePanel(self.panel)
        
    def add_template(self,name,constructor,image):
        icon = ImageIcon(image)
        if icon.iconWidth>self.max_icon_width:
            icon = ImageIcon(icon.image.getScaledInstance(self.max_icon_width,icon.iconHeight*self.max_icon_width/icon.iconWidth,Image.SCALE_SMOOTH))


        panel = JPanel(layout = BorderLayout(),opaque = False)

        button = JButton(icon = icon,toolTipText = name.replace('\n',' '),
                       borderPainted = False,focusPainted = False,contentAreaFilled = False,margin = java.awt.Insets(0,0,0,0),
                       verticalTextPosition = AbstractButton.BOTTOM,horizontalTextPosition = AbstractButton.CENTER,
                       mousePressed = lambda e: e.source.transferHandler.exportAsDrag(e.source,e,TransferHandler.COPY))
        button.transferHandler = self
        panel.add(button)

        text = '<html><center>%s</center></html>'%name.replace('\n','<br/>')
        label = JLabel(text,horizontalAlignment = SwingConstants.CENTER,foreground = Color.WHITE)
        self.labels.append(label)
        panel.add(label,BorderLayout.SOUTH)
        self.panels.append(panel)
        
        panel.alignmentY = Component.CENTER_ALIGNMENT
        panel.border = BorderFactory.createEmptyBorder(2,1,2,1)
        panel.maximumSize = panel.preferredSize
        self.panel.add(panel)
        self.templates[button] = constructor

    def getSourceActions(self,component):
        return TransferHandler.COPY

    def createTransferable(self,component):
        return TemplateTransferable(self.templates[component])

    def toggle_labels(self):
        for l in self.labels:
            l.visible = not l.visible
        for p in self.panels:
            p.maximumSize = p.preferredSize
            p.revalidate()
        self.panel.revalidate()
        ca.nengo.ui.NengoGraphics.getInstance().contentPane.revalidate()

################################################################################
class DropHandler(TransferHandler,java.awt.dnd.DropTargetListener):
    dragPoint = None

    def dragOver(self,event):
        self.dragPoint = event.getLocation()
    def dragEnter(self,event):
        pass
    def dragExit(self,event):
        pass
    def drop(self,event):
        pass
    def dropActionChanged(self,event):
        pass

    def find_at(self,container,pos):
        # get the nodes at the current location, in the current container
        nodes = container.findIntersectingNodes(java.awt.Rectangle(pos.x,pos.y,1,1))

        # loop through nodes, looking for NetworkViewers
        net = container
        netpos = pos
        for n in nodes:
            if isinstance(n,ca.nengo.ui.models.viewers.NetworkViewer):
                # we found a network
                net = n
                netpos = n.localToView(n.globalToLocal(pos))

                # look for nested networks at the current local position
                nn,p = self.find_at(n.ground,netpos)
                if nn is not n.ground:
                    # we found a nested network, so return it. Otherwise, we return the current network.
                    net = nn
                    netpos = p

                # we found the top network at the current position, so break
                break

        return net,netpos

    def find_target(self,point):
        ng = ca.nengo.ui.NengoGraphics.getInstance()
        top = ng.getTopWindow()
        if top is None:
            top = ng.world
            pos = top.localToView(point)
            net,pos = self.find_at(top.ground,pos)
            if net is top.ground: net = top
        else:
            pos = top.globalToLocal(point)
            nodes = top.findIntersectingNodes(java.awt.Rectangle(pos.x,pos.y,1,1))
            net,pos = self.find_at(top,pos)
        return net,pos

    def canImport(self,support,flavors = None):
        if flavors is not None:   # Java 1.5
            return True
    
        constructor = support.getTransferable().getTransferData(TemplateTransferable.flavor)
        
        constructor = eval(constructor)
        
        if constructor is ca.nengo.ui.models.constructors.CNetwork: return True

        drop_on_ensemble = False
        if constructor is ca.nengo.ui.models.constructors.CDecodedOrigin: drop_on_ensemble = True
        if constructor is ca.nengo.ui.models.constructors.CDecodedTermination: drop_on_ensemble = True

        net,pos = self.find_target(support.dropLocation.dropPoint)

        if net is ca.nengo.ui.NengoGraphics.getInstance().world:
            return False

        if drop_on_ensemble:
            for n3 in net.ground.findIntersectingNodes(java.awt.Rectangle(pos.x,pos.y,1,1)):
                if isinstance(n3,ca.nengo.ui.models.nodes.UINEFEnsemble):
                    return True
            return False

        if hasattr(constructor,'test_drop'):
            node = None
            for n3 in net.ground.findIntersectingNodes(java.awt.Rectangle(pos.x,pos.y,1,1)):
                if isinstance(n3,ca.nengo.ui.models.UINeoNode) or isinstance(n3, ca.nengo.ui.models.nodes.widgets.UITermination) or isinstance(n3, ca.nengo.ui.models.nodes.widgets.UIOrigin):
                    node = n3.model
            return constructor.test_drop(net.model,node)

        return True

    def importData(self,support,transferable = None):
        if transferable is not None:   # Java 1.5
            constructor = transferable.getTransferData(TemplateTransferable.flavor)
            net,pos = self.find_target(self.dragPoint)
        else:
            constructor = support.getTransferable().getTransferData(TemplateTransferable.flavor)
            net,pos = self.find_target(support.dropLocation.dropPoint)
            
        try:
            constructor = eval(constructor)
            drop_on_ensemble = False
            if constructor is ca.nengo.ui.models.constructors.CDecodedOrigin: drop_on_ensemble = True
            if constructor is ca.nengo.ui.models.constructors.CDecodedTermination: drop_on_ensemble = True

            node = None
            for n3 in net.ground.findIntersectingNodes(java.awt.Rectangle(pos.x,pos.y,1,1)):
                if isinstance(n3,ca.nengo.ui.models.UINeoNode) or isinstance(n3, ca.nengo.ui.models.nodes.widgets.UITermination) or isinstance(n3, ca.nengo.ui.models.nodes.widgets.UIOrigin):
                    node = n3

            if drop_on_ensemble:
                self.create(constructor(node.model),net,position = None,node = node)
            else:
                if hasattr(constructor,'make'):
                    constructor = TemplateConstructor(constructor)
                else:
                    constructor = constructor()
                self.create(constructor,net,position = pos,node = node)
            return
        except Exception, e:
            print e

    def create(self,constructor,nodeContainer,position,node = None):
        try:
            if isinstance(constructor,ca.nengo.ui.models.constructors.ConstructableNode):
                action = ca.nengo.ui.actions.CreateModelAction(nodeContainer,constructor)
                action.setPosition(position.x,position.y)
                action.doAction()
            elif isinstance(constructor,ca.nengo.ui.models.constructors.CDecodedOrigin):
                uc = ca.nengo.ui.configurable.managers.UserTemplateConfigurer(constructor)
                uc.configureAndWait()
                if constructor.model is not None:
                    node.showOrigin(constructor.model.name)
            elif isinstance(constructor,ca.nengo.ui.models.constructors.CDecodedTermination):
                uc = ca.nengo.ui.configurable.managers.UserTemplateConfigurer(constructor)
                uc.configureAndWait()
                if constructor.model is not None:
                    node.showTermination(constructor.model.name)
                
            elif isinstance(constructor,TemplateConstructor):
                assert isinstance(nodeContainer,ca.nengo.ui.models.viewers.NetworkViewer)
                nodeContainer.setNewItemPosition(position.x,position.y)

                if node is not None: nodemodel = node.getModel()
                else: nodemodel = None
                constructor.set_network(nodeContainer.getModel(),node = nodemodel)
                uc = ca.nengo.ui.configurable.managers.UserTemplateConfigurer(constructor)
                
                # do the configuring in a separate thread so we don't block the 
                #  main UI thread (this was causing the progress indicator to 
                #  not appear for templates)
                threading.Thread(target=self.configureAndWait,args=(node,uc)).start()

        except ca.nengo.ui.configurable.managers.ConfigDialogClosedException:
            pass
    
    def configureAndWait(self, node, user_configurer):
        #if node is not None:
        #    origins = node.model.getOrigins()
        #    terminations = node.model.getTerminations()

        try:
          user_configurer.configureAndWait()
        except ConfigDialogClosedException:
          pass


        #this was causing terminations and origins added via templates to be displayed twice
        #if node is not None:
            #for o in node.model.getOrigins():
            #    if o not in origins: node.showOrigin(o.name)

            #for t in node.model.getTerminations():
                #pass
                #if t not in terminations: node.showTermination(t.name)
                
            
################################################################################
class TemplateTransferable(java.awt.datatransfer.Transferable):
    flavor = java.awt.datatransfer.DataFlavor("application/nengo;class = java.lang.String")
    def __init__(self,action):
        self.action = action
    def getTransferDataFlavors(self):
        return [self.flavor]
    def isDataFlavorSupported(self,flavor):
        return flavor.equals(self.flavor)
    def getTransferData(self,flavor):
        return self.action
        
################################################################################
### Main
template = TemplateBar()
