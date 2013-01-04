import nef

net=nef.Network('2D Representation')  # Create the network

net.make_input('input',[0,0])         # Create a controllable 2-D input
                                      # with a starting value of (0,0)
                                      
net.make('neurons',100,2)             # Create a population with 100 neurons
                                      # representing 2 dimensions
                                      
net.connect('input','neurons')        # Connect the input to the neurons
net.add_to_nengo()


