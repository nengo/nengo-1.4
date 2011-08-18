Simple Vehicle
================================================

from __future__ import generators
import space
from ca.nengo.model.impl import *
from ca.nengo.model import *
from ca.nengo.model.nef.impl import *
from ca.nengo.model.neuron.impl import *
from ca.nengo.model.neuron import *
from ca.nengo.math.impl import *
from ca.nengo.model.plasticity.impl import *
from ca.nengo.util import *
from ca.nengo.plot import *
from java.awt import Color
import ccm
import random

dt=0.001
N=10
pstc=0.01

class Bot(space.MD2):
    def __init__(self):
        space.MD2.__init__(self,'python/md2/dalekx/tris.md2', 'python/md2/dalekx/imperial.png', 
                            scale=0.02, mass=800,overdraw_scale=1.4)
        z=0
        s=0.7
        r=0.7
        self.wheels=[space.Wheel(-s,0,z,radius=r),
                     space.Wheel(s,0,z,radius=r),
                     space.Wheel(0,s,z,friction=0,radius=r),
                     space.Wheel(0,-s,z,friction=0,radius=r)]
        
    def start(self):
        self.sch.add(space.MD2.start,args=(self,))
        self.range1=space.RangeSensor(0.3,1,0,maximum=6)
        self.range2=space.RangeSensor(-0.3,1,0,maximum=6)
        self.wheel1=0
        self.wheel2=0
        while True:        
            r1=self.range1.range
            r2=self.range2.range
            input1.functions[0].value=r1-1.8
            input2.functions[0].value=r2-1.8
            
            f1=motor1.getOrigin('X').getValues().getValues()[0]
            f2=motor2.getOrigin('X').getValues().getValues()[0]
            
            self.wheels[1].force=f1*600
            self.wheels[0].force=f2*600
            yield dt
    

try:
    world.remove(network)
except:
    pass
    
network = NetworkImpl()
network.name='Braitenberg'

ef = NEFEnsembleFactoryImpl()
ef.nodeFactory=LIFNeuronFactory(tauRC=.020, tauRef=.001, maxRate=IndicatorPDF(200,400), intercept=IndicatorPDF(-1,1))

sense1=ef.make("right input",N,1)
sense2=ef.make("left input",N,1)
motor1=ef.make("right motor",N,1)
motor2=ef.make("left motor",N,1)
network.addNode(sense1)
network.addNode(sense2)
network.addNode(motor1)
network.addNode(motor2)
sense2.addDecodedTermination('input',[[1]],pstc,False)
sense1.addDecodedTermination('input',[[1]],pstc,False)
motor2.addDecodedTermination('input',[[1]],pstc,False)
motor1.addDecodedTermination('input',[[1]],pstc,False)
network.addProjection(sense1.getOrigin('X'),motor2.getTermination('input'))
network.addProjection(sense2.getOrigin('X'),motor1.getTermination('input'))

input1=FunctionInput('right eye',[ConstantFunction(1,0)],Units.UNK)
network.addNode(input1)
input2=FunctionInput('left eye',[ConstantFunction(1,0)],Units.UNK)
network.addNode(input2)
network.addProjection(input1.getOrigin('origin'),sense1.getTermination('input'))
network.addProjection(input2.getOrigin('origin'),sense2.getTermination('input'))

world.add(network)


class Room(space.Room):
    def __init__(self):
        space.Room.__init__(self,10,10,dt=0.01)
    def start(self):    
        self.bot=Bot()
        self.add(self.bot, 0, 0,1)
        #view=space.View(self, (0, -10, 5))


        for i in range(6):
            self.add(space.Box(1,1,1,mass=1,color=Color(0x8888FF),flat_shading=False),random.uniform(-5,5),random.uniform(-5,5),random.uniform(4,6))
        
        self.sch.add(space.Room.start,args=(self,))
            
    
r=ccm.nengo.create(Room)    
network.addNode(r)



