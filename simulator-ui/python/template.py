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
        self.panel.layout=BoxLayout(self.panel,BoxLayout.Y_AXIS)

        #self.panel.minimumSize=55,-1
        self.panel.background=Color(0.1,0.1,0.1)
        self.max_icon_width=40
        self.templates={}
        self.labels=[]
        self.panels=[]

        
        self.ng=ca.nengo.ui.NengoGraphics.getInstance()
        self.ng.universe.transferHandler=DropHandler()
        
        self.add_template('Network',ca.nengo.ui.models.constructors.CNetwork(),'images/nengoIcons/network.gif')
        self.add_template('Ensemble',ca.nengo.ui.models.constructors.CNEFEnsemble(),'images/nengoIcons/ensemble.gif')
        self.add_template('Input',ca.nengo.ui.models.constructors.CFunctionInput(),'images/nengoIcons/input.png')

        for template in nef.templates.templates:
            self.add_template(getattr(template,'label'),TemplateConstructor(template),'images/nengoIcons/'+getattr(template,'icon'))

        #for i in range(20):
        #    self.add_template('Input',ca.nengo.ui.models.constructors.CFunctionInput(),'images/nengoIcons/input.gif')

                
        self.scrollPane=JScrollPane(self.panel,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
        self.ng.getContentPane().add(self.scrollPane,BorderLayout.WEST)
        
        self.scrollPane.verticalScrollBar.unitIncrement=20
        self.scrollPane.revalidate()
        self.size_with_scrollbar=self.scrollPane.preferredSize
        self.scrollPane.verticalScrollBarPolicy=ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        
    def add_template(self,name,constructor,image):
        icon=ImageIcon(image)
        if icon.iconWidth>self.max_icon_width:
            icon=ImageIcon(icon.image.getScaledInstance(self.max_icon_width,icon.iconHeight*self.max_icon_width/icon.iconWidth,Image.SCALE_SMOOTH))


        panel=JPanel(layout=BorderLayout(),opaque=False)

        button=JButton(icon=icon,toolTipText=name.replace('\n',' '),
                       borderPainted=False,focusPainted=False,contentAreaFilled=False,margin=java.awt.Insets(0,0,0,0),
                       verticalTextPosition=AbstractButton.BOTTOM,horizontalTextPosition=AbstractButton.CENTER,
                       mousePressed=lambda e: e.source.transferHandler.exportAsDrag(e.source,e,TransferHandler.COPY))
        button.transferHandler=self
        panel.add(button)

        text='<html><center>%s</center></html>'%name.replace('\n','<br/>')
        label=JLabel(text,horizontalAlignment=SwingConstants.CENTER,foreground=Color.WHITE)
        self.labels.append(label)
        panel.add(label,BorderLayout.SOUTH)
        self.panels.append(panel)
        
        panel.alignmentY=Component.CENTER_ALIGNMENT
        panel.border=BorderFactory.createEmptyBorder(2,1,2,1)
        panel.maximumSize=panel.preferredSize
        self.panel.add(panel)
        self.templates[button]=constructor

    def getSourceActions(self,component):
        return TransferHandler.COPY

    def createTransferable(self,component):
        return TemplateTransferable(self.templates[component])

    def toggle_visible(self):
        self.visible(not self.scrollPane.visible)
    def visible(self,visible):
        self.scrollPane.visible=visible
        ca.nengo.ui.NengoGraphics.getInstance().contentPane.revalidate()
        self.scrollPane.preferredSize=self.size_with_scrollbar

    def toggle_labels(self):
        for l in self.labels:
            l.visible=not l.visible
        for p in self.panels:
            p.maximumSize=p.preferredSize
            p.revalidate()
        self.panel.revalidate()
        ca.nengo.ui.NengoGraphics.getInstance().contentPane.revalidate()

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
template.visible(True)


