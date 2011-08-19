Constructing a Population with Many Neurons
==============================================

*Purpose*: This demo shows how to construct and manipulate a population of neurons.

*Comments*: These are 100 leaky integrate-and-fire (LIF) neurons. The neuron tuning properties have been randomly selected. 

*Usage*: Grab the slider control and move it up and down to see the effects of increasing or decreasing input. As a population, these neurons do a good job of representing a single scalar value. This can be seen by the fact that the input graph and value graphs match well.

*Output*: See the screen capture below

.. image:: images/manyneurons.png(add input value to graph???)

*Code*::
    
    import nef
    
    net=nef.Network('Many Neurons')
    input=net.make_input('input',[-0.45])
    neuron=net.make('neurons',100,1,noise=1,quick=True)
    net.connect(input,neuron)
    net.add_to(world)

