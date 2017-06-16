*************************
Introduction to scripting
*************************

The Jython scripting interface in Nengo
provides complete access to
all of the underlying Java functionality.
This provides complete flexibility,
but requires users to manually create
all of the standard components
(e.g., Origins, Terminations, Projections),
resulting in fairly repetitive code.

To simplify the creation of Nengo models,
we have developed the ``nef`` module
as a wrapper around the most common functions
needed for creating Nengo networks.
As an example,
the following code will create a new network
with an input that feeds into ensemble A,
which feeds into ensemble B::

  import nef

  net = nef.Network('Test network')
  net.add_to_nengo()
  net.make_input('input', values=[0])
  net.make('A', neurons=100, dimensions=1)
  net.make('B', neurons=100, dimensions=1)
  net.connect('input', 'A')
  net.connect('A', 'B')

Creating a network
------------------

A Nengo script should almost always start
by creating a new network object
and adding it to the Nengo user interface::

  import nef

  net = nef.Network('My network')
  net.add_to_nengo()

Creating an ensemble
--------------------

Now we can create ensembles in our network.
You must specify the name of the ensemble,
the number of neurons, and the number of dimensions::

  net.make('A', neurons=100, dimensions=1)
  net.make('B', 1000, 2)
  net.make('C', 50, 10)

You can also specify the radius for the ensemble.
The neural representation will be optimized to represent
values inside a sphere of the specified radius.
So, if you have a 2-dimensional ensemble
and you want to be able to represent the value ``(10, -10)``,
you should have a radius of around 15::

  net.make('D', 100, 2, radius=15)


Creating an input
-----------------

To create a simple input that has a constant value
(which can then be controlled interactive mode interface),
do the following::

  net.make_input('inputA', values=[0])

The name can anything,
and ``values`` is a list of the required length.
So, for a 5-dimensional input, you can do::

  net.make_input('inputB', values=[0, 0, 0, 0, 0])

or::

  net.make_input('inputC', values=[0] * 5)

You can also use values other than 0::

  net.make_input('inputD', values=[0.5, -0.3, 27])

Computing linear functions
--------------------------

To have components be useful,
they have to be connected to each other.
To assist this process,
the :func:`nef.Network.connect`
function will create the necessary Origins and Terminations,
as well as the Projection::

  net.make('A', 100, 1)
  net.make('B', 100, 1)
  net.connect('A', 'B')

You can also specify a transformation matrix
(to allow for the computation of any linear function)
and a post-synaptic time constant::

  net.make('A', 100, 2)
  net.make('B', 100, 3)
  net.connect('A', 'B', transform=[[0, 0.5], [1, 0], [0, 0.5]], pstc=0.03)

Computing nonlinear functions
-----------------------------

To compute nonlinear functions,
you can specify a function to compute
and an origin will automatically be created::

    net.make('A', 100, 1)
    net.make('B', 100, 1)

    def square(x):
        return x[0] * x[0]

    net.connect('A', 'B', func=square)

This also works for highly complex functions::

    import math

    net.make('A', 100, 5)
    net.make('B', 100, 1)

    def strange(x):
        if x[0] < 0.4:
            return 0.3
        elif x[1] * x[2] < 0.3:
            return math.sin(x[3])
        else:
            return x[4]

    net.connect('A', 'B', func=strange)

Nengo will automatically solve
for the decoders and connection weights
needed to approximate these highly complex functions.
