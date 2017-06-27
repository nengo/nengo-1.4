*****************************
1. Basic network construction
*****************************

An example of network construction using Nengo.
The example network is a neural integrator.

* :doc:`Written tutorial </tutorials/4-dynamics>`
* :doc:`Demo documentation and script </demos/integrator>`

.. raw:: html

   <iframe width="100%" height="480" src="https://www.youtube.com/embed/UNBP3hMcFb8" frameborder="0" allowfullscreen></iframe>

Transcript
==========

*(0:00)*
In this video I will demonstrate simple network construction using Nengo.
The example I will use is a neural integrator.
This is a network that is used to store information stably over time.
If you look along the left-hand side of Nengo,
you see a pallet of icons that can be used to construct networks.
Whenever we are beginning a model in Nengo,
we have to start with an empty network.
So I will drag the network icon and drop it into my empty workspace.
It is asking for a name, so I will call this "integrator_demo".
Clicking okay, Nengo constructs an empty network for us.

*(0:32)*
In order to do anything interesting,
we need to add neurons to our network,
so we can grab an ensemble and drop it into the empty network.
Again it is requesting information,
so I will call this the "integrator" population.
It is asking for the number of neurons, or nodes,
that we would like in this population,
we will leave that as a hundred.
This is asking for the dimension of the variable
to be represented by those 100 nodes.
I will leave that as one as we would like it
to represent a scalar value.
It is asking for the kind of neuron model
that we would like to be used.
I will again leave this as the default leaky integrate-and-fire neuron,
which is a simple spiking model of a neuron.
There are several parameters that we can set
but I will leave these again at their default.
And finally, it's asking for the radius.
This determines the maximum value of the variable
that's being represented by these neurons.
Again I will leave this as one.
If you would like other information
about what each of these parameters is for,
you can click the "?" nearby
and additional information will be provided.
Click okay and those neurons are then generated for us.

*(1:35)*
We can zoom in on those neurons by using our scroll wheel,
or we can drag them around in order to organize our network.
For the remainder of this demo,
I will simply maximize this window
since it's the only network that I'll be using.
We can also double-click on this population of cells
to see each of the individual nodes in here.
But again, I will just be treating this
as a population for this demonstration.
In order to get our nodes to do something interesting
we need to put input into them.

*(2:03)*
So if we return back to our palette,
we can see an icon called termination.
This is a method for introducing inputs
into neurons in a population of cells.
So I'll drag the termination and drop it on my integrator population.
And now we'll need to define this set of inputs.
I will call it "input," it will have a dimension of one,
since we're working with a scalar integrator.
Then we have a postsynaptic time constant,
which currently is set to .1 seconds, or 100 milliseconds.
The postsynaptic time constant is the decay rate
of the postsynaptic responses in these cells.
I will set it to .1 to simulate NMDA-like receptors.
As we describe on the website,
the particular weight that we would like
for a neural integrator will be equal to .1,
or the same as the postsynaptic time constant that we just defined.
So I'll set that to .1 and click okay.
Click okay again, and we can now see an input here.
We can think of this as a set of synapses
which will act in the way that we defined,
and that we can now connect things to.

*(3:04)*
Of course, we need another set of synapses
for a recurrent connection if we want to implement an integrator.
So I will drag a second termination, and drop it.
I will call this the recurrent connection, or "recur".
Again, it will also have an input dimension of one.
As we describe elsewhere,
this weight must be set to one
because we would essentially like to take the information
that is currently in the network,
and project it back into the network
because we would like to store it over time,
if there is no additional input.
Click okay.
Again we will have a 100 millisecond postsynaptic time constant.
Click okay. So now we have another set of synapses on these cells.

*(3:43)*
This one, since it's a recurrent connection,
will take the output from this population of cells
and connect it to the input in the same set of cells.
We can think of what is happening,
when I take this icon and I'm dropping it on this recurrent connection,
is that all of the connection weights
between neurons in this population have been computed for us by Nengo.
Those connection weights will implement the dynamics we have defined,
which in this case is a simple integrator.

*(4:11)*
Finally, to drive this network
we need to actually put some input into it.
So I will take a generic input, this f(t) icon on the side,
and put it in my network.
We can then define what we would like that function to be.
Click "set functions" and there are several that we can choose from,
but in this case I would just like a step,
so I will call this a "piecewise constant function."
Click new.
Now for my step function I must tell it
at what time I would like it to turn on
and at what time I would like it to turn off.
So we have 2 numbers to define.
I would like it to turn on at 100 milliseconds
and turn off at 200 milliseconds.
Save that.
We also must tell it what values we would like it to take on
before, during, and after that step.
So before and after we will leave as 0,
and during I'll set this to be equal to 5,
which should take us to about half height.
Click okay, create that function, click okay and okay again.
Then we have that function generated for us,
which will now generate that step.
Connect it to the input of these cells,
so we can drive the cells with this step input in the variable.

*(5:19)*
Now, it has taken me some time to construct this network,
largely because I've been talking.
But what I'm going to do is reconstruct the same network
without talking just to show you
how rapid network construction can be.
To remove a network we can right-click on it,
and select "remove model" and say yes.
So I just deleted the network I had constructed.
So let me begin and construct it again,
this time without talking so much.

*(5:40)*
I drop my neurons in, and use all the defaults.
I would like a termination on here called recurrent.
You can see that I'm taking advantage of the fact that
Nengo remembers the last thing I did.
I want an input,
this is going to have a different weight
equal to the postsynaptic time constant .1.
Click okay.
I would like to connect that as recurrent.
I would like an input.
I would like these functions to be equal to what they were before,
another step function.
And then I can drag-and-drop, and I'm done.
In the next video I will show how to simulate this network.
