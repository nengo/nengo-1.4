A Simple Harmonic Oscillator
============================

import nef

net=nef.Network('Oscillator')
input=net.make_input('input',[1])
input.functions=[ca.nengo.math.impl.PiecewiseConstantFunction([0.2,0.3],[0,5,0])]
A=net.make('A',200,2,quick=True)
net.connect(input,A,weight=0.1,pstc=0.1)
net.connect(A,A,[[0 10][-10 0]]pstc=0.1)
net.add_to(world)


