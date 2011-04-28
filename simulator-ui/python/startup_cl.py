"""A series of Python commands to execute when starting up
Nengo from the command line."""

execfile('python/startup_common.py')

print """Welcome to Nengo!

From this command-line interface you can run Nengo models
with Python syntax. To execute a .py script, you can use:

  execfile('directory/script.py')

Interactive plots are available if you're using the
nef.Network class, with:

  net.view()
"""
