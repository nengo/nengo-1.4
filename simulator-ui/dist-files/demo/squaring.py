import nef

net=nef.Network('Squaring') #Create the network object
input=net.make_input('input',[0]) #Create a controllable input function with a starting value of 0
A=net.make('A',100,1,quick=True) #Make a population with 100 neurons, 1 dimensions
B=net.make('B',100,1,quick=True,storage_code='B') #Make a population with 100 neurons, 1 dimensionsv
net.connect(input,A) #Connect the input to A
net.connect(A,B,func=lambda x: x[0]*x[0]) #Connect A and B with the defined function approximated in that connection
net.add_to_nengo()


