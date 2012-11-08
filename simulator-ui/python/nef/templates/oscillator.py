title='Oscillator'
label='Oscillator'
icon='oscillator.png'

params=[
    ('name','Name',str,'Name of the oscillator'),
    ('neurons','Number of neurons',int,'Number of neurons in the oscillator'),
    ('frequency','Oscillator frequency (Hz)',float,'The speed at which the osillator oscillates'),
    ('tau_feedback','Feedback time constant [s]',float,'Synaptic time constant of the oscillator feedback, in seconds (longer -> slower change but better value retention)'),
    ('tau_input','Input time constant [s]',float,'Synaptic time constant of the oscillator input, in seconds (longer -> more input filtering)'),
    ('scale','Scaling factor',float,'A scaling value for the input'),
    ('controlled', 'Frequency control', bool,'If checked will build a frequency controlled oscillator (a nonlinear system)'),
    ]

def test_params(net,p):
    try:
       net.network.getNode(p['name'])
       return 'That name is already taken'
    except:
        pass
    if p['neurons']<1: return 'Must have a positive number of neurons'
    if p['frequency']<1: return 'Must have a positive frequency'
    
import numeric
def feedback(x):
    return x[0]+x[2]*x[1], x[1]-x[2]*x[0], 0
    
def make(net, name='Oscillator', neurons=100, dimensions=2, frequency = 5, tau_feedback=0.1, tau_input=0.01, scale=1, controlled = False):
    frequency = frequency*2*numeric.pi;
    if (controlled):
        oscillator=net.make(name,neurons,dimensions=3)
        A = [[1, 0, 0], [0, 1, 0], [0, 0, 1]]
        B = [[tau_input*scale, 0], [0, tau_input*scale], [0, 0]] 
        net.connect(oscillator, oscillator, A, func=feedback, pstc=tau_feedback)
        oscillator.addDecodedTermination('frequency', [[0],[0],[tau_feedback*frequency]], tau_input, False)
             
    else:
        oscillator=net.make(name, neurons, dimensions=2)
        A = [[1, -frequency*tau_feedback], [frequency*tau_feedback, 1]]
        B = [[1, 0],[0, 1]]
        net.connect(oscillator, oscillator, A, pstc=tau_feedback)

    oscillator.addDecodedTermination('input', B, tau_input, False)
