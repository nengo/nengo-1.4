Tips and tricks
===============

Common transformation matrices
------------------------------

To simplify creating connection matrices
for high-dimensional ensembles,
you can use three additional parameters
in the :func:`nef.Network.connect` function:
*weight*, *index_pre*, and *index_post*.
*weight* specifies the overall gain
on the connection across all dimensions, and defaults to 1.
For example::

  net.make('A', 100, 3)
  net.make('B', 100, 3)
  net.connect('A', 'B', weight=0.5)
  # results in a transform matrix of [[0.5, 0, 0], [0, 0.5, 0], [0, 0, 0.5]]

Note that the system by default
assumes the identity matrix for the connection.

If you don't want the identity matrix,
and would prefer some other connectivity,
specify *index_pre* and *index_post*.
These indicate which dimensions
in the first ensemble should be mapped
to which dimensions in the second ensemble.
For example::

  net.make('A', 100, 3)
  net.make('B', 100, 1)
  net.connect('A', 'B', index_pre=2)
  # results in a transform matrix of [[0, 0, 1]]

  net.make('A', 100, 1)
  net.make('B', 100, 3)
  net.connect('A', 'B', index_post=0)
  # results in a transform matrix of [[1], [0], [0]]

  net.make('A', 100, 4)
  net.make('B', 100, 2)
  net.connect('A', 'B', index_pre=[1, 2])
  # results in a transform matrix of [[0, 1, 0, 0], [0, 0, 1, 0]]
  # which makes B hold the 2nd and 3rd element of A

  net.make('A', 100, 4)
  net.make('B', 100, 3)
  net.connect('A', 'B', index_pre=[1, 2], index_post=[0, 1])
  # results in a transform matrix of [[0, 1, 0, 0], [0, 0, 1, 0], [0, 0, 0, 0]]
  # which makes B hold the 2nd and 3rd element of A in its first two elements

Adding noise to the simulation
------------------------------

To make the inputs to neurons noisy,
you can specify an amount of noise
and a noise frequency
(how often a new noise value is sampled
from the uniform distribution between -noise and +noise).
Each neuron will sample from this distribution at this rate,
and add the resulting value to its input current.
The frequency defaults to 1000 Hz::

  net.make('H', 50, 1, noise=0.5, noise_frequency=1000)


Random inputs
-------------

Here is how you can convert an input
to provide a randomly changing value,
rather than a constant::

  net.make_fourier_input(
      'input', dimensions=1, base=0.1, high=10, power=0.5, seed=0)

This will produce a randomly varying input.
This input will consist of random sine waves
varying from 0.1 Hz to 10 Hz,
in 0.1 Hz increments.
The random number seed used is 0.

Changing modes: spiking, rate, and direct
-----------------------------------------

You can set an ensemble to be simulated with
spiking neurons, rate neurons, or no neurons (directly).
The default is to use spiking neurons.
To change the mode, use the ``mode`` argument::

  net.make('J', neurons=1, dimensions=100, mode='direct')
  net.make('K', neurons=50, dimensions=1, mode='rate')

One common usage of direct mode
is to quickly test out algorithms
without worrying about the neural implementation.
This can be especially important
when creating algorithms with large numbers of dimensions,
since they would require large numbers of neurons to simulate.
It can often be much faster
to test the algorithm without neurons in direct mode
before switching to a realistic neural model.

.. note::  When using direct mode,
           you may want to decrease the number of neurons
           in the population to 1,
           as this makes it much faster to create the ensemble.

Arrays of ensembles
-------------------

When building models that represent large numbers of dimensions,
it is sometimes useful to
break an ensemble down into sub-ensembles,
each of which represent a subset of dimensions.
Instead of building one large ensemble to represent 100 dimensions,
we might have 10 ensembles
that represent 10 dimensions each,
or 100 ensembles representing 1 dimension each.

The main advantage of this is speed:
it is much faster for the NEF methods
to compute decoders for many small ensembles,
rather than one big one.

However, there is one large disadvantage:
you cannot compute nonlinear functions
that use values in two different ensembles.
One of the core claims of the NEF
is that we can only approximate
nonlinear functions of two (or more) variables
if there are neurons that respond to *both* dimensions.
However, it is still possible
to compute any linear function.

We create an array
by specifying its length
and (optionally) the number of dimensions per ensemble
(the default is 1)::

  net.make_array('M', neurons=100, length=10, dimensions=1)

You can also use all of the parameters
available in :func:`nef.Network.make`
to configure the properties of the neurons.

.. note:: The *neurons* parameter specifies the number of neurons
          *in each ensemble*, not the total number of neurons!

The resulting array can be used just like a normal ensemble.
The following example makes a single 10-dimensional ensemble
and a network array of 5 two-dimensional ensembles
and connects one to the other::

  net.make_array('A', neurons=100, length=5, dimensions=2)
  net.make('B', neurons=500, dimensions=10)
  net.connect('A', 'B')

When computing nonlinear functions with an array,
the function is applied to *each ensemble separately*.
The following computes the products of five pairs of numbers,
storing the results in a single 5-dimensional array::

  net.make_array('A', neurons=100, length=5, dimensions=2)
  net.make('B', neurons=500, dimensions=5)

  def product(x):
      return x[0] * x[1]

  net.connect('A', 'B', func=product)

