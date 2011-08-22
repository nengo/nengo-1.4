Scripting Interface
====================

Many researchers prefer to create models by writing scripts rather than using a graphical user interface.  To accomplish this, we have embedded a
scripting system within Nengo.

The scripting language used in Nengo is Python.  Detailed documentation on Python syntax can be found at http://www.python.org/.  Nengo uses Jython (http://jython.org)
to interface between the script and the underlying Java implementation of Nengo.  This allows us to use Python syntax and still have access to all of the underlying Java
code.

Running scripts from a file
----------------------------

You can create scripts using your favourite text editor.  When saved, they should have a ``.py`` extension.  To run the script, click on the |open| icon, or select ``File->Open from file`` from the 
main menu.

.. |open| image:: ../python/images/open.png
    :scale: 75 %

Running scripts from the console
---------------------------------

You can also use scripting to interact with a model within Nengo.  Click on the |console| icon to toggle the script console (or press ``Ctrl-P`` to jump to it).  You can now type script
commands that will be immediately run.  For example, you can type this at the console::

    print "Hello from Nengo"

.. |console| image:: ../python/images/console.png
    :scale: 75 %
    
When using the script console, you can refer to the currently selected object using the word ``that``.  For example, if you click on a component of your model (it should be highlighted in yellow
to indicate it is selected), you can get its name by typing::

    print that.name
    
You can also change aspects of then model this way.  For example, click on an ensemble of neurons and change the number of neurons in that ensemble by typing::

    that.neurons=75
    
You can also run a script file from the console with this command, with ``script_name`` replaced by the name of the script to run::

    run script_name.py    

Pressing the up and down arrows will scroll through the history of your console commands.


Running scripts from the command line
---------------------------------------

You can also run scripts from the command line.  This allows you to simply run the model without running Nengo itself.  To do this, instead of running nengo with ``nengo`` 
(or ``nengo.bat`` on Windows), do::

    nengo-cl script_name.py
    
Of course, since this bypasses the Nengo graphical interface, you won't be able to click on the |interactive| icon to show the model.  Instead, you should add this to the
end of your model::

    net.view()

.. |interactive| image:: ../python/images/interactive.png
    :scale: 75 %


Importing other libraries
----------------------------

The scripting system allows you to make use of any Java library and any 100% Python library.  There are two methods for doing this in Python.

First, you can do it this way::

    import ca.nengo.utils

but then when you use the library, you will have to provide its full name::

    y=ca.nengo.utils.DataUtils.filter(x,0.005)

The other option is to import this way::

    from ca.nengo.utils import *

This allows you to just do::

    y=DataUtils.filter(x,0.005)
    


