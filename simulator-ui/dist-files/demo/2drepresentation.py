import nef

# Create the network
net = nef.Network('2D Representation')

# Create a controllable 2-D input with starting value of (0, 0)
net.make_input('input', [0, 0])

# Create a population with 100 neurons representing 2 dimensions
net.make('neurons', 100, 2)

# Connect the input to the neurons
net.connect('input', 'neurons')
net.add_to_nengo()
