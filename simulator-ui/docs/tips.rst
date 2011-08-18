Advanced Scripting Tips
===========================

Common transformation matrices
--------------------------------
To simplify creating connection matrices for high-dimensional ensembles, you can use three additional 
parameters in the :func:`nef.Network.connect()` function: *weight*, *index_pre*, and *index_post*.  
*weight* specifies the overall gain on the connection across all dimensions, and defaults to 1.  For example::

  A=net.make('A',100,3)
  B=net.make('B',100,3)
  net.connect(A,B,weight=0.5)  # makes a transform matrix of 
                             # [[0.5,0,0],[0,0.5,0],[0,0,0.5]]

Note that the system by default assumes the identity matrix for the connection.  

If you don't want the identity matrix, and would prefer some other connectivity, specify *index_pre* and *index_post*.
These indicate which dimensions in the first ensemble should be mapped to which dimensions in the second ensemble.  
For example::

    A=net.make('A',100,3)
    B=net.make('B',100,1)
    net.connect(A,B,index_pre=2)
                                 # makes a transform matrix of 
                                 # [[0,0,1]]


    A=net.make('A',100,1)
    B=net.make('B',100,3)
    net.connect(A,B,index_post=0)
                                  # makes a transform matrix of 
                                  # [[1],[0],[0]]


    A=net.make('A',100,4)
    B=net.make('B',100,2)
    net.connect(A,B,index_pre=[1,2])
                                     # makes a transform matrix of 
                                     # [[0,1,0,0],[0,0,1,0]]
                                     # which makes B hold the 2nd and 3rd element of A


    A=net.make('A',100,4)
    B=net.make('B',100,3)
    net.connect(A,B,index_pre=[1,2],index_post=[0,1])
                                     # makes a transform matrix of 
                                     # [[0,1,0,0],[0,0,1,0],[0,0,0,0]]
                                     # which makes B hold the 2nd and 3rd element of A
                                     # in its first two elements




Adding noise to the simulation
--------------------------------

To make the inputs to neurons noisy, you can specify an amount of noise and a noise frequency (how often a new noise 
value is sampled from the uniform distribution between -noise and +noise).  Each neuron will sample from
this distribution at this rate, and add the resulting value to its input current.  The frequency defaults to 1000Hz::

  H=net.make('H',50,1,noise=0.5,noise_frequency=1000)

Changing modes: spiking, rate, and direct
------------------------------------------

You can set an ensemble to be simulated as spiking neurons, rate neurons, or directly (no neurons).  
The default is spiking neurons::

  J=net.make('J',neurons=1,dimensions=100,mode='direct')
  K=net.make('K',neurons=50,dimensions=1,mode='rate')
  
One common usage of direct mode is to quickly test out algorithms without worrying about the neural implementation.
This can be especially important when creating algorithms with large numbers of dimensions, since they would require
large numbers of neurons to simulate.  It can often be much faster to test the algorithm without neurons in direct
mode before switching to a realistic neural model.

.. note::
   When using direct mode, you may want to decrease the number of neurons in the population to 1, as this makes it
   much faster to create the ensemble.

Arrays of ensembles
--------------------

When building models that represent large numbers of dimensions, it is sometimes useful to break an ensemble down
into sub-ensembles, each of which represent a subset of dimensions.  Instead of building one large ensemble
to represent 100 dimensions, we might have 10 ensembles that represent 10 dimensions each, or 100 ensembles
representing 1 dimension each.

The main advantage of this is speed: It is much faster for the NEF methods to compute decoders for many 
small ensembles, rather than one big one.

However, there is one large disadvantage: you cannot compute nonlinear functions that use values in two
different ensembles.  One of the core claims of the NEF is that we can only approximate nonlinear functions
of two (or more) variables if there are neurons that respond to *both* dimensions.  However, it is still possible
to compute any linear function.

We create an array by specifying its length and (optionally) the number of dimensions per ensemble (the default is 1)::

  M=net.make_array('M',neurons=100,length=10,dimensions=1)
  
You can also use all of the parameters available in :func:`nef.Network.make()` to configure the
properties of the neurons.

.. note::
   The *neurons* parameter specifies the number of neurons *in each ensemble*, not the total number of neurons!  
  
The resulting array can be used just like a normal ensemble.  The following example makes a single
10-dimensional ensemble and a network array of 5 two-dimensional ensembles and connects one to the other::

  A=net.make_array('A',neurons=100,length=5,dimensions=2)
  B=net.make('B',neurons=500,dimensions=10)
  net.connect(A,B)
  
When computing nonlinear functions with an array, the function is applied to *each ensemble separately*.
The following computes the products of five pairs of numbers, storing the results in a single 5-dimensional array::

  A=net.make_array('A',neurons=100,length=5,dimensions=2)
  B=net.make('B',neurons=500,dimensions=5)
  def product(x):
      return x[0]*x[1]
  net.connect(A,B,func=product)


