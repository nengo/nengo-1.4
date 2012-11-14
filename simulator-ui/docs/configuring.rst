Configuring Neural Ensembles
================================

When creating a neural ensemble, a variety of parameters can be adjusted.  Some of these parameters are
set to reflect the physiological properties of the neurons being modelled, while others can be set
to improve the accuracy of the transformations computed by the neurons.

Membrane time constant and refractory period
----------------------------------------------

The two parameters for Leaky-Integrate-and-Fire neurons are the membrane time constant (``tau_rc``) and the 
refractory period (``tau_ref``).
These parameters are set when creating the ensemble, and default to 0.02 seconds for the membrane time constant
and 0.002 seconds for the refractory period::

  net.make('D',100,2,tau_rc=0.02,tau_ref=0.002)

Empirical data on the membrane time constants for different types of neurons in different parts of the brain
can be found at http://ctn.uwaterloo.ca/~cnrglab/?q=node/547.


Maximum firing rate
--------------------

You can also specify the maximum firing rate for the neurons.  It should be noted that it will always be possible
to force these neurons to fire faster than this specified rate. Indeed, the actual maximum firing rate will
always be ``1/tau_ref``, since if enough current is forced into the simulated neuron, it will fire as fast as
its refractory period will allow.  However, what we can specify with this parameter is the *normal* operating range
for the neurons.  More technically, this is the maximum firing rate *assuming that the neurons are representing 
a value within the ensemble's radius*.

In most cases, we specify this by giving a range of maximum firing rates, and each neuron will have a maximum
chosen uniformly from within this range.  This gives a somewhat biologically realistic amount of diversity
in the tuning curves.  The following line makes neurons with maximums between 200Hz and 400Hz::

    net.make('E',100,2,max_rate=(200,400))

Alternatively, we can specify a particular set of maximum firing rates, and each neuron will take on a value
from the provided list.  If there are more neurons than elements in the list, the provided values will be
re-used::

    net.make('F',100,2,max_rate=[200,250,300,350,400])
    
.. note::
   The type of brackets used is important!!  Python has two types of brackets for this sort of situation:
   round brackets ``()`` and square brackets ``[]``.  Round brackets create a *tuple*, which we use for
   indicating a range of values to randomly choose within, and square brackets create a *list*, which we 
   use for specifying a list of particular value to use.

Intercept
----------

The intercept is the point on the tuning curve graph where the neuron starts firing.  For example, 
for a one-dimensional ensemble, a neuron with a preferred direction vector of [1]
and an intercept of 0.3 will only fire when representing values above 0.3.  If the preferred
direction vector is [-1], then it will only fire for values below 0.3.  In general, the neuron will
only fire if the dot product of **x** (the value being represented) and the preferred direction vector (see below),
divided by the radius, is greater than the intercept.  Note that since we divide by the radius, the
intercepts will always be normalized to be between -1 and 1.

While this parameter can be used to help match the tuning curves observed in the system being modelled,
one important other use is to build neural models that can perfectly represent the value 0.  For example,
if a 1-dimensional neural ensemble is built with intercepts in the range (0.3,1), then no neurons at all will fire
for values between -0.3 and 0.3.  This means that any value in that range (i.e. any small value) will be
rounded down to exactly 0.  This can be useful for optimizing thresholding and other functions where 
many of the output values are zero.

By default, intercepts are uniformly distributed between -1 and 1.  The intercepts can be specified by 
providing either a range, or a list of values::

  net.make('G',100,2,intercept=(-1,1))
  net.make('H',100,2,intercept=[-0.8,-0.4,0.4,0.8])

.. note::
   The type of brackets used is important!!  Python has two types of brackets for this sort of situation:
   round brackets ``()`` and square brackets ``[]``.  Round brackets create a *tuple*, which we use for
   indicating a range of values to randomly choose within, and square brackets create a *list*, which we 
   use for specifying a list of particular value to use.

Encoders (a.k.a. preferred direction vectors)
-----------------------------------------------

You can specify the encoders (preferred direction vectors) for the neurons.  By default, the encoders are 
chosen uniformly from the unit sphere.  Alternatively, you can specify those encoders by providing a list.  The 
encoders given will automatically be normalized to unit length::

  net.make('F',100,2,encoders=[[1,0],[-1,0],[0,1],[0,-1]])
  net.make('G',100,2,encoders=[[1,1],[1,-1],[-1,1],[-1,-1]])

This allows you to make complex sets of encoders by creating a list with the encoders you want.  For example, the 
following code creates an ensemble with 100 neurons, half of which have encoders chosen from the unit 
circle, and the other half of which are aligned on the diagonals::

    import random
    import math
        
    encoders=[]              # create an empty list to store the encoders
    for i in range(50):
        theta=random.uniform(-math.pi,pi)  # choose a random direction between -pi and pi
        encoders.append([math.sin(theta),math.cos(theta)])  # add the encoder
    for i in range(50):
        encoders.append(random.choice([[1,1],[1,-1],[-1,1],[-1,-1]]))  # add an aligned encoder

    net.make('G',100,2,encoders=encoders)     # create the ensemble

