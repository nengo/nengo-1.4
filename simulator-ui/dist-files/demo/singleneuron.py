import nef

net=nef.Network('Single Neuron') #Create the network object
input=net.make_input('input',[-0.45]) #Create a controllable input function 
                                      #with a starting value of -.45
neuron=net.make('neuron',1,1,max_rate=(100,100),intercept=(-0.5,-0.5),
    encoders=[[1]],noise=3) #Make 1 neuron, 1 dimension, a max firing 
                            #rate evenly distributed between 100 and 100, 
                            #an x-intercept evenly distributed between -.5 
                            #and -.5, an encoder of 1 and noise at every 
                            #step with a variance of 3 
net.connect(input,neuron) #Connect the input to the neuron
net.add_to_nengo()


