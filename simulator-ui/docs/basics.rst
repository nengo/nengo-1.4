Scripting Basics
============================

The Jython scripting interface in Nengo provides complete access to all of the underlying Java
functionality.  This provides complete flexibility, but requires users to manually create all of
the standard components (Origins, Terminations, Projections, etc), resulting in fairly
repetitive code.

To simplify the creation of Nengo models, we have developed the ``nef`` module as a wrapper around 
the most common functions needed for creating Nengo networks.  As an example, the following code will 
create a new network with an input that feeds into ensemble A which feeds into ensemble B::

  import nef
  net=nef.Network('Test Network')
  net.add_to_nengo()
  input=net.make_input('input',values=[0])
  A=net.make('A',neurons=100,dimensions=1)
  B=net.make('B',neurons=100,dimensions=1)
  net.connect(input,A)
  net.connect(A,B)

These scripts can be created with any text editor, saved with a ``.py`` extension, and run in Nengo
by choosing ``File->Open`` from the menu or clicking on the blue *Open* icon in the upper-left. All
of the examples in the ``demo`` directory have been written using this system.

Creating a network
--------------------

The first thing you need to do is to create your new network object and tell it to appear in the Nengo user 
interface::

  import nef
  net=nef.Network('My Network')
  net.add_to_nengo()

Creating an ensemble
-------------------------

Now we can create ensembles in our network.  You must specify the name of the ensemble, the number of 
neurons, and the number of dimensions::

  A=net.make('A',neurons=100,dimensions=1)
  B=net.make('B',1000,2)
  C=net.make('C',50,10)

You can also specify the radius for the ensemble.  The neural representation will be optimized to represent values
inside a sphere of the specified radius.  So, if you have a 2-dimensional ensemble and you want to be able to represent
the value ``(10,-10)``, you should have a radius of around 15::

  D=net.make('D',100,2,radius=15)
  
  
Creating an input
--------------------

To create a simple input that has a constant value (which can then be controlled interactive mode 
interface), do the following::

  inputA=net.make_input('inputA',values=[0])

The name can anything, and the values is an array of the required length.  So, for a 5-dimensional input, you can do::

  inputB=net.make_input('inputB',values=[0,0,0,0,0])

or:: 

  inputC=net.make_input('inputC',values=[0]*5)
  
You can also use values other than 0::

  inputD=net.make_input('inputD',values=[0.5,-0.3, 27])
  
Computing linear functions
---------------------------

To have components be useful, they have to be connected to each other.  To assist this process, the 
:func:`nef.Network.connect()` function will create the necessary Origins and/or Terminations as well as the Projection::

  A=net.make('A',100,1)
  B=net.make('B',100,1)
  net.connect(A,B)

You can refer to networks by name or by reference::

  net.make('A',100,1)
  net.make('B',100,1)
  net.connect('A','B')

You can also specify a transformation matrix (to allow for the computation of any linear
function) and a post-synaptic time constant::

  A=net.make('A',100,2)
  B=net.make('B',100,3)
  net.connect(A,B,transform=[[0,0.5],[1,0],[0,0.5]],pstc=0.03)

Computing nonlinear functions
------------------------------

To compute nonlinear functions, you can specify a function to compute and an origin will automatically be created::

    A=net.make('A',100,1)
    B=net.make('B',100,1)
    def square(x):
      return x[0]*x[0]
    net.connect(A,B,func=square)

This also works for highly complex functions::

    A=net.make('A',100,5)
    B=net.make('B',100,1)
    import math
    def strange(x):
      if x[0]<0.4: return 0.3
      elif x[1]*x[2]<0.3: return math.sin(x[3])
      else: return x[4]
    net.connect(A,B,func=strange)


