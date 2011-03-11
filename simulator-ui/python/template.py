from javax.swing import *
from java.awt import *
import java

from ca.nengo.ui.configurable.descriptors import *
from ca.nengo.ui.configurable import *
import ca.nengo

class Properties:
    pmap={}
    pmap[str]=PString
    pmap[int]=PInt
    pmap[float]=PFloat
    
    def __init__(self,params):
        self._params=params
        self._list=[]
        self._properties={}
        for key,text,type in params:
            p=self.pmap[type](text)
            self._list.append(p)
            self._properties[key]=p

class TemplateConstructor(IConfigurable):
    def __init__(self,template):
        self.template=template
        self.network=None
        self.properties=Properties(template.params)

    def completeConfiguration(self,props):
        args={}
        for k,p in self.properties._properties.items():
            args[k]=props.getValue(p)
        make_func=getattr(self.template,'make')
        make_func(self.network,**args)
    def preConfiguration(self,props):
        args={}
        for k,p in self.properties._properties.items():
            args[k]=props.getValue(p)
        test_func=getattr(self.template,'test_params')
        error=test_func(self.network,args)
        if error is not None:
            raise ca.nengo.ui.configurable.ConfigException(error)
    def getSchema(self):
        return ConfigSchemaImpl(self.properties._list,[])
    def getTypeName(self):
        return self.template.__name__
    def getDescription(self):
        return self.template.title

    def set_network(self,network):
        if network is None: self.network=None
        else: self.network=nef.Network(network)
    

import nef.templates

class TemplateBar(TransferHandler):
    def __init__(self):
        self.panel=JPanel()
        self.panel.preferredSize=50,50
        self.panel.background=Color(0.1,0.1,0.1)
        self.max_width=45
        self.templates={}

        
        self.ng=ca.nengo.ui.NengoGraphics.getInstance()
        self.ng.universe.transferHandler=DropHandler()
        
        self.add_template('Network',ca.nengo.ui.models.constructors.CNetwork(),'images/nengoIcons/network.gif')
        self.add_template('Ensemble',ca.nengo.ui.models.constructors.CNEFEnsemble(),'images/nengoIcons/ensemble.gif')
        self.add_template('Input',ca.nengo.ui.models.constructors.CFunctionInput(),'images/nengoIcons/input.gif')

        for template in nef.templates.templates:
            self.add_template(getattr(template,'title'),TemplateConstructor(template),'images/nengoIcons/'+getattr(template,'icon'))
                
        self.scrollPane=JScrollPane(self.panel)
        self.ng.getContentPane().add(self.scrollPane,BorderLayout.WEST)
        self.scrollPane.preferredSize=65,65
        self.scrollPane.revalidate()
        
    def add_template(self,name,constructor,image):
        icon=ImageIcon(image)
        if icon.iconWidth>self.max_width:
            icon=ImageIcon(icon.image.getScaledInstance(self.max_width,icon.iconHeight*self.max_width/icon.iconWidth,Image.SCALE_SMOOTH))
            
            
            
        button=JButton(icon=icon,
                       text=name,foreground=Color.WHITE,
                       borderPainted=False,focusPainted=False,contentAreaFilled=False,margin=java.awt.Insets(0,0,0,0),
                       verticalTextPosition=AbstractButton.BOTTOM,horizontalTextPosition=AbstractButton.CENTER,
                       mousePressed=lambda e: e.source.transferHandler.exportAsDrag(e.source,e,TransferHandler.COPY))
        button.transferHandler=self
        self.panel.add(button)
        self.templates[button]=constructor

    def getSourceActions(self,component):
        return TransferHandler.COPY

    def createTransferable(self,component):
        return TemplateTransferable(self.templates[component])

    def toggle_visible(self):
        self.visible(not self.scrollPane.visible)
    def visible(self,visible):
        ng=ca.nengo.ui.NengoGraphics.getInstance()
        self.scrollPane.visible=visible
        ng.contentPane.revalidate()
        


class DropHandler(TransferHandler):
    def canImport(self,support):
        constructor=support.getTransferable().getTransferData(TemplateTransferable.flavor)
        if isinstance(constructor,ca.nengo.ui.models.constructors.CNetwork): return True
        
        ng=ca.nengo.ui.NengoGraphics.getInstance()
        pos=ng.world.localToView(support.dropLocation.dropPoint)
        nodes=ng.world.ground.findIntersectingNodes(java.awt.Rectangle(pos.x,pos.y,1,1))
        for n in nodes:
            if isinstance(n,ca.nengo.ui.models.NodeContainer):
                return True
        else:
            return False

    def importData(self,support):
        try:
            constructor=support.getTransferable().getTransferData(TemplateTransferable.flavor)

            
            ng=ca.nengo.ui.NengoGraphics.getInstance()
            pos=ng.world.localToView(support.dropLocation.dropPoint)
            nodes=ng.world.ground.findIntersectingNodes(java.awt.Rectangle(pos.x,pos.y,1,1))

            for n in nodes:
                if isinstance(n,ca.nengo.ui.models.NodeContainer):
                    pos=n.localToView(n.globalToLocal(ng.world.localToView(support.dropLocation.dropPoint)))
                    self.create(constructor,n,pos)
                    break
            else:
                self.create(constructor,ng,pos)
        except Exception, e:
            print e

    def create(self,constructor,nodeContainer,position):
        if isinstance(constructor,ca.nengo.ui.models.constructors.ConstructableNode):
            action=ca.nengo.ui.actions.CreateModelAction(nodeContainer,constructor)
            action.setPosition(position.x,position.y)
            action.doAction()
        elif isinstance(constructor,IConfigurable):
            assert isinstance(nodeContainer,ca.nengo.ui.models.viewers.NetworkViewer)
            nodeContainer.setNewItemPosition(position.x,position.y)
            
            constructor.set_network(nodeContainer.getModel())
            uc=ca.nengo.ui.configurable.managers.UserTemplateConfigurer(constructor)
            uc.configureAndWait()
            
    
    
        

class TemplateTransferable(java.awt.datatransfer.Transferable):
    flavor=java.awt.datatransfer.DataFlavor("application/nengo")
    def __init__(self,action):
        self.action=action
    def getTransferDataFlavors(self):
        return [self.flavor]
    def isDataFlavorSupported(self,flavor):
        return flavor.equals(self.flavor)
    def getTransferData(self,flavor):
        return self.action

        

template=TemplateBar()
template.visible(False)

