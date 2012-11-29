title='Decoded Origin'
label='Origin'
icon='origin.png'
 
from ca.nengo.ui.configurable.descriptors import PFunctionArray
from ca.nengo.model.impl import NetworkArrayImpl

params = {
    'name': PString(
        'Name',
        'Name of origin',
        'origin',
    ),
    'functions': PFunctionArray(
        'Output Functions',
        ('Functions to be computed by the ensemble. The dimension of '
         'the resulting origin equals the number of functions specified'),
    ),
    'array_functions': PFunctionArray(
        'Output Functions (per sub-ensemble)',
        ("Functions to be computed by each sub-ensemble. The input dimension "
         "of each function can be no greater than the dimension of the "
         "sub-ensembles. The results from each sub-ensemble are concatenated "
         "into one vector, which is the result of this origin. Thus, the "
         "dimension of this origin is number of sub-ensembles times the "
         "number of functions specified."),
    ),
}

def params(net,node):
    if(isinstance(node, NetworkArrayImpl)):
        dimension = node.getNodes()[0].getDimension();
    else:
        dimension = node.getDimension();

def test_params(net,node,p):
    try:
        n = node.getOrigin(p['name'])
        if n is not None:
            return 'That name is already in use'
    except:
        pass

def test_drop(net,node):
    return hasattr(node,'addDecodedOrigin')

def make(net, node, name='origin', functions=[]):
    node.addDecodedOrigin(name, functions, "AXON")
