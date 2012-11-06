title='Basal Ganglia Rule'
label='BG Rule'
icon='BGRule.png'

params=[
    ('index','Rule Index',int),
    ('pattern','Semantic Pointer',str),
    ('dim','Dimensionality',int),
    ('pstc','Synaptic time constant [s]',float),
    ('use_single_input','Use Single Input',bool),
    ]

def test_params(net,node,p):
    if p['index']>node.getNode('STN').dimension : return 'Rule index cannot exceed basal ganglia dimension minus one (use a zero-based index)'

def test_drop(net,node):
    try:
        STN=node.getNode('STN')
        StrD1=node.getNode('StrD1')
        StrD2=node.getNode('StrD2')
        return True
    except:
        return False

import numeric
import hrr
from java.util import ArrayList
from java.util import HashMap
from ca.nengo.model.impl import NetworkImpl
from ca.nengo.model import Network

def make(net,node,index=0,dim=8,pattern='I',pstc=0.01,use_single_input=False):
    STN=node.getNode('STN')

    transform=numeric.zeros((STN.dimension,dim),'f')

    if dim in hrr.Vocabulary.defaults.keys():
        vocab=hrr.Vocabulary.defaults[dim]
    else:
        vocab=hrr.Vocabulary(dim)

    terms=[t.name for t in node.terminations]
    STNterms=[t.name for t in STN.terminations]

    count=0
    while 'rule_%02d'%count in terms or 'rule_%02d'%count in STNterms:
        count=count+1

    name='rule_%02d'%count

    transform[index,:]=vocab.parse(pattern).v


    if use_single_input:
        input=node.getNode('input')
        input.addDecodedTermination(name,transform,pstc,False)
        node.exposeTermination(input.getTermination(name),name)
    else:
        StrD1=node.getNode('StrD1')
        StrD2=node.getNode('StrD2')

        STN.addDecodedTermination(name,transform,pstc,False)
        node.exposeTermination(STN.getTermination(name),name+'_STN')
        StrD1.addDecodedTermination(name,transform*(0.8),pstc,False)
        node.exposeTermination(StrD1.getTermination(name),name+'_StrD1')
        StrD2.addDecodedTermination(name,transform*(1.2),pstc,False)
        node.exposeTermination(StrD2.getTermination(name),name+'_StrD2')

    if isinstance(node, Network) and node.getMetaData("type") == "BasalGanglia":
        bg = node
        if bg.getMetaData("bgrule") == None:
            bg.setMetaData("bgrule", ArrayList())

        bgrules = bg.getMetaData("bgrule")

        rule=HashMap(5)
        rule.put("index", index)
        rule.put("dim", dim)
        rule.put("pattern", pattern)
        rule.put("pstc", pstc)
        rule.put("use_single_input", use_single_input)

        bgrules.add(rule)

