statement="BLUE*CIRCLE+RED*TRIANGLE"

D=64

import nef
import nps

prods=nps.ProductionSet()

prods.add(dict(visual='STATEMENT'),dict(visual_to_wm=True))
prods.add(dict(visual='QUESTION'),dict(wm_deconv_visual_to_motor=True))

import hrr
for i in range(50):
    vocab=hrr.Vocabulary(D,max_similarity=0.05)
    a=vocab.parse('(%s)*~(%s)'%(statement,'RED')).compare(vocab.parse('TRIANGLE'))
    b=vocab.parse('(%s)*~(%s)'%(statement,'CIRCLE')).compare(vocab.parse('BLUE'))
    if min(a,b)>0.65: break

net=nef.Network('Question v2')
net.add_to(world)

model=nps.NPS(net,prods,D,direct_convolution=True,direct_buffer=['visual'],neurons_buffer=50)

model.add_buffer_feedback(wm=1,pstc=0.2)

from ca.nengo.model.impl import FunctionInput
from ca.nengo.math.impl import AbstractFunction
from ca.nengo.model import Units
import hrr


class Input(nef.SimpleNode):
    def __init__(self,name):
        nef.SimpleNode.__init__(self,name)
        self.v1=vocab.parse('STATEMENT+RED*TRIANGLE').v
        self.v2=vocab.parse('STATEMENT+BLUE*CIRCLE').v
        self.v3=vocab.parse('QUESTION+RED').v
        self.v4=vocab.parse('QUESTION+CIRCLE').v
    def origin_x(self):
        if 0.1<self.t_start<0.3:
            return self.v1
        elif 0.35<self.t_start<0.5:
            return self.v2
        elif 0.7<self.t_start<0.9:
            return self.v3
        elif 0.9<self.t_start<1.1:
            return self.v4            
        else:
            return [0]*D

inv=Input('inv')
net.add(inv)
net.connect(inv.getOrigin('x'),'buffer_visual')


net.network.getNode('prod').name='thalamus'

net.network.getNode('buffer_visual').name='visual'
net.network.getNode('buffer_wm').name='memory'
net.network.getNode('buffer_motor').name='motor'

