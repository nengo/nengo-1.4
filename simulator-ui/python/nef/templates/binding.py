title='Binding'
label='Binding'
icon='convolution.png'

params=[
    ('name','Name',str),
    ('outputName','Name of output ensemble',str),
    ('N_per_D','Number of neurons per dimension',int),
    ('invertA','Invert input A',bool),
    ('invertB','Invert input B',bool),
    ]

def test_params(net,p):
    outputIsSet = False
    nameIsTaken = False
    nodeList = net.network.getNodes()
    for i in nodeList:
        if i.name == p['outputName']:
            outputIsSet = True
        elif i.name == p['name']:
            nameIsTaken = True
    if nameIsTaken: return 'That name is already taken'
    if not outputIsSet: return 'Must provide the name of an existing output ensemble'
    
import nef
import nef.convolution
import numeric
def make(net,name='Bind', outputName='C', N_per_D=300, invertA=False, invertB=False):

    output=net.network.getNode(outputName) 
    nef.convolution.make_convolution(net, name, None, None, output, int(N_per_D/2+1), quick=True, invert_first=invertA, invert_second=invertB)