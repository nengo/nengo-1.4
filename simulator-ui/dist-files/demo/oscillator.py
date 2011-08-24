import nef

net=nef.Network('Oscillator')
input=net.make_input('input',[1,0],zero_after_time=0.1)
A=net.make('A',200,2)
net.connect(input,A,weight=1,pstc=0.1)
net.connect(A,A,[[1,1],[-1,1]],pstc=0.1)
net.add_to_nengo()
