import nef

net=nef.Network('Addition') #Create the network object
net.make_input('inputA',[0])  #Create a controllable input function 
                                     #with a starting value of 0
net.make_input('inputB',[0])  #Create another controllable input 
                                     #function with a starting value of 0
net.make('A',100,1) #Make a population with 100 neurons, 
                                 #1 dimensions
net.make('B',100,1)  #Make a population with 100 neurons, 1 dimensions 
                      #(storage codes work with 'quick' to load already made
                      #populations if they exist
net.make('C',100,1) #Make a population with 
                    #100 neurons, 1 dimensions
net.connect('inputA','A') #Connect all the relevant objects
net.connect('inputB','B')
net.connect('A','C')
net.connect('B','C')
net.add_to_nengo()


