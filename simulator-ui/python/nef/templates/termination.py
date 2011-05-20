title='Decoded Termination'
label='Termination'
icon='termination.png'

from ca.nengo.ui.configurable.descriptors import PTerminationWeights

def params(net,node):
    return [
    ('name','Name',str),
    ('weights','Weights',PTerminationWeights('Weights',node.dimension)),
    ('pstc','tauPSC',float),
    ('modulatory','Is Modulatory',bool),
    ]

def test_params(net,node,p):
    try:
        n = node.getTermination(p['name'])
        if n is not None:
            return 'That name is already in use'
    except:
        pass

def test_drop(net,node):
    return hasattr(node,'addDecodedTermination')

def make(net,node,name='termination',weights=None,pstc=0.01,modulatory=False):
    node.addDecodedTermination(name,weights,pstc,modulatory)
