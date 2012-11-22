from spa import *       #Import SPA related packages

class Rules:            #Define mappings for BG and Thal
    def start(vision='LETTER'):
        set(state=vision)
    def A(state='A'):
        set(state='B')
    def B(state='B'):
        set(state='C')
    def C(state='C'):
        set(state='D')
    def D(state='D'):
        set(state='E')
    def E(state='E'):
        set(state='A')
    
class Routing_Clean_All(SPA):     #Extend the imported SPA class
    dimensions=16        #Dimensions in SPs
    
    state=Buffer()       #Create a working memory/cortical element
    vision=Buffer(feedback=0)#Create a cortical element with no feedback
    BG=BasalGanglia(Rules()) #Set rules defined above
    thal=Thalamus(BG)        #Set thalamus with rules

    input=Input(10, vision='0.8*LETTER+D') #Define the starting input

model=Routing_Clean_All()

# Create the clean-up memory.
import hrr
vocab = hrr.Vocabulary.defaults[model.dimensions] # get the vocabulary used by the rest of the network
pd = [] # list of preferred direction vectors
vsize = len(vocab.keys) # vocabulary size
for item in vocab.keys:
    pd.append(vocab[item].v.tolist())
model.net.make('cleanup', neurons=300, dimensions=vsize, encoders=eye(vsize))
model.net.connect(model.state.net.network.getOrigin('state'), 'cleanup', transform=pd)

import nef
# Record data.
log=nef.Log(model.net,filename='NengoDemoOutput.csv')
log.add('cleanup')
log.add('state', origin='state')
log.add_spikes('cleanup',name='cleanup_spikes')
log.add_vocab('state',vocab)
