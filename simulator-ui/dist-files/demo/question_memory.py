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
net = nef.Network('Question answering with memory')
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

# Recurrently connect C so it acts as a memory
net.connect('C', 'C', pstc=0.4)

# Create input to model
vocab = hrr.Vocabulary(D, max_similarity=0.1)
CIRCLE = vocab.parse('CIRCLE').v
BLUE = vocab.parse('BLUE').v
RED = vocab.parse('RED').v
SQUARE = vocab.parse('SQUARE').v
ZERO = [0] * D

inputA = {0: RED, 0.25: BLUE, 0.5: ZERO}
net.make_input('inputA', inputA)
net.connect('inputA', 'A')

inputB = {0: CIRCLE, 0.25: SQUARE, 0.5: ZERO}
net.make_input('inputB', inputB)
net.connect('inputB', 'B')

inputE = {
    0.0: ZERO,
    0.5: CIRCLE, 0.6: RED, 0.7: SQUARE, 0.8: BLUE, 0.9: ZERO,
    1.0: CIRCLE, 1.1: RED, 1.2: SQUARE, 1.3: BLUE, 1.4: ZERO,
    1.5: CIRCLE, 1.6: RED, 1.7: SQUARE, 1.8: BLUE, 1.9: ZERO,
    2.0: CIRCLE, 2.1: RED, 2.2: SQUARE, 2.3: BLUE, 2.4: ZERO,
    2.5: CIRCLE, 2.6: RED, 2.7: SQUARE, 2.8: BLUE, 2.9: ZERO,
    3.0: CIRCLE, 3.1: RED, 3.2: SQUARE, 3.3: BLUE, 3.4: ZERO,
    3.5: CIRCLE, 3.6: RED, 3.7: SQUARE, 3.8: BLUE, 3.9: ZERO,
    4.0: CIRCLE, 4.1: RED, 4.2: SQUARE, 4.3: BLUE, 4.4: ZERO,
    4.5: CIRCLE, 4.6: RED, 4.7: SQUARE, 4.8: BLUE, 4.9: ZERO,
}

net.make_input('inputE', inputE)
net.connect('inputE', 'E')

net.add_to_nengo()
