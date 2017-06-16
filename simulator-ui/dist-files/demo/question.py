import math
import random

import hrr
import nef
import nef.convolution

D = 16
subdim = 4
N = 100
seed = 7

random.seed(seed)

# Create the network object
net = nef.Network('Question answering')

# Make some pseudo populations so they run well on less powerful machines
net.make('A', 1, D, mode='direct')
net.make('B', 1, D, mode='direct')
# Make an array of real populations, with 100 neurons per array element
# and D / subdim elements in the array
net.make_array('C', N, D / subdim,
               dimensions=subdim,
               quick=True,
               radius=1.0 / math.sqrt(D))
net.make('E', 1, D, mode='direct')
net.make('F', 1, D, mode='direct')

# Make a convolution network using the construct populations
conv1 = nef.convolution.make_convolution(
    net, '*', 'A', 'B', 'C', N, quick=True)
# Make a 'correlation' network (by inverting the second input)
conv2 = nef.convolution.make_convolution(
    net, '/', 'C', 'E', 'F', N, invert_second=True, quick=True)

# Create input to model
vocab = hrr.Vocabulary(D, max_similarity=0.1)
CIRCLE = vocab.parse('CIRCLE').v
BLUE = vocab.parse('BLUE').v
RED = vocab.parse('RED').v
SQUARE = vocab.parse('SQUARE').v
ZERO = [0] * D

# Create the inputs
inputA = {0.0: RED, 0.5: BLUE, 1.0: RED, 1.5: BLUE, 2.0: RED,
          2.5: BLUE, 3.0: RED, 3.5: BLUE, 4.0: RED, 4.5: BLUE}
net.make_input('inputA', inputA)
net.connect('inputA', 'A')

inputB = {0.0: CIRCLE, 0.5: SQUARE, 1.0: CIRCLE, 1.5: SQUARE, 2.0: CIRCLE,
          2.5: SQUARE, 3.0: CIRCLE, 3.5: SQUARE, 4.0: CIRCLE, 4.5: SQUARE}
net.make_input('inputB', inputB)
net.connect('inputB', 'B')

inputE = {
    0.0: ZERO, 0.2: CIRCLE, 0.35: RED, 0.5: ZERO, 0.7: SQUARE, 0.85: BLUE,
    1.0: ZERO, 1.2: CIRCLE, 1.35: RED, 1.5: ZERO, 1.7: SQUARE, 1.85: BLUE,
    2.0: ZERO, 2.2: CIRCLE, 2.35: RED, 2.5: ZERO, 2.7: SQUARE, 2.85: BLUE,
    3.0: ZERO, 3.2: CIRCLE, 3.35: RED, 3.5: ZERO, 3.7: SQUARE, 3.85: BLUE,
    4.0: ZERO, 4.2: CIRCLE, 4.35: RED, 4.5: ZERO, 4.7: SQUARE, 4.85: BLUE,
}

net.make_input('inputE', inputE)
net.connect('inputE', 'E')

net.add_to_nengo()
