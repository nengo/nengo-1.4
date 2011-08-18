A Controlled Question Answering Network
==============================================

D=16
subdim=4
N=100
seed=3

import nef
import nps
import nef.convolution
import hrr
import math
import random

random.seed(seed)

net=nef.Network('Question Answering with Control')

for i in range(50):
    vocab=hrr.Vocabulary(D,max_similarity=0.05)
    vocab.parse('CIRCLE+BLUE+RED+SQUARE')
    a=vocab.parse('(RED*CIRCLE+BLUE*SQUARE)*~(RED)').compare(vocab.parse('CIRCLE'))
    b=vocab.parse('(RED*CIRCLE+BLUE*SQUARE)*~(SQUARE)').compare(vocab.parse('BLUE'))
    if min(a,b)>0.65: break




class Input(nef.SimpleNode):
    def __init__(self,name):
        self.zero=[0]*D
        nef.SimpleNode.__init__(self,name)
        self.v1=vocab.parse('STATEMENT+RED*CIRCLE').v
        self.v2=vocab.parse('STATEMENT+BLUE*SQUARE').v
        self.v3=vocab.parse('QUESTION+RED').v
        self.v4=vocab.parse('QUESTION+SQUARE').v
    def origin_x(self):
        t=self.t_start
        if t<0.5:
          if 0.1<self.t_start<0.3:
            return self.v1
          elif 0.35<self.t_start<0.5:
            return self.v2
          else:
            return self.zero
        else:
          t=(t-0.5)%0.6
          if 0.2<t<0.4:
            return self.v3
          elif 0.4<t<0.6:
            return self.v4            
          else:
            return self.zero
inv=Input('inv')
net.add(inv)


prods=nps.ProductionSet()
prods.add(dict(visual='STATEMENT'),dict(visual_to_wm=True))
prods.add(dict(visual='QUESTION'),dict(wm_deconv_visual_to_motor=True))


model=nps.NPS(net,prods,D,direct_convolution=False,direct_buffer=['visual'],neurons_buffer=N/subdim,subdimensions=subdim)
model.add_buffer_feedback(wm=1,pstc=0.4)

net.connect(inv.getOrigin('x'),'buffer_visual')
      

net.network.getNode('prod').name='thalamus'
net.network.getNode('buffer_visual').name='visual'
net.network.getNode('buffer_wm').name='memory'
net.network.getNode('buffer_motor').name='motor'
net.network.getNode('channel_visual_to_wm').name='channel'
net.network.getNode('wm_deconv_visual_to_motor').name='*'
net.network.getNode('gate_visual_wm').name='gate1'
net.network.getNode('gate_wm_visual_motor').name='gate2'

net.add_to(world)

