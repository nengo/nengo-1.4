"""
Decorators for running nengo code fragments from within CPython.
"""

import inspect
import stats.reader
import subprocess
import tempfile

jython_template = """
# -- Automatically-generated Jython file to run nef code fragment for unit
#    tests
import sys

# -- no-op decorator in auto-gen jython file
import logging
logging.basicConfig(stream=sys.stderr)

def nengo_log(f):
    return f

%(fn_def)s

net = %(fn_name)s(logfile="%(logfile)s", **%(fn_kwargs)s)
net.run(time=%(t)s, dt=%(dt)s)

"""

# -- N.B. For this to work you have to run the 'rebuild_nengo_current.sh' script
#         that is in the root folder of the git repo.

import os
_mypath = os.path.abspath(os.path.split(__file__)[0])
nengo_cl = os.path.abspath(
    os.path.join(
        _mypath, '..', '..', '..', 'nengo-current', 'nengo-cl'))
print 'nengo_cl', nengo_cl

keep_tempfiles = True

def nengo_log(f):

    def deco(t, dt=0.001, **kwargs):
        pyfile = tempfile.NamedTemporaryFile(
            prefix='nengo_log',
            suffix='.py',
            delete=not keep_tempfiles)
        logfile = tempfile.NamedTemporaryFile(
            prefix='nengo_log',
            suffix='.csv',
            delete=not keep_tempfiles)

        #print f
        fn_def = inspect.getsource(f)
        assert fn_def.startswith('@nengo_log')
        jython_src = jython_template % dict(
            t=t,
            dt=dt,
            fn_def=fn_def,
            fn_name=f.__name__,
            fn_kwargs=repr(kwargs), # XXX only works for simple stuff
            logfile=logfile.name,
            )

        #print jython_src
        pyfile.write(jython_src)
        #pyfile.flush()
        pyfile.close()

        # print os.environ
        # print ' '.join([nengo_cl, pyfile.name])
        proc = subprocess.Popen(
            [nengo_cl, pyfile.name],
            #['ls', '-l'],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            )
        proc.wait()
        if proc.returncode:
            comm = proc.communicate()
            print 'Subprocess failed'
            print comm[0]
            print comm[1]
            raise Exception('Failed to evaluate %s %s'
                            % (nengo_cl, pyfile.name))

        print 'nengo_log pyfile', pyfile.name
        print 'nengo_log logfile', logfile.name
        rval = stats.reader.Reader(logfile.name, dir='/', search=False)
        return rval

    deco.__name__ = f.__name__
    return deco

