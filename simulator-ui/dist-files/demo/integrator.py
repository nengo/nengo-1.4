import nef

net=nef.Network('Integrator')
input=net.make_input('input',[1])
input.functions=[ca.nengo.math.impl.PiecewiseConstantFunction([0.2,0.3,0.44,0.54,0.8,0.9],[0,5,0,-10,0,5,0])]
A=net.make('A',100,1,quick=True)
net.connect(input,A,weight=0.1,pstc=0.1)
net.connect(A,A,pstc=0.1)
net.add_to(world)


