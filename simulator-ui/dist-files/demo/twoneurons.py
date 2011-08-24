import nef

net=nef.Network('Two Neurons') #Create the network object
input=net.make_input('input',[-0.45]) #Create a controllable input function with a starting value of -.45
neuron=net.make('neuron',2,1,max_rate=(100,100),intercept=(-0.5,-0.5),encoders=[[1],[-1]],noise=3) #Make 2 neurons, 1 dimension, max firing rates evenly distributed between 100 and 100, x-intercepts evenly distributed between -.5 and -.5, encoders of 1 and -1 and noise at every step with a variance of 3 
net.connect(input,neuron) #Connect the input to the neurons
net.add_to_nengo()


