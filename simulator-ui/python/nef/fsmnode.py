import hrr as _hrr
import nef

class _FSMState:
    """Holds information about a particular state in a finite state machine."""
    def __init__(self,name,node,hrr=None,enter_f=None,exit_f=None,timer_f=None,timer_t=None,input=None):
        """Initializes a finite state machine state. Requires a name
        (which must be unique to the node) and the FSMNode
        which it will be a part of.
        
        Keyword arguments:
        hrr -- An HRR to be bound to this state. If it is not assigned, it will be
          randomly generated.
        enter_f -- A function to run when you enter this state.
        exit_f -- A function to run when you exit this state.
        timer_f -- A function to run after a certain amount of time has elapsed.
        timer_t -- The amount of time after which the timer function will run.
        input -- A function to run in response to some input. (Does this make sense to include?)
        """
        self.name = name
        self.node = node
        self.enter_f = enter_f
        self.exit_f = exit_f
        self.timer_f = timer_f
        self.timer_t = timer_t
        self.input = input
        
        if not hrr:
            self.hrr = _hrr.HRR(N=node.state_dims)
        else:
            self.hrr = hrr

class FSMNode(nef.SimpleNode):
    def __init__(self,name,state_dims):
        # parameters
        self.state_dims = state_dims
        
        # set up other stuff
        self.timer = 0.0
        self.states = {}
        self.start_state = ''
        self.state = None
        
        # call SimpleNode constructor last
        nef.SimpleNode.__init__(self,name)
    
    def add_state(self,name,**kwargs):
        if self.states.has_key(name):
            raise Exception('FSMNode already has a state named %s.' % name)
        self.states[name] = _FSMState(name,self,**kwargs)
        
        # Make the first state added the start state, by default
        if not self.state:
            self.start_state = name
            self.state = self.states[name]
    
    def transition(self,next_state):
        if not self.states.has_key(next_state):
            raise Exception('FSMNode has no state named %s.' % next_state)
        
        if self.state and self.state.exit_f is not None:
            self.state.exit_f()
        
        self.state = self.states[next_state]
        self.reset_timer()
        
        if self.state.enter_f is not None:
            self.state.enter_f()
    
    def reset_timer(self):
        self.timer = self.t_start

    def origin_state(self):
        if self.state and self.state.timer_f is not None and self.t_start-self.timer >= self.state.timer_t:
            self.state.timer_f()
            # Most timer functions will transition, but if not, we'll
            # still reset the timer so that we can do an action many
            # times in a certain state after a certain amount of time.
            self.reset_timer()
        
        if self.state:
            return self.state.hrr.v
        return [0.0]*self.state_dims
    
    def termination_input(self,x):
        if self.state and self.state.input:
            self.state.input(x)
    
    def reset(self, randomize=False):
        self.timer = 0.0
        nef.SimpleNode.reset(self, randomize)
