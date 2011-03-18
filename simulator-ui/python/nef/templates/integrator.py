title='Integrator'
label='Integrator'
icon='integrator.png'

params=[
    ('name','Name',str),
    ('neurons','Number of neurons',int),
    ('dimensions','Number of dimensions',int),
    ('tau_feedback','Feedback time constant',float),
    ('tau_input','Input time constant',float),
    ('scale','Scaling factor',float),
    ]

def test_params(net,p):
    try:
       net.network.getNode(p['name'])
       return 'That name is already taken'
    except:
        pass
    if p['neurons']<1: return 'Must have a positive number of neurons'
    
import numeric
def make(net,name='Integrator',neurons=100,dimensions=1,tau_feedback=0.1,tau_input=0.01,scale=1):
    integrator=net.make(name,neurons,dimensions)
    net.connect(integrator,integrator,pstc=tau_feedback)
    integrator.addDecodedTermination('input',numeric.eye(dimensions)*tau_feedback*scale,tau_input,False)
