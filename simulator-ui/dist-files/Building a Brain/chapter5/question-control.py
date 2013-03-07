D=100 #Number of dimensions
N=30  #Neurons per dimension 
import nef
import hrr

# import templates accessible from the drag-and-drop bar
import nef.templates.integrator as integrator
import nef.templates.binding as binding
import nef.templates.gate as gating
import nef.templates.basalganglia as bgtemplate
import nef.templates.basalganglia_rule as bg_rule

net = nef.Network('Question Answering with Control (pre-built)',seed=15)

# Define the vocabulary of vectors
vocab = hrr.Vocabulary(D)
vocab.parse('CIRCLE+BLUE+RED+SQUARE+QUESTION+STATEMENT')

# Input, output, and intermediate ensembles
visual = net.make_array('Visual', N, D)
channel = net.make_array('Channel', N, D)
net.make_array('Motor', N, D)

# Create the memory
integrator.make(net,name='Memory',neurons=N*D,dimensions=D,tau_feedback=0.4,tau_input=0.05,scale=1)
memory = net.network.getNode('Memory')

# Add projections to and from the channel ensemble
net.connect('Visual', 'Channel', pstc=0.02)
net.network.addProjection(channel.getOrigin('X'), memory.getTermination('input'))

# Create ensemble calculating the unbinding transformation
binding.make(net, name='Unbind', outputName='Motor', N_per_D=100, invertA=True, invertB=False)
unbind = net.network.getNode('Unbind')
net.network.addProjection(visual.getOrigin('X'), unbind.getTermination('A'))
net.network.addProjection(memory.getOrigin('X'), unbind.getTermination('B'))

# Create basal ganglia and pattern matching rules
bgtemplate.make(net,name='Basal Ganglia',dimensions=2,pstc=0.01)
bg = net.network.getNode('Basal Ganglia')
bg_rule.make(net,bg,index=0,dimensions=D,pattern='STATEMENT',pstc=0.01,use_single_input=True)
bg_rule.make(net,bg,index=1,dimensions=D,pattern='QUESTION',pstc=0.01,use_single_input=True)
net.network.addProjection(visual.getOrigin('X'), bg.getTermination('rule_00'))
net.network.addProjection(visual.getOrigin('X'), bg.getTermination('rule_01'))

# Create the thalamus network to process the output from the basal ganglia
thalamus = net.make_array('Thalamus', N, 2, quick=True, intercept=(-1, 0), encoders=[[1]])
thalamus.addDecodedTermination('bg',[[-3, 0], [0, -3]],0.01,False)
net.network.addProjection(bg.getOrigin('output'), thalamus.getTermination('bg'))
def xBiased(x):
        return [x[0]+1]

# Add gating signals to control memory acquisition and motor output
gating.make(net,name='Gate1', gated='Channel', neurons=100 ,pstc=0.01)
gating.make(net,name='Gate2', gated='Motor', neurons=100 ,pstc=0.01)
net.connect(thalamus, 'Gate1', index_pre=0, func=xBiased)
net.connect(thalamus, 'Gate2', index_pre=1, func=xBiased)


inputs={}
ZERO=[0]*D
inputs[0.0]=ZERO
inputs[0.1]=vocab.parse('STATEMENT+RED*CIRCLE').v
inputs[0.5]=ZERO
inputs[0.6]=vocab.parse('STATEMENT+BLUE*SQUARE').v
inputs[1.0]=ZERO
inputs[1.2]=vocab.parse('QUESTION+RED').v
inputs[1.4]=vocab.parse('QUESTION+SQUARE').v
inputs[1.6]=ZERO
inputs[1.8]=vocab.parse('QUESTION+RED').v
inputs[2.0]=vocab.parse('QUESTION+SQUARE').v
inputs[2.2]=ZERO
inputs[2.4]=vocab.parse('QUESTION+RED').v
inputs[2.6]=vocab.parse('QUESTION+SQUARE').v
inputs[2.8]=ZERO
inputs[3.0]=vocab.parse('QUESTION+RED').v
inputs[3.2]=vocab.parse('QUESTION+SQUARE').v
inputs[3.4]=ZERO
inputs[3.6]=vocab.parse('QUESTION+RED').v
inputs[3.8]=vocab.parse('QUESTION+SQUARE').v
inputs[4.0]=ZERO
inputs[4.2]=vocab.parse('QUESTION+RED').v
inputs[4.4]=vocab.parse('QUESTION+SQUARE').v
inputs[4.6]=ZERO
inputs[4.8]=vocab.parse('QUESTION+RED').v
inputs[5.0]=vocab.parse('QUESTION+SQUARE').v
inputs[5.2]=ZERO
inputs[5.4]=vocab.parse('QUESTION+RED').v
inputs[5.6]=vocab.parse('QUESTION+SQUARE').v
inputs[5.8]=ZERO
inputs[6.0]=vocab.parse('QUESTION+RED').v
inputs[6.2]=vocab.parse('QUESTION+SQUARE').v
inputs[6.4]=ZERO
inputs[6.6]=vocab.parse('QUESTION+RED').v
inputs[6.8]=vocab.parse('QUESTION+SQUARE').v
inputs[7.0]=ZERO

net.make_input('Input',inputs)        
net.connect('Input','Visual')

net.add_to_nengo()
