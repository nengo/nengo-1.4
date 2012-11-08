import nef

net=nef.Network('Arbitrary Linear Transformations (pre-built)')
net.make_input('input',[0.5, -0.5])
net.make('x',200,2)
net.make('z',200,3)
net.connect('input','x')
net.connect('x', 'z', transform=[[0, 1], [1, 0], [0.5, 0.5]])
net.add_to_nengo()