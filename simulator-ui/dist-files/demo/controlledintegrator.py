import nef

# Create the network object
net = nef.Network('Controlled integrator')

# Create a controllable input that goes to 5 at time 0.2s,
# to 0 at time 0.3s, and so on
net.make_input('input', {0.2: 5, 0.3: 0, 0.44: -10, 0.54: 0, 0.8: 5, 0.9: 0})

# Create a controllable input function with starting value of 1
net.make_input('control', [1])
# Make a population with 225 neurons, 2 dimensions, and a
# larger radius to accommodate large simulataneous inputs
net.make('A', 225, 2, radius=1.5)

# Connect all the relevant objects with the relevant 1x2 mappings
# Postsynaptic time constant is 100 ms
net.connect('input', 'A', transform=[0.1, 0], pstc=0.1)
net.connect('control', 'A', transform=[0, 1], pstc=0.1)

def feedback(x):
    return x[0] * x[1]

# Create the recurrent connection mapping the 1D function "feedback"
# into the 2D population using the 1x2 transform
net.connect('A', 'A', transform=[1, 0], func=feedback, pstc=0.1)
net.add_to_nengo()
