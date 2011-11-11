Running Nengo in Matlab
================================

Since Nengo is a Java application, it can be directly embedded within Matlab.  This allows for stimuli and data analysis 
to be performed within Matlab, which may be more familiar to some users.


Step 1: Set up your Matlab classpath
-------------------------------------

Matlab needs to know where to find all the java files needed for running Nengo. You do this by adding the names of all
the ``.jar`` files to Matlab’s ``classpath.txt`` file. You can edit this file from within Matlab by typing ``edit classpath.txt``.

You need to add a bunch of lines to the end of this file that list all the ``.jar`` files found in your installation of 
Nengo and their full path. For example::

    D:\MatNengo\nengo-1074\nengo-1074.jar
    D:\MatNengo\nengo-1074\lib\Blas.jar
    D:\MatNengo\nengo-1074\lib\colt.jar
    D:\MatNengo\nengo-1074\lib\commons-collections-3.2.jar
    D:\MatNengo\nengo-1074\lib\formsrt.jar
    D:\MatNengo\nengo-1074\lib\iText-5.0.5.jar
    D:\MatNengo\nengo-1074\lib\Jama-1.0.2.jar
    D:\MatNengo\nengo-1074\lib\jcommon-1.0.0.jar
    D:\MatNengo\nengo-1074\lib\jfreechart-1.0.1.jar
    D:\MatNengo\nengo-1074\lib\jmatio.jar
    D:\MatNengo\nengo-1074\lib\jung-1.7.6.jar
    D:\MatNengo\nengo-1074\lib\jython.jar
    D:\MatNengo\nengo-1074\lib\log4j-1.2.14.jar
    D:\MatNengo\nengo-1074\lib\piccolo.jar
    D:\MatNengo\nengo-1074\lib\piccolox.jar
    D:\MatNengo\nengo-1074\lib\qdox-1.6.3.jar
    D:\MatNengo\nengo-1074\lib\ssj.jar
    D:\Matnengo\nengo-1074\python\jar\jbullet.jar
    D:\Matnengo\nengo-1074\python\jar\jpct.jar
    D:\Matnengo\nengo-1074\python\jar\vecmath.jar

This is technically all that needs to be done to run Nengo inside Matlab. Once you restart Matlab, you should be able to do::

    import ca.nengo.model.impl.*;
    network=NetworkImpl()
    network.setName('test')

All the basic functionality of Nengo is exposed in this way.

You may also want to increase the memory available to Java. To do this, create a file called `java.opts` in the Matlab 
startup directory with the following command in it::

    -Xmx1500m

This will give Java a maximum of 1500MB of memory.



Step 2: Connecting Python and Matlab
-------------------------------------

Since we’re used to creating models using the Python scripting system, we want to do the same in Matlab. To set this 
up, we do the following in Matlab::

    import org.python.util.*;
    python=PythonInterpreter();
    python.exec('import sys; sys.path.append("nengo-1074/python")')

The path specified on the last line must be your path to the python directory in your Nengo installation.

You can now run the same Python scripts that you can in the Nengo scripting system::

    python.execfile("addition.py")

We can also execute single python commands like this::

    python.exec("net.view(play=0.5)")


Step 3: Running a simulation and gathering data
--------------------------------------------------

However, what we really want to do is use Matlab to run a simulation, and gather whatever data we need. Here is how we can do that.

First, we need to know how to get access to objects created in the python script. For example, if we made an 
ensemble called C with ``C=net.make('C',100,1)`` in the script, we can get this object in Matlab as follows::

    C=javaMethod('__tojava__',python.get('C'),java.lang.Class.forName('java.lang.Object'));

Now we can get the value of its origin by something like::

    C.getOrigin('X').getValues().getValues()

With this in mind, the following Matlab code runs a simulation and gathers the output from C’s X origin::

    python.execfile('addition.py');
     
    % get the ensemble 'C'
    C=javaMethod('__tojava__',python.get('C'),java.lang.Class.forName('java.lang.Object'));
     
    % run the simulation
    t=0.0;
    dt=0.001;
    data=[];
    w=waitbar(0,'Running simulation...');
    while(t<1)
        waitbar(t);
        command=sprintf('net.network.simulator.run(%f,%f,%f)',t,t+dt,dt);
        python.exec(command);
        data=[data C.getOrigin('X').getValues().getValues()];
        t=t+dt;
    end
    close(w);










