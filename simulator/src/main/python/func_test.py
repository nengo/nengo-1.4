
import nef

net=nef.Network('Function Test',seed=3)
net.make_input('input',0.5)
net.make('A',100,1)
net.connect('input','A')
net.make('B',100,3)

def square(x):
    return x[0]*x[0],-x[0],x[0]

net.connect('A','B',func=square,pstc=0.1)

for i in range(1000):
    net.run(0.001)
    print i,net.node['B'].accumulator[0.1].value.get_value()    

