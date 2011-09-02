import nef

net=nef.Network('Integrator') #Create the network object
input=net.make_input('input',[0])  #Create a controllable input function 
                                   #with a starting value of 0
input.functions=[ca.nengo.math.impl.PiecewiseConstantFunction(
    [0.2,0.3,0.44,0.54,0.8,0.9],
    [0,5,0,-10,0,5,0])] #Change the input function (that was 0) to this
                        #piecewise step function
A=net.make('A',100,1,quick=True) #Make a population with 100 neurons, 
                                 #1 dimensions
net.connect(input,A,weight=0.1,pstc=0.1) #Connect the input to the integrator,
                                         #scaling the input by .1; postsynaptic
                                         #time constant is 10ms
net.connect(A,A,pstc=0.1) #Connect the population to itself with the 
                          #default weight of 1
net.add_to_nengo()


