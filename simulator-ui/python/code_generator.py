import ca.nengo

def generate(network):
    code=[]
    code.append('import nef')
    code.append('net=nef.Network("%s")'%network.name)
    for node in network.nodes:
        if hasattr(node,'getEnsembleFactory'):
            nf=node.getEnsembleFactory().getNodeFactory()
            c="net.make('%s',neurons=%d,dimensions=%d,tau_rc=%g,tau_ref=%g,intercept=(%g,%g),max_rate=(%g,%g))"%(node.name,node.neurons,node.dimension,nf.tauRC,nf.tauRef,nf.intercept.low,nf.intercept.high,nf.maxRate.low,nf.maxRate.high)
            code.append(c)    
    
    importable=['sin','cos','tan','asin','acos','atan','exp','log','pow','sqrt']        
            
    imported_funcs=[]        
    for p in network.projections:
        origin=p.origin
        termination=p.termination  
        
        connect="'%s','%s'"%(origin.node.name,termination.node.name)
        
        if origin.name!='X':
            funcs=[]
            for f in origin.functions:
                if isinstance(f,ca.nengo.math.impl.PostfixFunction):
                    exp=f.expression
                    exp=exp.replace('^','**')
                    exp=exp.replace('!',' not ')
                    exp=exp.replace('&',' and ')
                    exp=exp.replace('|',' or ')                    
                    exp=exp.replace('ln','log')
                    for f in importable:
                        if f in exp and not f in imported_funcs:
                            imported_funcs.append(f)
                    for i in range(origin.node.dimension):
                        exp=exp.replace('x%d'%i,'x[%d]'%i)
                    funcs.append(exp)
                else:
                    funcs.append('0')
            code.append('def function(x):')
            code.append('    return [%s]'%(','.join(funcs)))
            connect=connect+',func=function,origin_name="%s"'%origin.name                
        
        
        tr='['+','.join(['['+','.join(['%g'%x for x in row])+']' for row in termination.transform])+']'
        
        connect+=',transform=%s'%tr
        # TODO: look for special cases of identify matrixes (and maybe weight, index_pre, and index post?)
        
        code.append('net.connect(%s,pstc=%g)'%(connect,termination.tau))
    if len(imported_funcs)>0:
        code[1:1]=['from math import %s'%','.join(imported_funcs)]
    return '\n'.join(code)
    
    
    
    
import java
import javax
from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import ca.nengo
    
class CodeView(ca.nengo.util.VisiblyMutable.Listener):
    def __init__(self,network):
        self.network=network
        self.frame=JFrame(network.name)
        self.frame.visible=True
        self.frame.layout=BorderLayout()
        self.frame.size=(800,600)        
        
        
        self.code=JTextArea(generate(self.network),editable=False)
        scroll=JScrollPane(self.code)
        scroll.viewport.scrollMode=JViewport.SIMPLE_SCROLL_MODE   # not sure why this is needed -- if not done, text is corrupted when scrolling
        self.frame.add(scroll)
        
        self.network.addChangeListener(self)
        
    def changed(self,event):
        self.code.text=generate(self.network)
        
        
        
"""        
import nef
net=nef.Network("GenTest")
net.make('A',neurons=100,dimensions=2,tau_rc=0.02,tau_ref=0.002,intercept=(-1,1),max_rate=(200,400))
net.make('B',neurons=100,dimensions=2,tau_rc=0.02,tau_ref=0.002,intercept=(-1,1),max_rate=(30,60))
net.connect('A','B',transform=[[1,0],[0,1]],pstc=0.01)        

net.add_to_nengo()
CodeView(net.network)
"""
