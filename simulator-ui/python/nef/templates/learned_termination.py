title='Learned Termination'
label='Learned\nTermination'
icon='learn.png'

params=[
    ('errName','Name of (new) error ensemble',str),
    ('N_err', 'Number of neurons in error ensemble',int),
    ('preName','Name of pre ensemble',str),
    ('postName','Name of post ensemble',str),
    ('rate','Learning rate',float)
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
from ca.nengo.model.plasticity.impl import STDPTermination, PlasticEnsembleImpl
import nef
import numeric
    
def make(net,errName='error', N_err=50, preName='pre', postName='post', rate=5e-7):

    # get pre and post ensembles from their names
    pre = net.network.getNode(preName)
    post = net.network.getNode(postName)
    
    # modulatory termination (find unused termination)
    count=0
    while 'mod_%02d'%count in [t.name for t in post.terminations]:
        count=count+1
    modname = 'mod_%02d'%count
    post.addDecodedTermination(modname, numeric.eye(post.dimension), 0.005, True)
    
    # random weight matrix to initialize projection from pre to post
    def rand_weights(w):
        for i in range(len(w)):
            for j in range(len(w[0])):
                w[i][j] = random.uniform(-1e-3,1e-3)
        return w
    weight = rand_weights(numeric.zeros((post.neurons, pre.neurons)).tolist())
    
    # non-decoded termination (to learn transformation)
    count = 0
    prename = pre.getName()
    while '%s_%02d'%(prename,count) in [t.name for t in post.terminations]:
        count=count+1
    prename = '%s_%02d'%(prename, count)

    post.addPESTermination(prename, weight, 0.005, False)
    
    # Create error ensemble
    error = net.make(errName, N_err, post.dimension)
    
    # Add projections
    net.connect(error.getOrigin('X'),post.getTermination(modname))
    net.connect(pre.getOrigin('AXON'),post.getTermination(prename))

    # Set learning rule on the non-decoded termination
    net.learn(post,prename,modname,rate=rate)
    
