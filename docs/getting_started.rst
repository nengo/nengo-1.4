***************
Getting started
***************

.. warning::
   Nengo 1.4 is no longer actively maintained.
   Please use `Nengo 2.0 <https://www.nengo.ai/>`_ instead.

Installing Nengo 1.4
====================

Nengo 1.4 runs on Linux, OS X, and Windows.
The only requirement is that you have `Java <http://java.com>`_
installed on your computer.

To download Nengo, download the ``nengo-latest.zip`` file
from https://github.com/nengo/nengo_1.4/releases.

To install Nengo, unzip the file to a directory of your choosing.

Running the Nengo GUI
=====================

The main way to interact with Nengo is through
its graphical user interface (GUI).

* To run the Nengo GUI in Windows, double-click on ``nengo.bat``.

* To run Nengo in OS X and Linux, run ``./nengo``.

Nengo scripting
===============

As you build larger models,
you will find that models can be crated faster
by writing scripts rather than using the GUI.
The :doc:`Nengo demos <demos/index>`
are also provided as scripts.

The scripting language used in Nengo is Python.
Detailed documentation on Python syntax
can be found at `here <https://docs.python.org/2/reference/>`_.
Nengo uses `Jython <http://www.jython.org/>`_
to interface between the script
and Nengo's underlying Java code.

The script console
------------------

The GUI contains a console in which you can run Nengo scripts.
Click on the |console| icon
to toggle the script console
(or press ``Ctrl-P`` to jump to it).
You can now type script commands
that will be immediately run.
For example, you can type this at the console::

    print "Hello from Nengo"

When using the script console,
you can refer to the currently selected object
using the word ``that``.
For example, if you click on a component of your model
(it should be highlighted in yellow to indicate it is selected),
you can get its name by typing::

    print that.name

You can also change aspects of the model this way.
For example, click on an ensemble of neurons
and change the number of neurons in that ensemble by typing::

    that.neurons = 75

Pressing the up and down arrows will scroll through
the history of your console commands.

Script files
------------

Retyping your model through the script console
would be time consuming and error prone.
You can instead create script files
using your favourite text editor.
They are like any other Python script,
and so when saved, they should have a ``.py`` extension.

You can run a script file
from the script console with this command
(replace ``script_name`` with the name of the script to run)::

    run script_name.py

You can also run a script within the GUI
by clicking on the |open| icon,
or select "File -> Open from file" from the main menu.

Importing other libraries
-------------------------

The scripting system allows you
to make use of any Java library
and any 100% Python library.
There are two methods for doing this in Python.

First, you can do it this way::

    import ca.nengo.utils

but when you use the library, you will
have to provide the fullly qualified name::

    y = ca.nengo.utils.DataUtils.filter(x, 0.005)

The other option is to import this way::

    from ca.nengo.utils import DataUtils

This allows you to do::

    y = DataUtils.filter(x, 0.005)

Running Nengo from the command line
===================================

You can run Nengo scripts from the command line,
bypassing the GUI entirely.
This can be convenient for running Nengo scripts
many times to collect data,
or to run Nengo scripts through a remote terminal.

To do this, instead of running nengo with ``nengo``
(or ``nengo.bat`` on Windows), do

.. code:: bash

   nengo-cl script_name.py

Since this bypasses the Nengo graphical interface,
you won't be able to click on the |interactive| icon
to show the model and see plots.
You can open the Interactive Plots from a script manually
by adding the following to the end of your model::

    net.view()

where ``net`` is an ``nef.Network`` instance.

.. |open| image:: ../simulator-ui/python/images/open.png
   :scale: 75%

.. |console| image:: ../simulator-ui/python/images/console.png
   :scale: 75%

.. |interactive| image:: ../simulator-ui/python/images/interactive.png
   :scale: 75%
