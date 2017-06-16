import nef

# Create the network object
net = nef.Network('Communication channel')

# Create a controllable input function with a starting value of 0.5
net.make_input('input', [0.5])

# Make two populations with 100 neurons, 1 dimension
net.make('A', 100, 1)
net.make('B', 100, 1)

# Connect all the relevant objects
net.connect('input', 'A')
net.connect('A', 'B')
net.add_to_nengo()
