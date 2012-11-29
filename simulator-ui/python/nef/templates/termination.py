title = 'Decoded Termination'
label = 'Termination'
icon = 'termination.png'

from ca.nengo.ui.configurable.properties import PTerminationWeights

params = {
    'name': PString(
        'Name',
        'Name of termination',
        'termination',
    ),
    'weights': PTerminationWeights(
        'Weights',
        'Linear transformation matrix to apply',
        1,
    ),
    'pstc': PFloat(
        'PSTC [s]',
        'Post-synaptic time constant, in seconds',
        0.01,
        1e-40,
        float("inf"),
    ),
    'modulatory': PBoolean(
        'Is Modulatory',
        ('Only enable this setting if this termination merely adjusts '
         'neuron properties (rather than adding input current)'),
        False,
    ),
}

def test_params(net, node, p):
    try:
        n = node.getTermination(p['name'])
        if n is not None:
            return 'That name is already in use'
    except:
        pass


def test_drop(net, node):
    return hasattr(node, 'addDecodedTermination')


def make(net, node, name='termination', weights=None, pstc=0.01, modulatory=False):
    node.addDecodedTermination(name, weights, pstc, modulatory)
