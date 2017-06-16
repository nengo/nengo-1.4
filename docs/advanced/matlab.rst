Running Nengo in Matlab
=======================

Since Nengo is a Java application,
it can be directly embedded within Matlab.
This allows for stimuli and data analysis
to be performed within Matlab,
which may be more familiar to some users.

There are several steps to setting up Nengo to run within Matlab.

1. Set up your Matlab classpath
-------------------------------

Matlab needs to know where to find
all the Java files needed for running Nengo.
You do this by adding the names of all the ``.jar`` files
to Matlab's ``classpath.txt`` file.
You can edit this file from within Matlab
by entering ``edit classpath.txt`` in the command prompt.

You need to add a bunch of lines
to the end of this file that list all the ``.jar`` files
found in your installation of Nengo and their full path.
For example, if you unzipped ``nengo-latest.zip``
to ``D:\Code\Nengo``, you would add the following lines::

  D:\Code\Nengo\nengo-8d22507.jar
  D:\Code\Nengo\lib\colt.jar
  D:\Code\Nengo\lib\commons-collections-3.2.jar
  D:\Code\Nengo\lib\formsrt.jar
  D:\Code\Nengo\lib\itextpdf-5.3.4.jar
  D:\Code\Nengo\lib\Jama-1.0.2.jar
  D:\Code\Nengo\lib\jayatana-1.2.4.jar
  D:\Code\Nengo\lib\jbullet.jar
  D:\Code\Nengo\lib\jcommon-1.0.0.jar
  D:\Code\Nengo\lib\jfreechart-1.0.1.jar
  D:\Code\Nengo\lib\jgrapht-jdk1.5-0.7.3.jar
  D:\Code\Nengo\lib\jmatio.jar
  D:\Code\Nengo\lib\jnumeric-0.1.jar
  D:\Code\Nengo\lib\jpct.jar
  D:\Code\Nengo\lib\jung-1.7.6.jar
  D:\Code\Nengo\lib\junit.jar
  D:\Code\Nengo\lib\jython.jar
  D:\Code\Nengo\lib\log4j-1.2.16.jar
  D:\Code\Nengo\lib\macify-1.4.jar
  D:\Code\Nengo\lib\piccolo.jar
  D:\Code\Nengo\lib\piccolox.jar
  D:\Code\Nengo\lib\qdox-1.6.3.jar
  D:\Code\Nengo\lib\ssj.jar
  D:\Code\Nengo\lib\vecmath.jar

This is technically all that needs to be done
to run Nengo inside Matlab.
Once you restart Matlab, you should be able to do::

  import ca.nengo.model.impl.*;
  network = NetworkImpl();
  network.setName('test');

All the basic functionality of Nengo is exposed in this way.

You may also want to increase the memory available to Java.
To do this, create a file called `java.opts`
in the Matlab startup directory with the following text in it::

  -Xmx1500m

This will give Java a maximum of 1500 MB (1.5 GB) of memory.

2. Connecting Python and Matlab
-------------------------------

Since we're used to creating models
using the Python scripting system,
we want to do the same in Matlab.
To set this up, we do the following in Matlab::

  import org.python.util.*;
  python = PythonInterpreter();
  python.exec('import sys; sys.path.append("Nengo/python")')

The path specified on the last line
must be your path to the ``python`` directory
in your Nengo installation.

You can now run the same Python scripts
that you can in the Nengo scripting system::

  python.execfile('addition.py')

or, if the file is in another directory::

  python.execfile('Nengo\demo\addition.py')

We can also execute single python commands like this::

  python.exec('net.view(play=0.5)')

3. Running a simulation and gathering data
------------------------------------------

However, what we really want to do
is use Matlab to run a simulation,
and gather whatever data we need.
Here is how we can do that.

First, we need to know how to get access
to objects created in the Python script.
For example, if we made an ensemble called C
with ``C = net.make('C', 100, 1)`` in the script,
we can get this object in Matlab as follows::

  C = javaMethod('__tojava__', python.get('C'), java.lang.Class.forName('java.lang.Object'));

Now we can get the value of its origin by something like::

  C.getOrigin('X').getValues().getValues();

With this in mind, the following Matlab code
runs a simulation and gathers the output from C's X origin

.. code:: matlab

   python.execfile('addition.py');

   % get the ensemble 'C'
   C = javaMethod('__tojava__', python.get('C'), java.lang.Class.forName('java.lang.Object'));

   % run the simulation
   t = 0.0;
   dt = 0.001;
   data = [];
   w = waitbar(0, 'Running simulation...');
   while(t < 1)
       waitbar(t);
       command = sprintf('net.network.simulator.run(%f,%f,%f)', t, t + dt, dt);
       python.exec(command);
       data = [data C.getOrigin('X').getValues().getValues()];
       t = t + dt;
   end
   close(w);
