import nef

net=nef.Network('Arbitrary Linear Transformations')
input=net.make_input('input',[0.5, -0.5])
x=net.make('x',200,2,quick=True)
z=net.make('z',200,3,quick=True)
net.connect(input,x)
net.connect(x, z, transform=[[0, 1], [1, 0], [0.5, 0.5]])
net.add_to(world)