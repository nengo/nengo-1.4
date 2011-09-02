import nef

net=nef.Network('Many Neurons') #Create the network object
input=net.make_input('input',[-0.45]) #Create a controllable input function 
                                      #with a starting value of -.45
neuron=net.make('neurons',100,1,noise=1,
    quick=True) #Make a population with 100 neurons, 1 dimensions, and noise
                #variance of 1 (added at every step)
net.connect(input,neuron) #Connect the input to the population
net.add_to_nengo()

