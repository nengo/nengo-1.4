title='Thalamus Rule'
label='Thalamus Rule'
icon='ThalRule.png'

description='''<html>This template is dropped onto a thalamus model generated from the Thalamus template. It is used to define output rules in a SPA model. It provides a mapping from thalamus to cortex to determine the value of the output semantic pointer with respect to the indexed rule.</html>'''

def_params2 = [
    ('dimensions','Dimensionality',int,'The dimensionality of the output semantic pointer'),
    ('pattern','Semantic Pointer',str,'The output representation that the rule activates'),
    ('index','Rule Index',int, 'The number of the rule currently being defined (0 indexed)'),
    ]

def_params = [
    ('dimensions','Dimensionality',int,'The dimensionality of the output semantic pointer'),
    ]

params = []

dynamic_params_max_actions = 0

def test_params(net,node,p):
    if 'index' in p:
        try: 
            node.getNode('%d' % p['index'])
        except:
            return 'Rule index cannot exceed number of actions in the thalamus'

def test_drop(net,node):
    try:
        ens0 = node.getNode('0')
        nodes = node.getNodes()
        if len(nodes) > dynamic_params_max_actions:
            if len(params) != len(def_params2):
                for i in range(len(params)):
                    params.pop()
                for i in range(len(def_params2)):
                    params.append(def_params2[i])
        else:
            if len(params) != len(filter(lambda x: not x.name.startswith('output'), nodes)) + len(def_params):
                for i in range(len(params)):
                    params.pop()
                for i in range(len(def_params)):
                    params.append(def_params[i])
                for i in range(len(nodes)):
                    params.append(('pattern%d' % i,'Semantic Pointer %d' % i,str,'The output representation that rule %d activates' % i))
        return True
    except:
        return False

import numeric
import hrr
from java.util import ArrayList
from java.util import HashMap
from ca.nengo.model.impl import NetworkImpl
from ca.nengo.model import Network
from ca.nengo.math.impl import PostfixFunction
from ca.nengo.model.neuron import Neuron
from ca.nengo.model.impl import EnsembleOrigin

import java

def make(net, node, index = 0, dimensions = 8, pattern = 'I', **args):
    # Figure out which dictionary to use
    if dimensions in hrr.Vocabulary.defaults.keys():
        vocab = hrr.Vocabulary.defaults[dimensions]
    else:
        vocab = hrr.Vocabulary(dimensions)

    if(len(args) == 0):
        # Get the transform for the given pattern
        transform = vocab.parse(pattern).v

        rule_index, ens_origin = addOrigin(node, index, transform)

        node.exposeOrigin(ens_origin, 'rule%d_%d' % (index, rule_index))
    else:
        output_nodes = filter(lambda x: x.name.startswith('output'), node.getNodes())
        output = net.make('output%d' % len(output_nodes), 1, dimensions, quick = True, mode = 'direct', add_to_network = False)
        node.addNode(output)
        rule_index = 0
        for i in range(len(node.getNodes())):
            pattern = args['pattern%d' % i]
            transform = vocab.parse(pattern).v

            rule_index, ens_origin = addOrigin(node, i, transform)
            output_term = output.addDecodedTermination('action%d' % i, numeric.eye(dimensions), 0.001, False)
            node.addProjection(ens_origin, output_term)
        node.exposeOrigin(output.getOrigin('X'), 'rule_%d' % rule_index)



def addOrigin(node, index, transform):
    ens = node.getNode('%d' % index)

    # Get the next rule index for the given action index
    rule_index = 0
    while True:
        try:
            origin = ens.getOrigin('rule%d' % rule_index)
            if origin is None:
                break
            rule_index += 1
        except:
            break

    # Add the new ensemble origin
    return rule_index, ens.addDecodedOrigin('rule%d' % rule_index, [PostfixFunction('(x0+1)*%0.10f'%transform[i],1) for i in range(len(transform))], \
                                            Neuron.AXON)
