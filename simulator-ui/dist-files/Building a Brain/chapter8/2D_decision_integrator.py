import nef

net=nef.Network('2D Decision Integrator') 
input=net.make_input('input', [0, 0])  

net.make('MT', 100, 2, noise=10) 
net.make('LIP', 200, 2, noise=10) 
net.make('output', 100, 2, intercept=(0.3, 1), noise=10)

net.connect('input', 'MT', pstc=0.01)  
net.connect('MT', 'LIP', weight=0.1, pstc=0.1) 
net.connect('LIP', 'LIP', pstc=0.1)  
net.connect('LIP', 'output', pstc=0.01)

net.add_to_nengo()