import nef

net=nef.Network('Arbitrary Linear Transformations')
input=net.make_input('input',[0, 0, 0, 0, 0])
A=net.make('A',200,5,quick=True)
B=net.make('B',200,4,quick=True)
net.connect(input,A)
net.connect(A, B, transform=[[1, 0, 0, 0, 0.1], [0, 1, 0, 0, 0.2], [0, 0, 1, 0, 0.3], [0, 0, 0, 1, 0.4]])
net.add_to(world)