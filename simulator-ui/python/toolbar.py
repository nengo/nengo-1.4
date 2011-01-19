from javax.swing import *
from java.awt import *
import java

import ca.nengo
import sys
if 'lib/iText-5.0.5.jar' not in sys.path:
    sys.path.append('lib/iText-5.0.5.jar')




class ToolBar(ca.nengo.ui.lib.world.handlers.SelectionHandler.SelectionListener,ca.nengo.ui.lib.world.WorldObject.ChildListener):
    def __init__(self):
        self.ng=ca.nengo.ui.NengoGraphics.getInstance()
        self.selection=self.ng.getSelectedObj()
        self.toolbar=JToolBar("Nengo actions",floatable=False)
        
        self.button_run=self.make_button('play',self.do_run,'run',enabled=False)
        self.button_pdf=self.make_button('pdf',self.do_pdf,'save as pdf')
        self.toolbar.add(self.make_button('open',self.do_open,'open file'))
        self.toolbar.add(self.button_run)
        self.toolbar.add(self.button_pdf)
        self.toolbar.add(self.make_button('inspect',self.do_inspect,'inspect'))

        ca.nengo.ui.lib.world.handlers.SelectionHandler.addSelectionListener(self)
        self.ng.getWorld().getGround().addChildrenListener(self)

    def make_button(self,icon,func,tip,**args):
        return JButton(icon=ImageIcon('python/images/%s.png'%icon),rolloverIcon=ImageIcon('python/images/%s-pressed.png'%icon),
                       actionPerformed=func,toolTipText=tip,
                       borderPainted=False,focusPainted=False,contentAreaFilled=False,margin=java.awt.Insets(0,0,0,0),
                       verticalTextPosition=AbstractButton.BOTTOM,horizontalTextPosition=AbstractButton.CENTER,**args)


    def childAdded(self,obj):
        self.update()
    def childRemoved(self,obj):
        self.update()
    def objectFocused(self,obj):
        self.update()
        
    def update(self):
        self.selection=self.ng.getSelectedObj()

        if self.selection is not None and self.ng.configPane.toJComponent().isAuxVisible():
            self.ng.configPane.configureObj(self.selection.model)
        
            


        net=self.get_current_network()
        if net is None:
            self.button_run.enabled=False
            self.button_run.toolTipText='run'
        else:
            self.button_run.enabled=True
            self.button_run.toolTipText='run '+net.name
        
    def get_current_network(self):
        network=self.selection
        if network is not None:
            if network.networkParent is not None:
                network=network.networkParent
        else:
            for wo in ng.world.ground.children:
                if isinstance(wo,ca.nengo.ui.models.nodes.UINetwork):
                    network=wo
                    break
        return network        
        

    def do_inspect(self,event):
        pane=self.ng.configPane.toJComponent()
        pane.setAuxVisible(not pane.isAuxVisible())
        
        #self.inspect.visible=not self.inspect.visible
        #size=self.ng.size
        #self.ng.pack()
        #self.ng.size=size
        
    def do_open(self,event):
        ca.nengo.ui.actions.OpenNeoFileAction(ng).doAction()
    def do_run(self,event):
        network=self.get_current_network()
        if network is not None:         
            ca.nengo.ui.actions.RunInteractivePlotstAction(network).doAction()
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
            universe.paint(g2)
            #self.view.area.pdftemplate=None
            g2.dispose()

            cb.addTemplate(tp,20,0)
            doc.close()
        

    def init(self,frame):
        size=frame.size
        frame.getContentPane().add(self.toolbar,BorderLayout.PAGE_START)
        frame.pack()
        frame.size=size


        


toolbar=ToolBar()
ng=ca.nengo.ui.NengoGraphics.getInstance()
toolbar.init(ng)

