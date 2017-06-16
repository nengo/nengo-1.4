import nef

# Create the network
net = nef.Network('Single Neuron')

# Create a controllable input with a starting value of -0.45
net.make_input('input', [-0.45])

net.make('neuron',
         neurons=1,              # Make 1 neuron
         dimensions=1,           # representing 1 dimension
         max_rate=(100, 100),    # with a maximum firing rate of 100
         intercept=(-0.5, -0.5), # with an x-intercept of -0.5
         encoders=[[1]],         # with an encoder of 1
         noise=3)                # with noise of variance 3

# Connect the input to the neuron
net.connect('input', 'neuron')
net.add_to_nengo()
