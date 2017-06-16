import nef

# Create the network
net = nef.Network('Two neurons')
# Create a controllable input with a starting value of -.45
net.make_input('input', [-0.45])

net.make('neuron',
         neurons=2,               # Make 2 neurons
         dimensions=1,            # representing 1 dimension,
         max_rate=(100, 100),     # with firing rate of 100 Hz,
         intercept=(-0.5, -0.5),  # tuning curve x-intercept of -0.5,
         encoders=[[1], [-1]],    # encoders of 1 and -1,
         noise=3)                 # and noise with variance 3.

# Connect the input to the neuron
net.connect('input', 'neuron')
net.add_to_nengo()
