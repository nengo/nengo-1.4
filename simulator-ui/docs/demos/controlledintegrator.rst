Controlled Integrator
============================

import nef

net=nef.Network('Controlled Integrator')
input=net.make_input('input',[0])
input.functions=[ca.nengo.math.impl.PiecewiseConstantFunction([0.2,0.3,0.44,0.54,0.8,0.9],[0,5,0,-10,0,5,0])]
control=net.make_input('control',[1])
A=net.make('A',225,2,radius=1.5,quick=True)
net.connect(input,A,transform=[0.1,0],pstc=0.1)
net.connect(control,A,transform=[0,1],pstc=0.1)
def feedback(x):
    return x[0]*x[1]
net.connect(A,A,transform=[1,0],func=feedback,pstc=0.1)
net.add_to(world)


