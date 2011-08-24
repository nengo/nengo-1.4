import nef
import nef.convolution
import hrr

D=10

vocab=hrr.Vocabulary(D, include_pairs=True)
vocab.parse('a+b+c+d+e')

net=nef.Network('Convolution') #Create the network object
A=net.make('A',300,D,quick=True) #Make a population of 300 neurons and 10 dimensions
B=net.make('B',300,D,quick=True)
C=net.make('C',300,D,quick=True)
conv=nef.convolution.make_convolution(net,'*',A,B,C,100,quick=True) #Call the code to construct a convolution network using the created populations and 100 neurons per dimension

net.add_to_nengo()




