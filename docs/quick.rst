Speeding up Network Creation (and Making Consistent Models) 
============================================================

Whenever Nengo creates an ensemble, it needs to compute a decoder.  This is done via the NEF
method of doing a least-squares minimization computation.  For large ensembles (~500 neurons or more), 
this can take some time, since it needs to invert an NxN matrix.

By deault, an ensemble consists of neurons with randomly generated encoders, intercepts, and
maximum firing rates (withing the specified ranges).  This means that a new decoder must be
computed for every new ensemble.  However, if we specify a random number seed, all of these
parameters will be consistent: that is, if we run the script again and re-create the same
ensemble with the same random number seed, Nengo will detect this and re-use the previously
computed decoder.  This greatly speeds up running the script.  (Of course, the first time
the script is run, it will still take the same amount of time).

There are two ways to specify the random number seed.  The first is to set the ``fixed_seed`` parameter
when creating the Network::

    net=Network('My Network',fixed_seed=5)
    
This tells Nengo to use the random seed ``5`` for every single ensemble that is created within
this network.  In other words, if you now do the following::

  net.make('A',neurons=300,dimensions=1)
  net.make('B',neurons=300,dimensions=1)

then the neurons within ensembles A and B will be *identical*.  This will also be true for
ensembles within a NetworkArray::

  net.make_array('C',300,10)
  
which will create an array of ten identical ensembles, each with 300 identical neurons.

Avoiding Identical Ensembles
----------------------------------

Using ``fixed_seed`` allows you to make networks very quickly, since Nengo takes advantage
of these identical ensembles and does not need to re-compute decoders.  However, it leads to
neurally implausible models, since neurons are not identical in this way.  As an alternative, you
can use the second way of specifying seeds, the ``seed`` parameter::

    net=Network('My Network',seed=5)

With this approach, Nengo will create a new seed for every ensemble, based on your initial seed
value.  There will be no identical neurons in your model.  However, if you re-run your script,
Nengo will re-generate exactly the same neurons, and so will be able to re-use the previously
computed decoders.

Sharing Consistent Models
--------------------------

A useful side-effect of the ``seed`` parameter is that it allows us to be sure that *exactly*
the same model is generated on different computers.  That is, if you build a model and set
a specific seed, then other researchers can run that same script and will get exactly the
same model as you created.  In this way we can ensure consistent performance.

Individual Differences
------------------------

One way to think about the ``seed`` parameter is as a way to generate neural models
with individual differences.  The same script run with a different ``seed`` value will
create a slightly different model, due to the low-level neural differences.  This may 
affect the overall behaviour.
    
