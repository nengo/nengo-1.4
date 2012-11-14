Adding Arbitary Code to a Model
================================

Nengo models are composed of Nodes.  Each Node has Origins (outputs) and Terminations (inputs), allowing
them to be connected up to perform operations.  Nengo has built-in Node types for Neurons, Ensembles (groups of Neurons), 
Networks, Arrays, and so on.  However, when creating a complex model, we may want to create our own Node type.  This
might be to provide custom input or output from a model, or it can be used to have a non-neural component within
a larger model.

Technically, the only requirement for being a Node is that an object supports the ca.nengo.model.Node interface.
However, for the mast majority of cases, it is easier to make use of the :class:`nef.SimpleNode` wrapper class.

SimpleNode
-----------

You create a :class:`nef.SimpleNode` by subclassing, defining functions to be called for whatever Origins and/or
Terminations you want for this object.  The functions you define will be called once every time step (usually
0.001 seconds).  These functions can contain arbitrary code, allowing you to implement anything you want to.  
For example, the following code creates a Node that outputs a sine wave::

    import nef
    import math
    
    net=nef.Network('Sine Wave')
    
    # define the SimpleNode
    class SineWave(nef.SimpleNode):
        def origin_wave(self):
            return [math.sin(self.t)]
    wave=net.add(SineWave('wave'))
    
    net.make('neurons',100,1)
    
    # connect the SimpleNode to the group of neurons
    net.connect(wave.getOrigin('wave'),'neurons')
    
    net.add_to_nengo()
    
Outputs (a.k.a. origins)            
--------------------------

You can create as many outputs as you want from a SimpleNode, as long as each one has a distinct name.
Each origin consists of a single function that will get called once per time-step and must return
an array of floats.

When defining this function, it is often useful to know the current simulation time.  This can be
accessed as ``self.t``, and is the time (in seconds) of the beginning of the current time-step (the
end of the current time step is ``self.t_end``)::

    class ManyOrigins(nef.SimpleNode):
        
        # an origin that is 0 for t<0.5 and 1 for t>=0.5
        def origin_step(self):
            if self.t<0.5: return [0]
            else: return [1]
        
        # a triangle wave with period of 1.0 seconds
        def origin_triangle(self):
            x=self.t%1.0
            if x<0.5: return [x*2]
            else: return [2.0-x*2]
            
        # a sine wave and a cosine with frequency 10 Hz
        def origin_circle(self):
            theta=self.t*(2*math.pi)*10
            return [math.sin(theta),math.cos(theta)]

When connecting a SimpleNode to other nodes, we need to specify which origin we are connecting.  The
name of the origin is determined by the function definition, of the form ``origin_<name>``::

    net.make('A',100,1)
    net.make('B',200,2)
    many=net.add(ManyOrigins('many'))
    net.connect(many.getOrigin('triangle'),'A')
    net.connect(many.getOrigin('circle'),'B')
    

Inputs (a.k.a. Terminations)
------------------------------

To provide input to a SimpleNode, we define terminations.  These are done in a similar manner as origins, but these
functions take an input value (usually denoted ``x``), which is an array of floats containing the input.

When definining the termination, we also have to define the number of dimensions expected.  We do this by setting
the ``dimensions`` parameter (which defaults to 1).  We can also specify the post-synaptic time constant at this
termination by setting the ``pstc`` parameter (default is None).

For example, the following object takes a 5-dimensional input vector and outputs the largest of the received values::

    class Largest(nef.SimpleNode):
        def init(self):
            self.largest=0
        def termination_values(self,x,dimensions=5,pstc=0.01):
            self.largest=max(x)
        def origin_largest(self):
            return [self.largest]
            
    net=nef.Network('largest')
    net.make_input('input',[0]*5)
    largest=net.add(Largest('largest'))
    net.connect('input',largest.getTermination('values'))
    
.. note::
    When making a component like this, make sure to define an initial value for ``largest`` (or whatever internal parameter
    is being used to map inputs to outputs) inside the ``init(self)`` function.  This function will be called before the
    origins are evaluated so that there is a valid ``self.largest`` return value.

Arbitrary Code
----------------

You can also define a function that will be called every time step, but which is *not* tied to a particular
Origin or Termination.  This function is called ``tick``.  Here is a simple example where this function simply
prints the current time::

    class Time(nef.SimpleNode):
        def tick(self):
            print 'The current time in the simulation is:',self.t

As a more complex example, here is a ``tick`` function used to save spike raster information to a text file while the
simulation runs::

    class SpikeSaver(nef.SimpleNode):
        def tick(self):
            f=file('data.csv','a+')
            data=A.getOrigin('AXON').getValues().getValues()
            f.write('%1.3f,%s\n'%(self.t,list(data)))
            f.close()
            
    net=nef.Network('Spike Saver example')
    A=net.make('A',50,1)
    saver=net.add(SpikeSaver('saver'))
    
    
            
      


