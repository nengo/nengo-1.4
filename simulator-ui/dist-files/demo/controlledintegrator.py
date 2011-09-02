import nef

net=nef.Network('Controlled Integrator') #Create the network object
input=net.make_input('input',[0])  #Create a controllable input function with 
                                   #a starting value of 0
input.functions=[ca.nengo.math.impl.PiecewiseConstantFunction(
    [0.2,0.3,0.44,0.54,0.8,0.9],
    [0,5,0,-10,0,5,0])] #Change the input function (that was 0) to this
                        #piecewise step function
control=net.make_input('control',[1])  #Create a controllable input function
                                       #with a starting value of 1
A=net.make('A',225,2,radius=1.5,
    quick=True) #Make a population with 225 neurons, 2 dimensions, and a 
                #larger radius to accommodate large simulataneous inputs
net.connect(input,A,transform=[0.1,0],pstc=0.1) #Connect all the relevant
                                                #objects with the relevant 1x2
                                                #mappings, postsynaptic time
                                                #constant is 10ms
net.connect(control,A,transform=[0,1],pstc=0.1)
def feedback(x):
    return x[0]*x[1]
net.connect(A,A,transform=[1,0],func=feedback,pstc=0.1) #Create the recurrent
                                                        #connection mapping the
                                                        #1D function 'feedback'
                                                        #into the 2D population
                                                        #using the 1x2 transform
net.add_to_nengo()


