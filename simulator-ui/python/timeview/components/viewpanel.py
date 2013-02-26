import java
import javax
from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *

import math

from timeview.components import core

from ca.nengo.model.impl import NetworkImpl

class ViewPanel(JPanel):
    def __init__(self, network):
        JPanel.__init__(self)
        self.network = network
        self.nodes = {}

    def paintProjection(self, oname, tname, g):
        if oname in self.nodes and tname in self.nodes:
            c1 = self.nodes[oname]
            c2 = self.nodes[tname]
            if c1.visible and c2.visible:

                arrowsize = 7.0
                sin60 = -math.sqrt(3) / 2
                cos60 = -0.5

                if c1 is c2:
                    scale = 0.1
                    x = c1.x + c1.width / 2
                    y = c1.y + c2.height / 2
                    g.drawOval(int(c1.x - c1.width * scale), int(c1.y - c1.height / 2 - c1.height * scale), int(c1.width * (1 + scale * 2)), int(c2.height * (1 + scale * 2)))
                    xc = x
                    yc = y - c1.height - c1.height * scale
                    xa = -arrowsize
                    ya = 0.0
                else:
                    x1 = c1.x + c1.width / 2
                    x2 = c2.x + c2.width / 2
                    y1 = c1.y + c1.height / 2
                    y2 = c2.y + c2.height / 2
                    g.drawLine(x1, y1, x2, y2)

                    place = 0.4

                    xc = (x1 * place + x2 * (1 - place)) + 0.5
                    yc = (y1 * place + y2 * (1 - place)) + 0.5

                    length = math.sqrt(float((x2 - x1) ** 2 + (y2 - y1) ** 2))
                    if length == 0:
                        xa = arrowsize
                        ya = 0.0
                    else:
                        xa = (x2 - x1) * arrowsize / length
                        ya = (y2 - y1) * arrowsize / length

                g.fillPolygon([int(xc + xa), int(xc + cos60 * xa - sin60 * ya), int(xc + cos60 * xa + sin60 * ya)],
                              [int(yc + ya), int(yc + sin60 * xa + cos60 * ya), int(yc - sin60 * xa + cos60 * ya)],
                              3)

    def paintProjections(self, network, g, prefix=""):
        for p in network.projections:
            origin = p.origin
            termination = p.termination

            oname = prefix + origin.node.name
            tname = prefix + termination.node.name

            self.paintProjection(oname, tname, g)

            # Check to see if the termination or origin is wrapped. If it is, then use that,
            # otherwise, just use the termination / origin itself. 
            # Note: This may or may not have unintended consequences when checking what object
            #       the termination / origin is connected to (see next code block - 
            #       ... isinstance(termination.node, NetworkImpl) ...)
            if hasattr(termination, 'wrappedTermination' ):
                termination = termination.wrappedTermination
            if hasattr(origin, 'wrappedOrigin' ):
                origin = origin.wrappedOrigin
    
            if isinstance(termination.node, NetworkImpl):
                self.paintProjection(oname, tname + ':' + termination.node.name, g)
                if isinstance(origin.node, NetworkImpl):
                    self.paintProjection(oname + ':' + origin.name, tname + ':' + termination.name, g)
            if isinstance(origin.node, NetworkImpl):
                self.paintProjection(oname + ':' + origin.node.name, tname, g)

        for n in network.nodes:
            if isinstance(n, NetworkImpl):
                self.paintProjections(n, g, prefix=prefix + n.name + ':')

    def paintComponent(self, g):
        g.color = Color.white
        g.fillRect(0, 0, self.width, self.height)

        g.color = Color.black
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        self.paintProjections(self.network, g)
        
    def tick(self, t):
        for c in self.getComponents():
            if isinstance(c, core.DataViewComponent):
                c.tick(t)

    def screenshot(self, filename):
        # TODO: Make this function compatible with both mac and windows systems. The current solution
        #       is kinda hacky.

        #pt=self.getLocationOnScreen()
        #image=java.awt.Robot().createScreenCapture(java.awt.Rectangle(pt.x,pt.y,self.width,self.height))
        #javax.imageio.ImageIO.write(image,'png', java.io.File(filename));
        #return

        #self.visible=False
        image = java.awt.image.BufferedImage(self.width, self.height, java.awt.image.BufferedImage.TYPE_INT_ARGB)
        g = image.getGraphics()
        self.paintComponent(g)
        self.paintBorder(g)

        self.paintChildren(g)
        """
        print g

        comps=list(self.components)
        comps.reverse()
        for c in comps:
            g.translate(c.x,c.y)
            g.setClip(0,0,c.width,c.height)
            #g.color=c.foreground
            #g.font=c.font
            c.update(g)
            g.translate(-c.x,-c.y)
        #self.paintComponents(g)
        #self.paintChildren(g)
        g.setClip(0,0,self.width,self.height)
        """

        #self.paint_bubbles(g)

        javax.imageio.ImageIO.write(image, 'png', java.io.File(filename))