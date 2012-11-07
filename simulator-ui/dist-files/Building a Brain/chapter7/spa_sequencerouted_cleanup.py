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
    


class RoutingClean(SPA):     #Extend the imported SPA class
    dimensions=16        #Dimensions in SPs
    
    state=Buffer()       #Create a working memory/cortical element
    vision=Buffer(feedback=0)#Create a cortical element with no feedback
    BG=BasalGanglia(Rules()) #Set rules defined above
    thal=Thalamus(BG)        #Set thalamus with rules

    input=Input(10, vision='0.8*LETTER+D') #Define the starting input

model=RoutingClean()

# Create the clean-up memory.
import hrr
vocab = hrr.Vocabulary.defaults[model.dimensions] # get the vocabulary used by the rest of the network
pd = [vocab['A'].v.tolist()] # get a preferred direction vector aligned to the 'A' vector
model.net.make('cleanup A', neurons=100, dimensions=1)
model.net.connect(model.state.net.network.getOrigin('state'), 'cleanup A', transform=pd)
