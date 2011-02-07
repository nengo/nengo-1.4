from javax.swing import *
from java.awt import *
import java

import ca.nengo

class TemplateBar(TransferHandler):
    def __init__(self):
        self.panel=JPanel()
        self.panel.preferredSize=50,50
        self.templates={}

        
        self.ng=ca.nengo.ui.NengoGraphics.getInstance()
        self.ng.universe.transferHandler=DropHandler()


        
        self.add_template('network',ca.nengo.ui.models.constructors.CNetwork)
        self.add_template('ensemble',ca.nengo.ui.models.constructors.CNEFEnsemble)

        
        self.ng.getContentPane().add(JScrollPane(self.panel),BorderLayout.WEST)
        
    def add_template(self,name,constructor):
        button=JButton(icon=ImageIcon('images/nengoIcons/EnsembleIcon.gif'),
                       text=name,
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


class DropHandler(TransferHandler):
    def canImport(self,support):
        return True
    def importData(self,support):
        constructor=support.getTransferable().getTransferData(TemplateTransferable.flavor)
        ng=ca.nengo.ui.NengoGraphics.getInstance()
        pos=ng.world.localToView(support.dropLocation.dropPoint)
        nodes=ng.world.ground.findIntersectingNodes(java.awt.Rectangle(pos.x,pos.y,1,1))

        for n in nodes:
            if isinstance(n,ca.nengo.ui.models.NodeContainer):
                action=ca.nengo.ui.actions.CreateModelAction(n,constructor())
                pos=n.localToView(n.globalToLocal(ng.world.localToView(support.dropLocation.dropPoint)))
                action.setPosition(pos.x,pos.y)
                action.doAction()
                break
        else:
            action=ca.nengo.ui.actions.CreateModelAction(ng,constructor())
            action.setPosition(pos.x,pos.y)
            action.doAction()
    
        

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

        

ng=ca.nengo.ui.NengoGraphics.getInstance()
size=ng.size
template=TemplateBar()
ng.pack()
ng.size=size

