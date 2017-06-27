******************
5. Basic scripting
******************

An example of basic scripting in Nengo.
The example network is a neural integrator.

* :doc:`Scripting documentation </scripting/index>`

.. raw:: html

   <iframe width="100%" height="480" src="http://www.youtube.com/embed/8TldkRu2EPg" frameborder="0" allowfullscreen></iframe>

Transcript
==========

In this tutorial we will be looking at scripting in Nengo.

And so I'm going to be constructing a neural integrator
in this Python file called myscript.py.
We use the python scripting language for all Nengo scripts.

I will begin by importing the NEF package.
The first thing I then need to do is create a Network.
I do this by calling the nef.Network function.
I need to give the Network a name,
so I will call it MyIntegrator.

Now we've created the network object
and we can begin to call functions
on that object to put things in the network.
So for instance, I will start by creating an input.
So I say the variable input will be equal
to the results of the make_input function.
I will simply call it 'input',
and give it an initial constant value of 0.
Nevertheless when we run that object in the network,
we will be able to call up a slider
and change that value on the fly in Interactive Plots.

The next thing I need to do is to add some neurons to this Network.
So, I will call the make function
and assign the results to the variable A.
We need to define some parameters for the make function.
First, we need to give it a name,
and then tell it how many neurons and the number of dimensions.
I will also set the quick value equal to true.

After running the script the first time,
setting quick to true will cause the script
to load the population of already created neurons.
In a network like this one, with only 100 neurons,
this won't make much difference.
However, with very large numbers of neurons,
the time to create a population can be significant,
so setting quick to true
can save a lot of time compiling the network.

There are many other parameters
that can also go into defining a population of neurons,
such as the maximum firing rates, membrane time constant, and so on.
If you want to know what those parameters are,
you basically have two options.

The first is to look directly at the code.
Here, this is the code for the make function.
As you can see this is found in your Nengo install directory
under the /python/nef subdirectory.
The file is called nef_core.py which defines
many of the critical functions for making networks in scripts.
Down below, you can see all of the different parameters that can be set,
and they are also documented in detail in the code itself.

If you prefer not to read the code,
you can also go to the Nengo website.
In the Nengo website, in the documentation section,
there is a section on scripting.
At the very bottom of that page,
many of the core classes in the scripting packages are listed.
If you click on any of these,
you will be taken to a formatted documentation page
that provides a listing of all of the various classes, functions, and so on.
For instance, the nef.Network package is there, and we can look into it.
Here you see all of the various parameters for the constructor listed.
As well, you can scroll down and find
the other functions defined on the class,
and all of the parameters with descriptions listed.
Here, for instance is make_input,
which we called earlier in our script.
And as you can see there are many, many other functions
that are useful for making networks,
but haven't been included in this brief scripting tutorial.

In any case, let us return to the script that we're writing.
As you will recall, we've now defined a population of 100 neurons called A.
We need to actually drive those neurons somehow,
so I'm going to do my first connection.
I'm going to connect the input we created to the A population.
And, I'm going to indicate how much weight
that input should be given as it drives those neurons.
This is essentially a gain factor
that determines how much the input is multiplied by
before being encoded by the neurons.

And then I will also tell it
what kind of temporal dynamics to give the input.
We call this the PTSC, which is how we define dynamics in general.
I'm going to give it a time constant of 100 milliseconds.
So essentially, the input will be filtered
by a low-pass filter with a 100 millisecond decay.

Of course the neurons are not talking to
any other neurons at this point,
they are simply being driven by the input.
Because we are constructing an integrator,
the connections we want to perform are actually quite simple.
Specifically, we want the population of neurons to talk to itself.
That is, we want to define lateral, or recurrent, connections
that will take the information currently
encoded by the population and project that to itself in the future,
so it can 'remember' its own value.
Any driving input will thus be added
to the current value being encoded by the neurons.

I will use the same PTSC as before: 100 milliseconds.
You will notice that I did not define a weight in this case.
That is because the scripting system will assume
connections have a weight of 1 if it is not specified.
Because the integrator is attempting
to perfectly remember its current state,
this is the appropriate weight.
The derivation of this is found in papers
that are linked from our website.

The last thing I need to do
is add all of these objects to the GUI,
so we can see them and manipulate them further if we want to.
I will now save that script.
And I can go over to the Nengo GUI.

I will click on the folder,
find my script, and double click on it to load it into the GUI.
You can see that we now have an integrator network
that has been added into the workspace.
We have the input we defined, and a population of neurons.
We can now run Interactive Plots
to see if it acts like an integrator as expected.

When we open interactive plots for the first time on a new network,
just the network elements are shown, connected as defined in the GUI.
It doesn't provide any data plots yet.
But, as I mentioned, we can right-click and pull up a controller,
so we can play with the input.
And we can right click on the population
and take a look at perhaps a spike raster,
which defaults to
showing a small percentage of the neurons in the population.
And perhaps we would like to see
the decoded values of the cells as well.

Things are a bit cluttered, so let me re-arrange things.
That's better.
Now we can click play to watch the network run.
Hopefully with no input there will not be too much drift.
Now I will give a bit of input,
and you see that it will integrate that value.
And if I take the input back to zero,
it should stop integrating and hold the value it has so far,
thus acting like a kind of memory.
And we can see it's doing exactly that kind of behaviour.

So, that concludes
this very brief introduction to the use of scripting.
I highly recommend taking a look at the website for more details.
And if you have any questions,
please feel free to use our mailing lists.
Thank you.
