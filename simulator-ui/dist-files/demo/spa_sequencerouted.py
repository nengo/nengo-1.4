from spa import *

D=16

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

    input=Input(0.1,vision='LETTER+D')

model=Routing()
