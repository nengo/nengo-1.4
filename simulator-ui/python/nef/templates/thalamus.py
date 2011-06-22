import nef

title='Thalamus'
label='Thalamus'
icon='thalamus.png'
    
params=[
    ('name','Name',str),
    ('neurons','Neurons per dimension',int),
    ('D','Dimensions',int),
    ('useQuick', 'Quick mode', bool),
    ]

def test_params(net,p):
    try:
       net.network.getNode(p['name'])
       return 'That name is already taken'
    except:
        pass

def make(net,name='Network Array', neurons=50, D=2, useQuick=True):
    thal = net.make_array(name, neurons, D, max_rate=(100,300), intercept=(-1, 0), radius=1, encoders=[[1]], quick=useQuick)    
    def addOne(x):
        return [x[0]+1]            
    net.connect(thal, None, func=addOne, origin_name='xBiased')
