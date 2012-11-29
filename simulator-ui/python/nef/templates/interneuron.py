title = 'Interneurons'
label = 'Interneurons'
icon = 'interneuron.png'

import sys
import ca.nengo
from ca.nengo.ui.configurable.properties import PInt, PFloat

description = ("This template creates inhibitory interneurons. "
               "This adjusts the connection weights so that instead of "
               "having a mixture of excitatory and inhibitory synapses, "
               "the primary connection is all excitatory, and a separate "
               "group of neurons is created to provide the inhibition.")

params = {
    'neurons': PInt(
        'Number of neurons',
        'How many interneurons to create',
        50,
        1,
        sys.maxint,
    ),
    'pstc': PFloat(
        'Post-synaptic time constant (s)',
        'Time constant for connection from interneurons to target population',
        0.01,
        1e-40,
        float("inf"),
    ),
}

def test_drop(net, node):
    if isinstance(node, ca.nengo.model.nef.impl.DecodedTermination):
        for p in net.projections:
            if p.termination == node:
                if isinstance(p.origin, ca.nengo.model.nef.impl.DecodedOrigin):
                    return True
                break
    return False    

def make(net, node, neurons=50, pstc=0.01):
    if not isinstance(node, ca.nengo.model.nef.impl.DecodedTermination):
        raise Exception('Interneurons must be created on a Decoded Termination')
    proj = None
    for p in net.network.projections:
        if p.termination == node:
            proj = p
            break
    if proj is None:
        raise Exception('Interneurons must be created on an already-existing projection')
    
    if not isinstance(proj.origin, ca.nengo.model.nef.impl.DecodedOrigin):
        raise Exception('Interneurons require a projection from a Decoded Origin to a Decoded Termination')

    proj.addBias(neurons, pstc, proj.termination.tau, True, False)
