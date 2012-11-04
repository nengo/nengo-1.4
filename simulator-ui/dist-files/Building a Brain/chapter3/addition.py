import nef

net=nef.Network('Scalar Addition (pre-built)')
net.make_input('inputA',[0])
net.make_input('inputB',[0])
net.make('A',100,1)
net.make('B',100,1)
net.make('C',100,1, radius=2)
net.connect('inputA','A')
net.connect('inputB','B')
net.connect('A','C')
net.connect('B','C')
net.add_to_nengo()


