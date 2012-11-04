import inspect
import tempfile

jython_template = """
# -- Automatically-generated Jython file to run nef code fragment for unit
#    tests

# -- no-op decorator in auto-gen jython file
import logging
logging.basicConfig(stream=sys.stderr)

def nengo_log(f):
    return f

%(fn_def)s

net = %(fn_name)s(**%(fn_kwargs)s)
log = net.log(filename="%(logfile)s")
net.run(time=%(t)s, dt=%(dt)s)

"""

nengo_cl = '../../nengo-70ea992/nengo-cl'

keep_tempfiles = True

def nengo_log(f):

    def deco(t, dt=0.001, **kwargs):
        print f
        fn_def = inspect.getsource(f)
        assert fn_def.startswith('@nengo_log')
        fn_name = f.__name__
        fn_kwargs = repr(kwargs) # XXX
        logfile = 'logfile'
        jython_src = jython_template % locals()

        print jython_src

        pyfile = tempfile.NamedTemporaryFile(
            prefix='nengo_log',
            suffix='.py',
            delete=not keep_tempfiles)
        logfile = tempfile.NamedTemporaryFile(
            prefix='nengo_log',
            suffix='.csv',
            delete=not keep_tempfiles)

        print jython_src
        pyfile.write(jython_src)

        cmd = ['bash', nengo_cl, pyfile.name]
        print cmd
        return
        subprocess.check_call()

        print 'pyfile', pyfile.name
        print 'logfile', logfile.name

        return None

    deco.__name__ = f.__name__
    return deco


@nengo_log
def connect_something():
    import nef
    net = nef.Network('top')
    netA = nef.Network('A')
    netB = nef.Network('B')
    netA.make('X', 100, 1)
    netA.make('Y', 100, 1)
    netB.make('X', 100, 1)
    netB.make('Y', 100, 1)
    net.add(netA.network)
    net.add(netB.network)
    net.connect('A.X', 'B.Y')
    return net


def test_0():
    stats0 = connect_something(t=1.0)
    stats1 = connect_something(t=1.0)
    assert stats0 == stats1


