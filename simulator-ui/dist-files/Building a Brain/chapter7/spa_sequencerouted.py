from spa import *       #Import SPA related packages

class Rules:            #Define mappings for BG and Thal
    def start(vision='START'):
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
    


class Routing(SPA):     #Extend the imported SPA class
    dimensions=16        #Dimensions in SPs
    
    state=Buffer()       #Create a working memory/cortical element
    vision=Buffer(feedback=0)#Create a cortical element with no feedback
    BG=BasalGanglia(Rules()) #Set rules defined above
    thal=Thalamus(BG)        #Set thalamus with rules

    input=Input(0.4, vision='0.8*START+D') #Define the starting input

model=Routing()            #Run the class

