title='Gate'
label='Gate'
icon='termination.png' # TODO

params=[
    ('name','Name',str),
    ('gated','Name of gated ensemble',str),
    ('neurons','Number of neurons',int),
    ('pstc','PSTC', float),
    ]

def test_params(net,p):
    gatedIsSet = False
    nameIsTaken = False
    nodeList = net.network.getNodes()
    for i in nodeList:
        if i.name == p['gated']:
            gatedIsSet = True
        elif i.name == p['name']:
            nameIsTaken = True
    if nameIsTaken: return 'That name is already taken'
    if not gatedIsSet: return 'Must provide the name of an existing ensemble to be gated'
    
import nef

def make(net,name='Gate', gated='visual', neurons=40 ,pstc=0.01):
    gate=net.make(name, neurons, 1, intercept=(-0.7, 0), encoders=[[-1]])
    def addOne(x):
        return [x[0]+1]            
    net.connect(gate, None, func=addOne, origin_name='xBiased')
    output=net.network.getNode(gated)
    weights=[[-10]]*output.neurons
    output.addTermination('gate', weights, pstc, False)
    orig = gate.getOrigin('xBiased')
    term = output.getTermination('gate')
    net.network.addProjection(orig, term)