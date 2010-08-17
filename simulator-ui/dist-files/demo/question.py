statement="blue*circle+red*triangle"
question="red"

D=64

import nef
import nps

prods=nps.ProductionSet()

prods.add(dict(visual='statement'),dict(visual_to_wm=True))
prods.add(dict(visual='question'),dict(wm_deconv_visual_to_motor=True))


net=nef.Network('Question')
net.add_to(world)

model=nps.NPS(net,prods,D,direct_convolution=True,direct_buffer=['visual'],neurons_buffer=50)

model.add_buffer_feedback(wm=1,pstc=0.1)

from ca.nengo.model.impl import FunctionInput
from ca.nengo.math.impl import AbstractFunction
from ca.nengo.model import Units
import hrr

class InputFunction(AbstractFunction):
    serialVersionUID=1
    def __init__(self,index):
        AbstractFunction.__init__(self,1)
        self.index=index
        self.statement=hrr.Vocabulary.defaults[D].parse('statement+'+statement).v
        self.question=hrr.Vocabulary.defaults[D].parse('question+'+question).v
        
    def map(self,x):
        t=x[0]
        
        if t<0.3:
            return self.statement[self.index]
        elif t<0.35:
            return 0
        else:
            return self.question[self.index]

iv=FunctionInput('input_visual',[InputFunction(i) for i in range(D)],Units.UNK)
net.add(iv)
net.connect(iv,'buffer_visual')




