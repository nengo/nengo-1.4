import nef

net=nef.Network('Nonlinear Functions')

inputX=net.make_input('input X',[0])
X=net.make('X',100,1)
net.connect(inputX,X)

result=net.make('result (squaring)',100,1)
def square(x):
    return x[0]*x[0]
net.connect(X,result,func=square)

inputY=net.make_input('input Y',[0])
Y=net.make('Y',100,1)
net.connect(inputY, Y)
vector=net.make('2D Vector', 100, 2)
net.connect(X, vector, transform=[1, 0])
net.connect(Y, vector, transform=[0, 1])

result2=net.make('result (multiplication)',100,1)
def product(x):
    return x[0]*x[1]
net.connect(vector, result2, func=product)

net.add_to(world)


