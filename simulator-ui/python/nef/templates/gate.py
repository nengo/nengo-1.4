title='Gate'
label='Gate'
icon='gate.png'

params=[
    ('name','Name',str,'Name of the new gating ensemble'),
    ('gated','Name of gated ensemble',str,'Name of the existing ensemble to gate'),
    ('neurons','Number of neurons',int,'Number of neurons in the new gating ensemble'),
    ('pstc','Synaptic time constant [s]', float, 'Synaptic time constant of the gating ensemble'),
    ]

def test_params(net, p):
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
import nef.array

def make(net,name='Gate', gated='visual', neurons=40 ,pstc=0.01):
    gate=net.make(name, neurons, 1, intercept=(-0.7, 0), encoders=[[-1]])
    def addOne(x):
        return [x[0]+1]            
    net.connect(gate, None, func=addOne, origin_name='xBiased', create_projection=False)
    output=net.network.getNode(gated)
    if isinstance(output,nef.array.NetworkArray):
        weights=[[-10]]*(output.nodes[0].neurons*len(output.nodes))
    else:
        weights=[[-10]]*output.neurons
    
    count=0
    while 'gate_%02d'%count in [t.name for t in output.terminations]:
        count=count+1
    oname = str('gate_%02d'%count)
    output.addTermination(oname, weights, pstc, False)
    
    orig = gate.getOrigin('xBiased')
    term = output.getTermination(oname)
    net.network.addProjection(orig, term)

    if net.getMetaData("gate") == None:
        net.setMetaData("gate", ArrayList())
    gates = net.getMetaData("gate")

    gate=HashMap(4)
    gate.put("name", name)
    gate.put("gated", gated)
    gate.put("neurons", neurons)
    gate.put("pstc", pstc)

    gates.add(gate)


