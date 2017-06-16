import nef

# Create the network object
net = nef.Network('Oscillator')

# Create a controllable input function to kick off the oscillator
net.make_input('input', [1, 0], zero_after_time=0.1)

# Make a population with 200 neurons, 2 dimensions
net.make('A', 200, 2)

net.connect('input', 'A')
# Recurrently connect A with the connection matrix for an oscillator
net.connect('A', 'A', [[1, 1], [-1, 1]], pstc=0.1)

net.add_to_nengo()
