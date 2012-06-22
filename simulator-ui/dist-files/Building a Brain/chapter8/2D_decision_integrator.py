import nef

net=nef.Network('2D Decision Integrator') 
input=net.make_input('input',[0,0])  

MT=net.make('MT',100,2, noise=10) 
LIP=net.make('LIP',200,2, noise=10) 
LIPT=net.make('LIP Threshold',100,2,intercept=(.3,1),noise=10)

net.connect(input,MT,weight=1,pstc=0.1)  
net.connect(MT,LIP,weight=.1, pstc=.01) 
net.connect(LIP,LIP,pstc=0.1)  
net.connect(LIP,LIPT,pstc=.01)

net.add_to_nengo()