title='Basal Ganglia'
label='Basal\nGanglia'
icon='basalganglia.png'

params=[
    ('name','Name',str,'Name of the new basal ganglia sub-network'),
    ('dimensions','Number of actions',int,'Number of actions for the basal ganglia to decide between'),
    ('pstc','Time constant for input [s]',float,'Synaptic time constant for input filtering, in seconds'),
    ]

def test_params(net,p):
    try:
       net.network.getNode(p['name'])
       return 'That name is already taken'
    except:
        pass
    if p['dimensions']<1: return 'Must have a positive number of actions'
    
import nps.basalganglia
import nef
import numeric
from ca.nengo.model.impl import NetworkImpl
from java.util import ArrayList
from java.util import HashMap
def make(net,name='Basal Ganglia',dimensions=1,pstc=0.01,netbg=None,same_neurons=True):

    if netbg is None:
        netbg=nef.Network(name)
    input=netbg.make('input',1,dimensions,quick=True,mode='direct')
    output=netbg.make('output',1,dimensions,quick=True,mode='direct')
    nps.basalganglia.make_basal_ganglia(netbg,input,output,dimensions,same_neurons=same_neurons)

    input.addDecodedTermination('input',numeric.eye(dimensions),pstc,False)
    netbg.network.exposeTermination(input.getTermination('input'),'input')
    netbg.network.exposeOrigin(output.getOrigin('X'),'output')
    
    if net is not None:
        net.add(netbg.network)
    
    if net.network.getMetaData("BasalGanglia") == None:
        net.network.setMetaData("BasalGanglia", HashMap())
    bgs = net.network.getMetaData("BasalGanglia")

    bg=HashMap(4)
    bg.put("name", name)
    bg.put("dimensions", dimensions)
    bg.put("pstc", pstc)
    bg.put("same_neurons", same_neurons)

    bgs.put(name, bg)

    if net.network.getMetaData("templates") == None:
        net.network.setMetaData("templates", ArrayList())
    templates = net.network.getMetaData("templates")
    templates.add(name)

