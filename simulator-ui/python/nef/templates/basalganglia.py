title='Basal Ganglia'
icon='basalganglia.png'

params=[
    ('dimensions','Number of actions',int),
    ]

def test_params(net,p):
    if p['dimensions']<1: return 'Must have a positive number of actions'
    
import nps.basalganglia
def make(net,dimensions=1):
    input=net.make_input('utility',[0]*dimensions)
    output=net.make_array('selection',30,dimensions)
    nps.basalganglia.make_basal_ganglia(net,input,output,dimensions)
    
