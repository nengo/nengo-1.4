title='STDP Learning Rule'
label='Learning Rule'
icon='integrator.png'

params=[
    ('errName','Name of (new) error ensemble',str),
    ('N_err', 'Number of neurons in error ensemble', int),
    ('preName','Name of pre ensemble',str),
    ('postName','Name of post ensemble',str),
    ('rate','Learning rate',float),
    ]

def test_params(net,p):
    preIsSet = False
    postIsSet = False
    nameIsTaken = False
    nodeList = net.network.getNodes()
    for i in nodeList:
        if i.name == p['preName']:
            preIsSet = True
        elif i.name == p['postName']:
            postIsSet = True
        elif i.name == p['errName']:
            nameIsTaken = True
    if nameIsTaken: return 'The name for error ensemble is already taken'
    if not preIsSet: return 'Must provide the name of an existing pre ensemble'
    if not postIsSet: return 'Must provide the name of an existing post ensemble'
    if p['N_err'] < 1: return 'Number of dimensions must be at least one'
    
import random
from ca.nengo.model.plasticity.impl import ErrorLearningFunction, InSpikeErrorFunction, \
    OutSpikeErrorFunction, SpikePlasticityRule
import numeric
    
def make(net,errName='error', N_err = 50, preName='pre', postName = 'post', rate=5e-7):

    # get pre and post ensembles from their names
    pre = net.network.getNode(preName)
    post = net.network.getNode(postName)
    
    # modulatory termination
    count=0
    while 'mod_%02d'%count in [t.name for t in post.terminations]:
        count=count+1
    modname = 'mod_%02d'%count
    post.addDecodedTermination(modname, numeric.eye(post.dimension), 0.005, True)
    
    # non-decoded termination (to learn transformation)
    try:
        pre_term = node.getTermination(p[pre.getName()])
    except:
        pre_term = None
    
    if pre_term is None:
        def rand_weights(w):
            for i in range(len(w)):
                for j in range(len(w[0])):
                    w[i][j] = random.uniform(-1e-3,1e-3)
            return w
        weight = rand_weights(numeric.zeros((post.neurons, pre.neurons)).tolist())
        post.addTermination(pre.getName(), weight, 0.005, False)
    
    # Set learning rule on the non-decoded termination
    # Code ripped directly from nef_core
    in_args = {'a2Minus':  6.6e-3,
        'a3Minus':  3.1e-3,
        'tauMinus': 33.7,
        'tauX':     101.0}
                   
    inFcn = InSpikeErrorFunction([n.scale for n in post.nodes],post.encoders,
        in_args['a2Minus'],in_args['a3Minus'],in_args['tauMinus'],in_args['tauX'])
    
    inFcn.setLearningRate(rate)
                    
    out_args = {'a2Plus':  8.8e-11,
        'a3Plus':  5.3e-2,
        'tauPlus': 16.8,
        'tauY':    125.0}
                    
    outFcn = OutSpikeErrorFunction([n.scale for n in post.nodes],post.encoders,
        out_args['a2Plus'],out_args['a3Plus'],out_args['tauPlus'],out_args['tauY'])
    
    outFcn.setLearningRate(rate)
    rule=SpikePlasticityRule(inFcn, outFcn, 'AXON', modname)
    post.setPlasticityRule(pre.getName(),rule)
    
    # Create error ensemble
    error = net.make(errName, N_err, post.dimension)
    
    # Add projections
    net.network.addProjection(error.getOrigin('X'), post.getTermination(modname))
    net.network.addProjection(pre.getOrigin('AXON'), post.getTermination(pre.getName()))
    