Matrix operations
-----------------

To simplify the manipulation of matrices,
we have added a version of JNumeric to Nengo.
This allows for a syntax similar to Matlab,
but based on the NumPy Python library.

To use this for matrix manipulation,
you will first have to convert any matrix you have
into an array object::

  a = [[1, 2, 3], [4, 5, 6]] # old method (list of lists)
  a = array([[1, 2, 3], [4, 5, 6]])  # new method (JNumeric array)

You can also specify the storage format to be used as follows::

  a = array([[1, 2, 3], [4, 5, 6]], typecode='f')

.. note:: Valid values for the typecode parameter are:

          * ``'1'`` int8
          * ``'s'`` int16
          * ``'i'`` int32
          * ``'l'`` int64
          * ``'f'`` float32
          * ``'d'`` float64
          * ``'F'`` complex64
          * ``'D'`` complex128

The first important thing you can do with this array
is use full slice syntax.
This is the ``[:]`` notation used to access part of an array.
A slice is a set of three values,
all of which are optional.
``[a:b:c]`` means to start at index ``a``,
go to index ``b`` (but not include ``b``),
and have a step size of ``c`` between items.
The default for ``a`` is 0,
for ``b`` is the length of the array,
and for ``c`` is 1.
For multiple dimensions,
we put a comma between slices for each dimension.
The following examples are all for a 2D array.
Note that the order of the 2nd and 3rd parameters
are reversed from Matlab,
and all indices start at 0.
Additionally, negative indices can be used,
and indicate the number of indices
from the end (e.g., ``-1`` is the last index)::

  a[0]        # the first row
  a[0, :]     # the first row
  a[0:3]      # the first three rows
  a[:, 0]     # the first column
  a[:, 0:3]   # the first three columns
  a[:, :3]    # the first three columns
  a[:, 2:]    # all columns from the 2nd to the end
  a[:, :-1]   # all columns except the last one
  a[::2]      # just the even-numbered rows
  a[::3]      # every third row
  a[::-1]     # all rows in reverse order
  a[:, ::2]   # just the even-numbered columns
  a[:, ::-1]  # all columns in reverse order
  a.T         # transpose

With such an array, you can perform element-wise operations as follows::

  c = a + b   # element-wise addition; same as .+ in matlab
  c = a * b   # element-wise multiplication; same as .* in matlab
  b = cos(a)  # computes cosine of all values in a

.. note::
   Other functions that work on arrays include:
   ``add``, ``subtract``, ``multiply``, ``divide``, ``remainder``, ``power``,
   ``arccos``, ``arccosh``, ``arcsinh``, ``arctan``, ``arctanh``,
   ``ceil``, ``floor``, ``conjugate``, ``imaginary``,
   ``cos``, ``cosh``, ``sin``, ``sinh``, ``tan``, ``tanh``,
   ``exp``, ``log``, ``log10``, ``real``, ``sqrt``,
   ``maximum``, ``minimum``, ``equal``, ``not_equal``,
   ``less``, ``less_equal``, ``greater``, ``greater_equal``,
   ``logical_and``, ``logical_or``, ``logical_xor``, ``logical_not``,
   ``bitwise_and``, ``bitwise_or``, ``bitwise_xor``

You can also create particular arrays::

  arange(5)     # same as array(range(5)); same as array([0, 1, 2, 3, 4])
  arange(2, 5)  # same as array(range(2, 5)); same as array([2, 3, 4])

  eye(5)                      # 5x5 identity matrix
  ones((3, 2))                # 3x2 matrix of all 1
  ones((3, 2), typecode='f')  # 3x2 matrix of all 1.0 (floating point values)
  zeros((3, 2))               # 3x2 matrix of all 0

The following functions help manipulate the shape of a matrix::

  a.shape                 # get the current size of the matrix
  b = reshape(a, (3, 4))  # convert to a 3x4 matrix (must already have 12 elements)
  b = resize(a, (3, 4))   # convert to a 3x4 matrix (can start at any size)
  b = ravel(a)            # convert to a 1-D vector
  b = diag([1, 2, 3])     # create a diagonal matrix with the given values

Some basic linear algebra operations are available::

  c = dot(a, b)
  c = dot(a, a.T)
  c = innerproduct(a, a)
  c = convolve(a, b)

And Fourier transforms::

  b = fft(a)
  a = ifft(b)

The following functions also exist:
``argmax``, ``argsort``, ``argmin``, ``asarray``, ``bitwise_not``, ``choose``,
``clip``, ``compress``, ``concatenate``, ``fromfunction``, ``indices``,
``nonzero``, ``searchsorted``, ``sort``, ``take``, ``where``,
``tostring``, ``fromstring``, ``trace``, ``repeat``,
``diagonal``, ``sum``, ``cumsum``, ``product``, ``cumproduct``,
``alltrue``, ``sometrue``

The vast majority of the time,
you can use these objects the same way
you would a normal list of values
(e.g., for specifying transformation matrices).
If you ever need to explicitly convert one back into a list,
you can call the ``tolist`` method::

  a = array([1, 2, 3])
  b = a.tolist()

These functions are all available at the Nengo console
and in any script called using the ``run`` command.
To access them in a separate script file, do::

  from numeric import *
