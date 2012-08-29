from spa import *       #Import SPA related packages

class Rules:            #Define mappings for BG and Thal
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
    

class Sequence(SPA):     #Extend the imported SPA class
    dimensions=16        #Dimensions in SPs
    
    state=Buffer()       #Create a working memory/cortical element
    BG=BasalGanglia(Rules()) #Set rules defined above
    thal=Thalamus(BG)        #Set thalamus with rules
    
    input=Input(0.1,state='D') #Define the starting input

seq=Sequence()            #Run the class
