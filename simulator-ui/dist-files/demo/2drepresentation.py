import nef

net=nef.Network('2D Representation') #Create the network object
input=net.make_input('input',[0,0]) #Create a controllable input function 
                                    #with a starting value of 0 and 0 in 
                                    #the two dimensions
neuron=net.make('neurons',100,2,quick=True) #Make a population with 100 
                                            #neurons, 2 dimensions
net.connect(input,neuron) #Connect the input object to the neuron object, 
                          #with an identity matrix by default
net.add_to_nengo()


