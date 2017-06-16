from spa import *


# Define the rules by specifying the start state and the desired next state
class Rules:

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
class Sequence(SPA):
    dimensions = 16
    # Working memory (recurrent network) object (i.e., Buffer)
    state = Buffer()
    # A basal ganglia with the prespecified set of rules
    BG = BasalGanglia(Rules())
    # A thalamus for that basal ganglia (so it uses the same rules)
    thal = Thalamus(BG)
    # Define an input; set the input to 'state' to D for 100 ms
    input = Input(0.1, state='D')


seq = Sequence()
