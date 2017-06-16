import nef

# Create the network object
net = nef.Network('Addition')

# Create two controllable inputs with starting value 0
net.make_input('input A', [0])
net.make_input('input B', [0])

# Create three populations with 100 neurons, 1 dimension each
net.make('A', 100, 1)
net.make('B', 100, 1)
net.make('C', 100, 1)

# Connect all the relevant objects
net.connect('input A', 'A')
net.connect('input B', 'B')
net.connect('A', 'C')
net.connect('B', 'C')
net.add_to_nengo()
