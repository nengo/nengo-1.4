title='Basal Ganglia'
label='Basal\nGanglia'
icon='basalganglia.png'

params=[
    ('name','Name',str),
    ('dimensions','Number of actions',int),
    ('pstc','Time constant for input',float),
    ]

def test_params(net,p):
    if p['dimensions']<1: return 'Must have a positive number of actions'
    
import nps.basalganglia
import nef
import numeric
def make(net,name='Basal Ganglia',dimensions=1,pstc=0.01):

    netbg=nef.Network(name)
    input=netbg.make('input',1,dimensions,quick=True,mode='direct')
    output=netbg.make('output',1,dimensions,quick=True,mode='direct')
    nps.basalganglia.make_basal_ganglia(netbg,input,output,dimensions)

    input.addDecodedTermination('input',numeric.eye(dimensions),pstc,False)
    netbg.network.exposeTermination(input.getTermination('input'),'input')
    netbg.network.exposeOrigin(output.getOrigin('X'),'output')
    

    net.add(netbg.network)
    
