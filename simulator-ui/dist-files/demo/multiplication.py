import nef

# Create the network object
net = nef.Network('Multiplication')

# Create a controllable input function with a starting value of 8
net.make_input('input A', [8])
# Create a controllable input function with a starting value of 5
net.make_input('input B', [5])

# Make two 1-D population with 100 neurons and radius of 10
net.make('A', 100, 1, radius=10)
net.make('B', 100, 1, radius=10)

# Make a 2-D population with 225 neurons, and a larger radius
# (so 10, 10 input still fits within the circle of that radius)
net.make('Combined', 225, 2, radius=15)

# Make a 1-D population with 100 neurons and radius of 100
net.make('D', 100, 1, radius=100)

# Connect all the relevant objects
net.connect('input A', 'A')
net.connect('input B', 'B')
net.connect('A', 'Combined', transform=[1, 0])
net.connect('B', 'Combined', transform=[0, 1])

def product(x):
    return x[0] * x[1]

# Create the output connection mapping the 1D function `product`
net.connect('Combined', 'D', func=product)

net.add_to_nengo()
