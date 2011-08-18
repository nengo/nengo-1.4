Sequencing with the SPA Package
================================================


from spa import *

D=16

class Rules:
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
    


class Sequence(SPA):
    dimensions=16
    
    state=Buffer()
    BG=BasalGanglia(Rules())
    thal=Thalamus(BG)
    
    input=Input(0.1,state='D')

seq=Sequence()
