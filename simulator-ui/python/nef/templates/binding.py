title='Binding'
label='Binding'
icon='convolution.png'

params=[
    ('name','Name',str),
    ('nameA','Name of input ensemble A',str),
    ('nameB','Name of input ensemble B',str),
    ('nameC','Name of output ensemble',str),
    ('dimensions','Number of dimensions',int),
    ('N_per_D','Number of neurons per dimension',int),
    ('invertA','Invert input A',bool),
    ('invertB','Invert input B',bool),
    ('pstc','Time constant for input',float),
    ]

def test_params(net,p):
    aIsSet = False
    bIsSet = False
    cIsSet = False
    nameIsTaken = False
    nodeList = net.network.getNodes()
    for i in nodeList:
        if i.name == p['nameA']:
            aIsSet = True
        elif i.name == p['nameB']:
            bIsSet = True
        elif i.name == p['nameC']:
            cIsSet = True
        elif i.name == p['name']:
            nameIsTaken = True
    if nameIsTaken: return 'That name is already taken'
    if not (aIsSet and bIsSet and cIsSet): return 'Must provide names of existing input/output ensembles'
    if p['dimensions']<1: return 'Must have a positive number of dimensions'
    
import nef
import nef.convolution
import numeric
def make(net,name='Bind',nameA='A', nameB='B', nameC='C', dimensions=20, N_per_D=300, invertA=False, invertB=False, pstc=0.01):

    netconv = nef.Network(name)
    inputA=net.network.getNode(nameA)
    inputB=net.network.getNode(nameB)
    output=net.network.getNode(nameC)
    
    nef.convolution.make_convolution(netconv, '*', inputA, inputB, output, N_per_D, quick=True, invert_first=invertA, invert_second=invertB)