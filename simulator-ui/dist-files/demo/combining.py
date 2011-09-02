import nef

net=nef.Network('Combining') #Create the network object
inputA=net.make_input('inputA',[0]) #Create a controllable input function 
                                    #with a starting value of 0
inputB=net.make_input('inputB',[0]) #Create another controllable input 
                                    #function with a starting value of 0
A=net.make('A',100,1,quick=True)  #Make a population with 100 neurons, 
                                  #1 dimensions
B=net.make('B',100,1,quick=True,
    storage_code='B') #Make a population with 100 neurons, 1 dimensions 
                      #(storage codes work with 'quick' to load already made
                      #populations if they exist
C=net.make('C',100,2,quick=True,
    radius=1.5) #Make a population with 100 neurons, 2 dimensions, and set a
                #larger radius (so 1,1 input still fits within the circle of
                #that radius)
net.connect(inputA,A) #Connect all the relevant objects (default connection 
                      #is identity)
net.connect(inputB,B)
net.connect(A,C,transform=[1,0]) #Connect with the given 1x2D mapping matrix
net.connect(B,C,transform=[0,1])
net.add_to_nengo()


