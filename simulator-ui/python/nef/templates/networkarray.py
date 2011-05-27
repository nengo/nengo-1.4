import nef
from ca.nengo.ui.configurable import *
from javax.swing import *
from javax.swing.event import DocumentListener

title='Network Array'
label='Network\nArray'
icon='network.gif'

class SignInputPanel(PropertyInputPanel,DocumentListener):
    def __init__(self,property):
        PropertyInputPanel.__init__(self,property)
        self.comboBox = JComboBox(["Unconstrained", "Positive", "Negative"])
        self.add(self.comboBox)    
        
    def isValueSet(self):
        return True
    def getValue(self):
        item = self.comboBox.getSelectedItem()
        if item=="Positive":
            return 1
        elif item =="Negative":
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
    
params=[
    ('name','Name',str),
    ('neurons','Neurons per dimension',int),
    ('length','Number of dimensions',int),
    ('radius','Radius',float),
    ('iLow','Intercept (low)',float),
    ('iHigh','Intercept (high)',float),
    ('rLow','Max rate (low)',float),
    ('rHigh','Max rate (high)',float),
    ('encSign','Encoding sign', PTemplateSign),
    ('useQuick', 'Quick mode', bool),
    ]

def test_params(net,p):
    try:
       net.network.getNode(p['name'])
       return 'That name is already taken'
    except:
        pass
    if p['iLow'] > p['iHigh']: return 'Low intercept must be less than high intercept'
    if p['rLow'] > p['rHigh']: return 'Low max firing rate must be less than high max firing rate'

def make(net,name='Network Array', neurons=50, length=10, radius=1.0, rLow=200, rHigh=400, iLow=-1, iHigh=1, encSign=0, useQuick=True):
    if int(encSign) is not 0:
        net.make_array(name, neurons, length, max_rate=(rLow,rHigh), intercept=(iLow, iHigh), radius=radius, encoders=[[encSign]], quick=useQuick)
    else:
        net.make_array(name, neurons, length, max_rate=(rLow,rHigh), intercept=(iLow, iHigh), radius=radius, quick=useQuick)
    
