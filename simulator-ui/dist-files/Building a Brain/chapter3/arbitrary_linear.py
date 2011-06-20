import nef

net=nef.Network('Arbitrary Linear Transformations')
input=net.make_input('input',[0.5, -0.5])
A=net.make('A',200,2,quick=True)
B=net.make('B',200,3,quick=True)
net.connect(input,A)
net.connect(A, B, transform=[[0, 1], [1, 0], [0.5, 0.5]])
net.add_to(world)