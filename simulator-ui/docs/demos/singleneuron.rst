A Single Neuron
================================================

*Purpose*: This demo shows how to construct and manipulate a single neuron.

*Comments*: This leaky integrate-and-fire (LIF) neuron is a simple, standard model of a spiking single neuron. It resides inside a neural `population', even though there is only one neuron. 

*Usage*: Grab the slider control and move it up and down to see the effects of increasing or decreasing input. This neuron will fire faster with more input (an 'on' neuron). 

*Output*: See the screen capture below

.. image:: images/singleneuron.png

*Code*::

    import nef
    
    net=nef.Network('Single Neuron')
    input=net.make_input('input',[-0.45])
    neuron=net.make('neuron',1,1,max_rate=(100,100),intercept=(-0.5,-0.5),encoders=[[1]],noise=3)
    net.connect(input,neuron)
    net.add_to(world)


