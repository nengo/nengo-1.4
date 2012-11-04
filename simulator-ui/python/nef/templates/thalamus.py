import nef

title='Thalamus'
label='Thalamus'
icon='thalamus.png'
    
params=[
    ('name','Name',str,'Name of the new thalamus sub-network'),
    ('neurons','Neurons per dimension',int,'Number of neurons for each dimension of the thalamus'),
    ('dimensions','Dimensions',int,'Number of dimensions for the thalamus'),
    ('useQuick', 'Quick mode', bool,'Whether to use quick mode'),
    ]

def test_params(net,p):
    try:
       net.network.getNode(p['name'])
       return 'That name is already taken'
    except:
        pass

def make(net,name='Network Array', neurons=50, dimensions=2, inhib_scale=3, tau_inhib=.005, useQuick=True):
    thal = net.make_array(name, neurons, dimensions, max_rate=(100,300), intercept=(-1, 0), radius=1, encoders=[[1]], quick=useQuick)    

    # setup inhibitory scaling matrix
    inhib_scaling_matrix = [[0]*dimensions for i in range(dimensions)]
    for i in range(dimensions):
        inhib_scaling_matrix[i][i] = -inhib_scale
    # setup inhibitory matrix
    inhib_matrix = []
    for i in range(dimensions):
        inhib_matrix_part = [[inhib_scaling_matrix[i]] * neurons]
        inhib_matrix.append(inhib_matrix_part[0])

    thal.addTermination('bg_input', inhib_matrix, tau_inhib, False)

    def addOne(x):
        return [x[0]+1]            
    net.connect(thal, None, func=addOne, origin_name='xBiased', create_projection=False)

    return thal
