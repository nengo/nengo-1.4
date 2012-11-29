import nef
from ca.nengo.ui.configurable import *
from javax.swing import *
from javax.swing.event import DocumentListener

title = 'Network Array'
label = 'Network\nArray'
icon = 'array.png'


class SignInputPanel(PropertyInputPanel, DocumentListener):
    def __init__(self, property):
        PropertyInputPanel.__init__(self, property)
        self.comboBox = JComboBox(["Unconstrained", "Positive", "Negative"])
        self.add(self.comboBox)

    def isValueSet(self):
        return True

    def getValue(self):
        item = self.comboBox.getSelectedItem()
        if item == "Positive":
            return 1
        elif item == "Negative":
            return -1
        else:
            return 0

    def setValue(self, value):
        pass


class PTemplateSign(Property):
    def createInputPanel(self):
        return SignInputPanel(self)

    def getTypeName(self):
        return "Encoder Sign"

    def getTypeClass(self):
        return PInt

description = ("<html>This template enables constructing subnetworks full D "
               "(# of dimensions) independent populations of neurons. "
               "These are faster to construct but cannot compute all the "
               "same nonlinear functions as a single large population with "
               "D dimensions.</html>")

params = {
    'name': PString(
        'Name',
        'Name of the Network Array',
        'Network Array',
    ),
    'neurons': PInt(
        'Neurons per dimension',
        'Number of neurons in each of the ensembles',
        50,
        1,
        sys.maxint,
    ),
    'length': PInt(
        'Number of dimensions',
        'Number of ensembles in the array',
        10,
        1,
        sys.maxint,
    ),
    'radius': PFloat(
        'Radius',
        'Maximum magnitude of vector that can be represented in each ensemble',
        1.0,
        0,
        float("inf"),
    ),
    'iLow': PFloat(
        'Intercept (low)',
        'Smallest value for neurons to start firing at (between -1 and 1)',
        -1,
        -1,
        1,
    ),
    'iHigh': PFloat(
        'Intercept (high)',
        'Largest value for neurons to start firing at (between -1 and 1)',
        1,
        -1,
        1,
    ),
    'rLow': PFloat(
        'Max rate (low) [Hz]',
        'Smallest maximum firing rate for neurons in the ensemble',
        200,
        0,
        sys.maxint,
    ),
    'rHigh': PFloat(
        'Max rate (high) [Hz]',
        'Largest maximum firing rate for neurons in the ensemble',
        400,
        0,
        sys.maxint,
    ),
    'encSign': PTemplateSign(
        'Encoding sign',
        'Limits the sign of the encoders',
        0,
    ),
    'useQuick': PBoolean(
        'Quick mode',
        'Uses the exact same encoders and decoders for each ensemble in the array',
        True,
    ),
}


def test_params(net, p):
    try:
        net.network.getNode(p['name'])
        return 'That name is already taken'
    except:
        pass
    if p['iLow'] > p['iHigh']:
        return 'Low intercept must be less than high intercept'
    if p['rLow'] > p['rHigh']:
        return 'Low max firing rate must be less than high max firing rate'

from ca.nengo.model.impl import NetworkImpl
from java.util import ArrayList
from java.util import HashMap


def make(net, name='Network Array', neurons=50, length=10, radius=1.0, rLow=200, rHigh=400, iLow=-1, iHigh=1, encSign=0, useQuick=True):
    if encSign != 0:
        ensemble = net.make_array(name, neurons, length, max_rate=(rLow, rHigh), intercept=(iLow, iHigh), radius=radius, encoders=[[encSign]], quick=useQuick)
    else:
        ensemble = net.make_array(name, neurons, length, max_rate=(rLow,rHigh), intercept=(iLow, iHigh), radius=radius, quick=useQuick)
