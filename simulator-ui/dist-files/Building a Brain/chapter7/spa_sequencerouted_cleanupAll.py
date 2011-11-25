from spa import *

class Rules:
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
    


class Routing(SPA):
    dimensions=16

    state=Buffer()
    vision=Buffer(feedback=0)
    BG=BasalGanglia(Rules)
    thal=Thalamus(BG)

    input=Input(10, vision='0.8*LETTER+D')

model=Routing()

# Create the clean-up memory.
import hrr
vocab = hrr.Vocabulary.defaults[model.dimensions] # get the vocabulary used by the rest of the network
pd = [] # list of preferred direction vectors
vsize = len(vocab.keys) # vocabulary size
for item in vocab.keys:
    pd.append(vocab[item].v.tolist())
cleanup = model.net.make('cleanup', neurons=300, dimensions=vsize, encoders=eye(vsize))
model.net.connect(model.state.net.network.getOrigin('state'), cleanup, transform=pd)

import nef
# Record data.
log=nef.Log(model.net,filename='NengoDemoOutput.csv')
log.add('cleanup')
log.add('state')
log.add_spikes('cleanup',name='cleanup_spikes')
log.add_vocab('state_vocab',vocab)