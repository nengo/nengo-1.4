N=30
D=1
import random
import nef
from ca.nengo.model.plasticity.impl import *
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units


net=nef.Network('Learn Communication')
input=FunctionInput('input',[FourierFunction(.1, 10,.5,i,0) for i in range(D)],Units.UNK)
net.add(input)

A=net.make('A',N,D)
B=net.make('B',N,D)
net.connect(input,A)

error=net.make('error',N,D)
net.connect(A,error)
net.connect(B,error,weight=-1)


def rand_weights(w):
    for i in range(len(w)):
        for j in range(len(w[0])):
            w[i][j] = random.uniform(-1e-4,1e-4)
    return w

net.connect(A,B,weight_func=rand_weights)
net.connect(error,B,modulatory=True)

inFcn = InSpikeErrorFunction([n.scale for n in B.nodes],B.encoders)
#inFcn.setLearningRate(5e-4) 
outFcn = OutSpikeErrorFunction([n.scale for n in B.nodes],B.encoders)
#outFcn.setLearningRate(5e-4) 
learn_rule=SpikePlasticityRule(inFcn, outFcn, 'AXON', 'error')
B.setPlasticityRule('A',learn_rule)


stop=net.make_input('stop learning',[0])
error.addTermination('gate',[[-10]]*N,0.01,False)
net.connect(stop,error.getTermination('gate'))


net.add_to(world)



