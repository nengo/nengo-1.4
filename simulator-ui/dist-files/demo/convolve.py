import hrr
import nef
import nef.convolution

D = 10

vocab = hrr.Vocabulary(D, include_pairs=True)
vocab.parse('a+b+c+d+e')

# Create the network object
net = nef.Network('Convolution')

# Make three population of D * 30 neurons and D dimensions
net.make('A', D * 30, D)
net.make('B', D * 30, D)
net.make('C', D * 30, D)

# Construct a convolution network using A, B, C and 100 neurons per dimension
conv = nef.convolution.make_convolution(net, '*', 'A', 'B', 'C', 100)

net.add_to_nengo()
