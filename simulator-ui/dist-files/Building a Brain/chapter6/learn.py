N=60
D=1
import random
import nef
from ca.nengo.model.plasticity.impl import *
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units


net=nef.Network('Learning')
input=FunctionInput('input',[FourierFunction(.1, 10,.5,12)],Units.UNK)
net.add(input)

A=net.make('pre',N,D)
B=net.make('post',N,D)
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

# non-decoded termination (to learn transformation)   
# Set learning rule on the non-decoded termination
# Code ripped directly from nef_core
in_args = {'a2Minus':  6.6e-3,
    'a3Minus':  3.1e-3,
    'tauMinus': 33.7,
    'tauX':     101.0}
                   
inFcn = InSpikeErrorFunction([n.scale for n in B.nodes],B.encoders,
    in_args['a2Minus'],in_args['a3Minus'],in_args['tauMinus'],in_args['tauX'])
    
inFcn.setLearningRate(5.0e-7)
                    
out_args = {'a2Plus':  8.8e-11,
    'a3Plus':  5.3e-2,
    'tauPlus': 16.8,
    'tauY':    125.0}
                    
outFcn = OutSpikeErrorFunction([n.scale for n in B.nodes],B.encoders,
    out_args['a2Plus'],out_args['a3Plus'],out_args['tauPlus'],out_args['tauY'])
    
outFcn.setLearningRate(5.0e-7)
rule=SpikePlasticityRule(inFcn, outFcn, 'AXON', 'error')
B.setPlasticityRule('pre',rule)

stop=net.make_input('switch',[0])
gate=net.make('gate', 100, 1, intercept=(-0.7, 0), encoders=[[-1]])
error.addTermination('gate',[[-10]]*N,0.01,False)
def addOne(x):
    return [x[0]+1]            
net.connect(gate, error.getTermination('gate'), func=addOne, origin_name='xBiased')
net.connect(stop, gate)

actual = net.make('actual error', 1, 1, mode='direct')
net.connect(A,actual)
net.connect(B,actual,weight=-1)

net.add_to(world)



