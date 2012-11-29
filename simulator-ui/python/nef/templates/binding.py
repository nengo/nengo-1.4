title = 'Binding'
label = 'Binding'
icon = 'convolution.png'

description = ("<html>This template inserts a subnetwork that computes the "
               "circular convolution (the SPA binding operator) of two inputs."
               " The dimensionality of the input is determined by the "
               "dimensionality of the specified output ensemble. Inverting "
               "an input causes it to be unbound from the other input. "
               "Inputs must be specified after construction.</html>")

params = {
    'name': PString(
        'Name',
        'Name of the binding (circular convolution) population',
        'Bind',
    ),
    'outputName': PString(
        'Name of output ensemble',
        ('Name of an existing ensemble specifying the output destination '
         'for the binding ensemble'),
        'C',
    ),
    'N_per_D': PInt(
        'Number of neurons per dimension',
        'Number of neurons per dimension of the binding population',
        300,
        1,
        sys.maxint,
    ),
    'invertA': PBoolean(
        'Invert input A',
        'Whether to invert the first input (A)',
        False,
    ),
    'invertB': PBoolean(
        'Invert input B',
        'Whether to invert the second input (B)',
        False,
    ),
}

def test_params(net, p):
    outputIsSet = False
    nameIsTaken = False
    nodeList = net.network.getNodes()
    for i in nodeList:
        if i.name == p['outputName']:
            outputIsSet = True
        elif i.name == p['name']:
            nameIsTaken = True
    if nameIsTaken:
        return 'That name is already taken'
    if not outputIsSet:
        return 'Must provide the name of an existing output ensemble'

import nef
import nef.convolution
import numeric
from java.util import ArrayList
from java.util import HashMap


def make(net, name='Bind', outputName='C', N_per_D=300, invertA=False, invertB=False):

    output = net.network.getNode(outputName)
    nef.convolution.make_convolution(net, name, None, None, output, int(N_per_D / 2 + 1), quick=True, invert_first=invertA, invert_second=invertB)


