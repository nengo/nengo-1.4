Generating large ensembles
==========================

With the standard Nengo install,
creating neural populations
with more than 500 neurons takes a very long time.
This is because Nengo needs to solve for the decoders,
which involves (pseudo-)inverting an :math:`N \times N` matrix,
where :math:`N` is the number of neurons.

By default, Nengo is a pure Java application,
so in order to perform this matrix pseudo-inversion,
it uses a Java implementation of singular value decomposition (SVD),
which is quite slow.
To speed this up,
we can tell Nengo to call out to another program (Python)
to perform this operation.

To ensure that this process works properly,
follow these steps.

1. Install Python, SciPy, and NumPy
-----------------------------------

Python is a freely available programming language,
and it has two libraries (SciPy and NumPy)
which provide most of the high-speed
matrix operations found in Matlab.

For detailed instructions on installing these tools
on all operating systems, `go here
<https://nengo.github.io/download.html#installing-python-and-numpy>`_.

.. note:: Nengo 1.4 is only tested with Python 2.7.
          Python 3.x may work, but if you run into problems,
          try downgrading to Python 2.7.

2. Tell Nengo where Python is
-----------------------------

For Linux and OS X,
Nengo should automatically be able to find Python to run it.
However, this seems not to be the case for some versions of Windows.
Under Windows, you will have to edit the file
``external/pseudoInverse.bat`` by changing this::

  python pseudoInverse %1 %2 %3 %4

into this::

  C:\python27\python.exe pseudoInverse %1 %2 %3 %4

Where ``C:\python27`` is the directory
in which you installed Python.

3. Test
-------

You should now be able to create larger ensembles in Nengo.
A population with 500 neurons should take 5 -- 10 seconds.
1000 neurons should take 15 -- 30 seconds.
The largest we recommend using this technique is 5000 neurons,
which may take up to half an hour.
As always, you can make use of
the :doc:`quick </scripting/speed>` option
to re-use ensembles once they have been created.

To confirm that Nengo is using this faster approach,
you can examine the console output from Nengo.
This is the window Nengo is being run from,
and on Windows this is a black window
showing a lot of text as ensembles are created.
This is not the Script Console
at the bottom of the main Nengo window.

If the external script is working,
when you create an ensemble you should see something like this::

  INFO  [Configuring NEFEnsemble:ca.nengo.util.Memory]: Used: 62454552 Total: 101449728 Max: 810942464 (before gamma)
  INFO  [Configuring NEFEnsemble:ca.nengo.util.Memory]: Used: 71208944 Total: 101449728 Max: 810942464 (before inverse)
  INFO  [Configuring NEFEnsemble:ca.nengo.util.Memory]: Used: 69374440 Total: 164933632 Max: 810942464 (after inverse)

If the external script is not working,
when you create an ensemble you will see something like this::

  INFO  [Configuring NEFEnsemble:ca.nengo.util.Memory]: Used: 108627944 Total: 164933632 Max: 810942464 (before gamma)
  INFO  [Configuring NEFEnsemble:ca.nengo.util.Memory]: Used: 108627944 Total: 164933632 Max: 810942464 (before inverse)
  File not found: java.io.FileNotFoundException: external\matrix_-3645035487712329947.inv (The system cannot find the file specified)
  INFO  [Configuring NEFEnsemble:ca.nengo.math.impl.WeightedCostApproximator]: Using 53 singular values for pseudo-inverse
  INFO  [Configuring NEFEnsemble:ca.nengo.util.Memory]: Used: 75885904 Total: 164933632 Max: 810942464 (after inverse)

Nengo will still operate properly in this case,
though decoder solving will be slower.
