import nef

# Create the network
net = nef.Network('Many neurons')

# Create a controllable input with a starting value of -.45
net.make_input('input', [-0.45])

# Make a 1-D population of 100 neurons, with injected noise variance 1
net.make('neurons', neurons=100, dimensions=1, noise=1)

# Connect the input to the neuron
net.connect('input', 'neurons')
net.add_to_nengo()
