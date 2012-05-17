"""Commands to run when starting nengo-cl (the command-line version)"""

execfile('python/startup_common.py')

import sys
sys.argv=sys.argv[1:]   # strip out the "python/startup_cl.py" from the list
if len(sys.argv)>0:
    # run the script indicated on the command line
    execfile(sys.argv[0])
else:
    # start an interactive console
    sys.argv=['-i']
    import org.python.util
    interp=org.python.util.JLineConsole(locals())
    interp.interact()
    