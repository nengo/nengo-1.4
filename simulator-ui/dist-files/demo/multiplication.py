import nef

net=nef.Network('Multiply') #Create the network object
inputA=net.make_input('inputA',[8]) #Create a controllable input function with a starting value of 8
inputB=net.make_input('inputB',[5]) #Create a controllable input function with a starting value of 5
A=net.make('A',100,1,radius=10,quick=True) #Make a population with 100 neurons, 1 dimensions, a radius of 10 (default is 1)
B=net.make('B',100,1,radius=10,quick=True,storage_code='B') #Make a population with 100 neurons, 1 dimensions, a radius of 10 (default is 1), storage_code works with quick to reuse an appropriate population if created before
C=net.make('Combined',225,2,radius=15,quick=True) #Make a population with 225 neurons, 2 dimensions, and set a larger radius (so 10,10 input still fits within the circle of that radius)
D=net.make('D',100,1,radius=100,quick=True,storage_code='D') #Make a population with 100 neurons, 1 dimensions, a radius of 10 (default is 1)
net.connect(inputA,A) #Connect all the relevant objects
net.connect(inputB,B)
net.connect(A,C,transform=[1,0]) #Connect with the given 1x2D mapping matrix
net.connect(B,C,transform=[0,1])
def product(x):
    return x[0]*x[1]
net.connect(C,D,func=product) #Create the output connection mapping the 1D function 'product'
net.add_to_nengo()


