Connection Weights 
=====================

Viewing synaptic weights
---------------------------
For most Nengo models, we do not need to worry about the exact connection weights, as Nengo solves for the 
optimal connection weights using the Neural Engineering Framework.  However, we sometimes would like to
have direct access to this weight matrix, so we can modify it in various ways.  We do this using the 
*weight_func* argument in the :func:`nef.Network.connect()` function.  For example, to simply print the
solved-for optimal weights, we can do the following::

  net.make('A',100,1)
  net.make('B',100,1)
  
  def print_weights(w):
      print w
      return w
  
  net.connect('A','B',weight_func=print_weights)
  
Adjusting weights
------------------
We can also adjust these weights by modifying them and returning the new matrix.  The following code randomly
adjusts each connection weight by an amount sampled from a normal distribution of standard deviation 0.001::
          
    net.make('A',100,1)
    net.make('B',100,1)

    def randomize(w):
        for i in range(len(w)):
            for j in range(len(w[i])):
                w[i][j]+=random.gauss(0,0.001)
        return w

    net.connect('A','B',weight_func=randomize)

  
Sparsification
---------------
We can also use this approach to enforce sparsity constraints on our networks.  For example, one could simply
find all the small weights in the *w* matrix and set them to zero.  The following code takes a slightly 
different approach that we have found to be a bit more robust.  Here, if we want 20% connectivity (i.e. each 
neuron in population A is only connected to 20% of the neurons in population B), we simply randomly select
80% of the weights in the matrix and set them to zero.  To make up for this reduction in connectivity, we
also increase the remaining weights by scaling them by 1.0/0.2::

    net.make('A',100,1)
    net.make('B',100,1)

    p=0.2
    def sparsify(w):
        for i in range(len(w)):
            for j in range(len(w[i])):
                if random.random()<p:
                    w[i][j]/=p
                else:
                    w[i][j]=0.0
        return w            

    net.connect('A','B',weight_func=sparsify)
  

