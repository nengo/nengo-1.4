Integrating with IPython Notebook
==================================

We are currently working on experimental support for `IPython <http://ipython.org/>`_'s new Notebook system.  The iPython Notebook
gives a browser-based interface to Python that is similar to the one in Mathematica.

However, to make use of IPython's graphing features, it must be run from Python, not Jython (which is what Nengo uses).  To 
address this, we can make slight modifications to IPython to tell it to run Nengo scripts using Nengo, but everything else
in normal Python.

Installing IPython
---------------------------

- Download and install IPython using the instructions at http://ipython.org/download.html
- You should now be able to activate the notebook system by running ``ipython notebook --pylab``.  (The ``--pylab`` argument is optional,
  but initializes the pylab graphic system so it is recommended).  A browser window will appear with the notebook interface.
  
Configuring IPython Notebook
-------------------------------------

- We need to customize IPython a bit to tell it about Nengo. Create a configuration profile with the command ``ipython profile create``.
- Open the newly-created file ``profile_default/ipython_config.py``.  It should be off your new IPython directory.  On Linux, this is
  usually ``~/.config/ipython``.  For other operating systems, see http://ipython.org/ipython-doc/stable/config/overview.html#configuration-file-location
- Add the following lines to the bottom of the file::

    import sys
    sys.path.append('python')

  This will give IPython access to Nengo's python directory.  This will let you import things from that directory; most importantly, the
  ``stats`` module will be useful for running Nengo models and extracting data from the logs files.

Telling IPython to use Nengo
-------------------------------------

- We now need to make a small change to the IPython interface.  We do this by editting one of the javascript files it uses to create its interface.
- First, find your python install's site-packages directory.  This varies by computer, but you can get a list by doing::

    import sys
    for f in sys.path:
        if f.endswith('packages'): print f

- Find the IPython directory ``IPython/frontend/html/notebook/static/js``
- Edit ``notebook.js``.  Find the line containing ``Notebook.prototype.execute_selected_cell``.  About 10 lines below that should be::

            var code = cell.get_code();
            var msg_id = that.kernel.execute(cell.get_code());

  Change it to::
  
            var code = cell.get_code();
            if (code.indexOf("nef.Network")!=-1) {
                code='from stats.ipython import run_in_nengo\nrun_in_nengo("""'+code+'""")';
            }
            var msg_id = that.kernel.execute(code);
  
Running a Nengo model from IPython notebook
---------------------------------------------

- Go to your Nengo directory.  You must run IPython from the same directory that Nengo is in!
- Run the IPython interface with ``ipython notebook --pylab``
- Create a New Notebook
- Enter the following code to make a simple model::

    import nef
    net=nef.Network('Test')
    net.make('A',50,1)
    net.make_input('input',{0: -1, 0.2: -0.5, 0.4:0, 0.6:0.5, 0.8:1.0})
    net.connect('input','A')
    log=net.log()
    log.add('input',tau=0)
    log.add('A')
    net.run(1.0)
    
- Run the model by pressing Ctrl-Enter.  It should think for a bit and then output ``"finished running experiment-Test.py"``.  It creates this name
  based on the name of the Network being run.
- As with running a model in Nengo, the logfile should be created as ``Test-<date>-<time>.csv`` inside your Nengo directory.

Analyzing data from a Nengo run
---------------------------------

- As with Nengo, you can use the ``stats`` module to extract data from a log file.  Create a new cell below your current one in the Notebook and 
  enter the following::
  
    import stats
    data=stats.Reader()
    plot(data.time,data.input)
    plot(data.time,data.A)  
  
Running a model with different parameter settings
--------------------------------------------------

- First we need to identify the parameters we might want to vary in the Nengo model.  Do this by defining them as variables at the top of the
  code.  For example::
  
    # here are my parameters
    N=50

    import nef
    net=nef.Network('Test')
    net.make('A',N,1)
    net.make_input('input',{0: -1, 0.2: -0.5, 0.4:0, 0.6:0.5, 0.8:1.0})
    net.connect('input','A')
    log=net.log()
    log.add('input',tau=0)
    log.add('A')
    net.run(1.0)  
  
  The parameters should be defined before any import statements, but can be after any comments at the top of the file.
- Re-run the model (Ctrl-Enter).
- Make a new cell with the following code::

    import stats
    stats.run('experiment-Test',3,N=[5,10,20,50])

  The model will be run 3 times at each parameter setting (N=5, N=10, N=20, and N=50), for a total of 12 runs.  Each parameter combination
  has its own directory inside the ``experiment-Test`` directory.  
  
Plotting data from varying parameter settings
----------------------------------------------

- We use the ``stats.Stats`` class to access the data from simulation runs.  For example, to get the data from the runs where N=5, we can do::

    import stats
    s=stats.Stats('experiment-Test')

    data=s.data(N=5)

    for i in range(len(data)):
        plot(data.time[:,i],data.A[:,i])
    
- We can use a similar approach to plot the average activity for varying values of N::

    import stats
    s=stats.Stats('experiment-Test')

    for N in [5,10,20,50]:
        data=s.data(N=N)
       
        plot(data.time[:,0],numpy.mean(data.A,axis=1),label='%d'%N) 
    legend(loc='best')     
  
Computing summary data
-----------------------

- We often want to look at data that's more high-level than the raw time-varying output.  For example, we might want to
  determine the representation accuracy of the model.  We can do this by writing a function that does our analysis.  It
  should expect 2 inputs: a Reader object and a dictionary holding any other computed results.
  
- This particular example computes the mean-squared error between the input and A values within 5 different 100ms time windows::
  
    import stats
    s=stats.Stats('experiment-Test')

    def error(data,computed):
        errors=[]
        for t in [0.1,0.3,0.5,0.7,0.9]:
            A=data.get('A',(t,t+0.1))
            ideal=data.get('input',(t,t+0.1))
            errors.append(sqrt(mean((ideal-A)**2)))
        return mean(errors)    
            
    s.compute(error=error)      
    
- We can now see how the error changes as N varies by doing::

    import stats
    s=stats.Stats('experiment-Test')

    N=[5,10,20,50]
    plot(N,[mean(s.computed(N=n).error) for n in N])    
    
    
