Cycling Through a Sequence
============================
*Purpose*: This demo uses the basal ganglia model to cycle through a 5 element sequence.

*Comments*: This basal ganglia is now hooked up to a memory.  This allows it to update that memory based on its current input/action mappings.  The mappings are defined in the code such that A->B, B->C, etc. until E->A completing a loop.

*Usage*: When you run the network, it will go through the sequence forever.  It's interesting to note the distance between the 'peaks' of the selected items.  It's about 40ms for this simple action.  We like to make a big deal of this.

*Output*: See the screen capture below. 

.. image:: images/sequence.png

*Code*::
    
    import nef
    import nps
    
    net=nef.Network('Sequence')
    p=nps.ProductionSet()
    p.add(lhs=dict(memory='A'),rhs=dict(memory='B'))
    p.add(lhs=dict(memory='B'),rhs=dict(memory='C'))
    p.add(lhs=dict(memory='C'),rhs=dict(memory='D'))
    p.add(lhs=dict(memory='D'),rhs=dict(memory='E'))
    p.add(lhs=dict(memory='E'),rhs=dict(memory='A'))
    model=nps.NPS(net,p,dimensions=10,neurons_buffer=30,align_hrr=True)
    model.set_initial(0.1,memory='A')
    model.add_buffer_feedback(memory=1)
    net.add_to(world)



