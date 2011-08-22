import nef

net=nef.Network('Oscillator')
input=net.make_input('input',[0,0])
input.functions=[ca.nengo.math.impl.PiecewiseConstantFunction([0.2,0.3],[0,1,0]),ca.nengo.math.impl.PiecewiseConstantFunction([0],[0,0])]
A=net.make('A',200,2,quick=False)
net.connect(input,A,weight=1,pstc=0.1)
net.connect(A,A,[[1,1],[-1,1]],pstc=0.1)
net.add_to_nengo()
