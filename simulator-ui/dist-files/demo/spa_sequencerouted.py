from spa import *


# Define the rules by specifying the start state and the desired next state
class Rules:

    def start(self, vision='START'):
        set(state=vision)

    def A(self, state='A'):  # If in state A...
        set(state='B')  # then go to state B

    def B(self, state='B'):
        set(state='C')

    def C(self, state='C'):
        set(state='D')

    def D(self, state='D'):
        set(state='E')

    def E(self, state='E'):
        set(state='A')


# Define an SPA model (cortex, basal ganglia, thalamus)
class Routing(SPA):
    dimensions = 16
    # Working memory (recurrent network) object (i.e., Buffer)
    state = Buffer()
    # Cortical network with no recurrence (no memory, just transient states)
    vision = Buffer(feedback=0)
    # A basal ganglia with the prespecified set of rules
    BG = BasalGanglia(Rules())
    # A thalamus for that basal ganglia (so it uses the same rules)
    thal = Thalamus(BG)
    # Define an input; set the input to 'vision' to 0.8*START+D for 100 ms
    input = Input(0.1, vision='0.8*START+D')


model = Routing()
