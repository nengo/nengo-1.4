import nef

# Create the network object
net = nef.Network('Combining')

# Create two controllable input function with starting values of 0
net.make_input('input A', [0])
net.make_input('input B', [0])

# Make two populations with 100 neurons, 1 dimension
net.make('A', 100, 1)
net.make('B', 100, 1)

# Make a population with 100 neurons, 2 dimensions, and a larger radius
# (so 1,1 input still fits within the circle of that radius)
net.make('C', 100, 2, radius=1.5)
# Connect all the relevant objects (default connection is identity)
net.connect('input A', 'A')
net.connect('input B', 'B')
# Connect with the given 1D to 2D mapping matrix
net.connect('A', 'C', transform=[1, 0])
net.connect('B', 'C', transform=[0, 1])
net.add_to_nengo()
