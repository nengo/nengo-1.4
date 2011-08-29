Interactive Plots Layout Files
=================================================

The interactive plots mode in Nengo shows a wide variety of information about a
running Nengo model, including graphs to interpret the value being represented
within a network and interactive controls which allow the inputs to the system to
be varied.

The exact configuration of this view is saved in a text file in the ``layouts`` directory,
using the name of the network as an identifier.  For example, the layout for the
multiplication demo is saved as ``layouts/Multiply.layout`` and looks like:
  .. literalinclude:: ../../layouts/Multiply.layout
  
The first line gives the size and location of the window, the last line gives the 
setting of the various simulation parameters, and the middle lines define the
various plots that are displayed.

While this saved file format is human-readable, it is not meant to be hand-coded.  The best
way to create a layout is to open up the Interactive plots window, create the layout you
want, and save it.  A corresponding file will be created in the ``layouts`` folder.

Specifying a Layout
--------------------

If you want to, you can define a layout in a script by cutting and pasting from 
a ``.layout`` file.  For this, you can use the :py:func:`nef.Network.set_layout` function.

For example, here we define a simple network and directly specify the layout to use::

    import nef

    net=nef.Network('My Test Model')
    input=net.make_input('input',[0,0])
    neuron=net.make('neurons',100,2,quick=True)
    net.connect(input,neuron)
    net.add_to_nengo()

    net.set_layout({'height': 473, 'x': -983, 'width': 798, 'state': 0, 'y': 85},
     [('neurons', None, {'x': 373, 'height': 32, 'label': 0, 'width': 79, 'y': 76}),
     ('input', None, {'x': 53, 'height': 32, 'label': 0, 'width': 51, 'y': 76}),
     ('neurons', 'voltage grid', {'x': 489, 'height': 104, 'auto_improve': 0, 'label': 0, 'width': 104, 'rows': None, 'y': 30}),
     ('neurons', 'value|X', {'x': 601, 'height': 105, 'sel_dim': [0, 1],'label': 0, 'width': 158, 'autozoom': 0, 'last_maxy': 1.0, 'y': 29}),
     ('neurons', 'preferred directions', {'x': 558, 'height': 200, 'label': 0, 'width': 200, 'y': 147}),
     ('neurons', 'XY plot|X', {'width': 200, 'autohide': 1, 'last_maxy': 1.0, 'sel_dim': [0, 1], 'y': 148, 'label': 0, 'x': 346, 'height': 200, 'autozoom': 1}),
     ('input', 'control', {'x': 67, 'height': 200, 'range': 1.0, 'label': 1, 'limits': 1, 'width': 120, 'limits_w': 0, 'y': 145})],
     {'sim_spd': 4, 'rcd_time': 4.0, 'filter': 0.03, 'dt': 0, 'show_time': 0.5})
     
This ability is useful when sending a model to someone else, so that they will automatically
see the particular set of graphs you specify.  This can be easier than also sending the
``.layout`` file.


