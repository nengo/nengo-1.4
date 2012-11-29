title = 'Integrator'
label = 'Integrator'
icon = 'integrator.png'

description = ("<html>This constructs an integrator of the specified number "
               "of dimensions. It requires an input of that number of "
               "dimensions after construction.</html>")

params = {
    'name': PString(
        'Name',
        'Name of the integrator',
        'My Integrator',
    ),
    'neurons': PInt(
        'Number of neurons',
        'Number of neurons in the integrator',
        100,
        1,
        sys.maxint,
    ),
    'dimensions': PInt(
        'Number of dimensions',
        'Number of dimensions for the integrator',
        1,
        1,
        sys.maxint,
    ),
    'tau_feedback': PFloat(
        'Feedback PSTC [s]',
        ('Post-synaptic time constant of the integrative feedback, in seconds '
         '(longer -> slower change but better value retention)'),
        0.1,
        1e-40,
        float("inf"),
    ),
    'tau_input': PFloat(
        'Input PSTC [s]',
        ('Post-synaptic time constant of the integrator input, in seconds '
         '(longer -> more input filtering)'),
        0.01,
        1e-40,
        float("inf"),
    ),
    'scale': PFloat(
        'Scaling factor',
        'A scaling value for the input (controls the rate of integration)',
        1,
    ),
}

def test_params(net, p):
    try:
        net.network.getNode(p['name'])
        return 'That name is already taken'
    except:
        pass

import numeric
from java.util import ArrayList
from java.util import HashMap


def make(net, name='Integrator', neurons=100, dimensions=1, tau_feedback=0.1, tau_input=0.01, scale=1):
    if (dimensions < 8):
        integrator = net.make(name, neurons, dimensions)
    else:
        integrator = net.make_array(name, int(neurons / dimensions), dimensions, quick=True)
    net.connect(integrator, integrator, pstc=tau_feedback)
    integrator.addDecodedTermination('input', numeric.eye(dimensions) * tau_feedback * scale, tau_input, False)
    if net.network.getMetaData("integrator") == None:
        net.network.setMetaData("integrator", HashMap())
    integrators = net.network.getMetaData("integrator")

    integrator = HashMap(6)
    integrator.put("name", name)
    integrator.put("neurons", neurons)
    integrator.put("dimensions", dimensions)
    integrator.put("tau_feedback", tau_feedback)
    integrator.put("tau_input", tau_input)
    integrator.put("scale", scale)

    integrators.put(name, integrator)

    if net.network.getMetaData("templates") == None:
        net.network.setMetaData("templates", ArrayList())
    templates = net.network.getMetaData("templates")
    templates.add(name)

    if net.network.getMetaData("templateProjections") == None:
        net.network.setMetaData("templateProjections", HashMap())
    templateproj = net.network.getMetaData("templateProjections")
    templateproj.put(name, name)
