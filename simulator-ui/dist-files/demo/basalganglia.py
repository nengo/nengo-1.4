import nef
import nps

D = 5

# Create the network object
net = nef.Network('Basal Ganglia')

# Create a controllable input function with starting value all zeros
net.make_input('input', [0] * D)

# Make a direct mode population with 100 neurons and 5 dimensions
net.make('output', 1, D, mode='direct')

# Make a basal ganglia model with 50 neurons per action
nps.basalganglia.make_basal_ganglia(
    net, 'input', 'output', D, same_neurons=False, neurons=50)
net.add_to_nengo()
