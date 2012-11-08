title='Binding'
label='Binding'
icon='convolution.png'

params=[
    ('name','Name',str,'Name of the binding (circular convolution) population'),
    ('outputName','Name of output ensemble',str,'Name of an existing ensemble specifying the output destination for the binding ensemble'),
    ('N_per_D','Number of neurons per dimension',int,'Number of neurons per dimension of the binding population'),
    ('invertA','Invert input A',bool,'Whether to invert the first input (A)'),
    ('invertB','Invert input B',bool,'Whether to invert the second input (B)'),
    ]

def test_params(net,p):
    outputIsSet = False
    nameIsTaken = False
    nodeList = net.network.getNodes()
    for i in nodeList:
        if i.name == p['outputName']:
            outputIsSet = True
        elif i.name == p['name']:
            nameIsTaken = True
    if nameIsTaken: return 'That name is already taken'
    if not outputIsSet: return 'Must provide the name of an existing output ensemble'
    
import nef
import nef.convolution
import numeric
from java.util import ArrayList
from java.util import HashMap
def make(net,name='Bind', outputName='C', N_per_D=300, invertA=False, invertB=False):

    output=net.network.getNode(outputName) 
    nef.convolution.make_convolution(net, name, None, None, output, int(N_per_D/2+1), quick=True, invert_first=invertA, invert_second=invertB)

    if net.network.getMetaData("binding") == None:
        net.network.setMetaData("binding", HashMap())
    bindings = net.network.getMetaData("binding")

    binding=HashMap(5)
    binding.put("name", name)
    binding.put("outputName", outputName)
    binding.put("N_per_D", N_per_D)
    binding.put("invertA", invertA)
    binding.put("invertB", invertB)

    bindings.put(name, binding)

    if net.network.getMetaData("templates") == None:
        net.network.setMetaData("templates", ArrayList())
    templates = net.network.getMetaData("templates")
    templates.add(name)

    if net.network.getMetaData("templateProjections") == None:
        net.network.setMetaData("templateProjections", HashMap())
    templateproj = net.network.getMetaData("templateProjections")
    templateproj.put(name, outputName)

