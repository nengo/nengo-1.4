import nef

# Create the network object
net = nef.Network('Squaring')

# Create a controllable input function with a starting value of 0
net.make_input('input', [0])

# Make two populations with 100 neurons, 1 dimension
net.make('A', 100, 1)
net.make('B', 100, 1, storage_code='B')

# Make relevant connections
net.connect('input', 'A')
net.connect('A', 'B', func=lambda x: x[0] * x[0])

net.add_to_nengo()
