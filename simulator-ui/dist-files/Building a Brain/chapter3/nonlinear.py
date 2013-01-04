import nef

net=nef.Network('Nonlinear Functions (pre-built)')

net.make_input('inputX',[0])
net.make('X',100,1)
net.connect('inputX','X')

net.make('result (squaring)',100,1)
def square(x):
    return x[0]*x[0]
net.connect('X','result (squaring)',func=square)

net.make_input('inputY',[0])
net.make('Y',100,1)
net.connect('inputY', 'Y')
net.make('2D vector', 100, 2)
net.connect('X','2D vector', transform=[1, 0])
net.connect('Y','2D vector', transform=[0, 1])

net.make('result (multiplication)',100,1)
def product(x):
    return x[0]*x[1]
net.connect('2D vector', 'result (multiplication)', func=product)

net.add_to_nengo()


