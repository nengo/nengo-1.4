import numpy

class Theme:
    def __init__(self):
        self.bar_colors=['0.4','0.8','0.6','1.0']
    def bar_color(self,i):
        return self.bar_colors[i%len(self.bar_colors)]

class ThemePhi:
    def __init__(self,color=True):
        self.phi=(1+numpy.sqrt(5))/2-1
        self.phi2=(1+numpy.sqrt(7))/2-1
        self.start_y=0.5
        self.start_theta=1.7
        self.color=color
        #self.test_plot()
    def theta_to_rb(self,theta):
            b=numpy.sin(theta)
            r=numpy.cos(theta)
            # move to extremes of color space
            if abs(r)>abs(b):
                if r>0:
                    b=b/r
                    r=1.0
                else:
                    b=b/-r
                    r=-1.0
            else:        
                if b>0:
                    r=r/b
                    b=1.0
                else:
                    b=r/-b
                    b=-1.0
            return r,b        
    
    def bar_color(self,i):
        return self.make_color(i,max_y=0.9,min_y=0.0)        
    def line_color(self,i):
        return self.make_color(i,max_y=0.8,min_y=0.1)        
        
    def make_color(self,i,max_y=1.0,min_y=0):
        y=((self.phi*i+self.start_y)%1)*(max_y-min_y)+min_y
        if self.color:
            theta=self.phi*i*2.8+self.start_theta
            r,b=self.theta_to_rb(theta)
        else:
            r,b=0,0    
        return self.ybr_to_color(y,b*0.5+0.5,r*0.5+0.5)
    
    def ybr_to_color(self,y,cb,cr):
        r=y+1.402*(cr-0.5)
        g=y-0.34414*(cb-0.5)-0.71414*(cr-0.5)
        b=y+1.772*(cb-0.5)
        r=int(r*255)
        g=int(g*255)
        b=int(b*255)
        if r<0: r=0
        if g<0: g=0
        if b<0: b=0
        if r>255: r=255
        if g>255: g=255
        if b>255: b=255
        return '#%02x%02x%02x'%(r,g,b)
    def test_plot(self):
        data=[]
        for i in range(20):
            y=((self.phi*i+self.start_y)%1)*(self.max_y-self.min_y)+self.min_y
            theta=(self.phi*i)+self.start_theta
            theta=theta%(2*numpy.pi)
            data.append((y,theta))
        import pylab
        pylab.scatter(numpy.array(data)[:,0],numpy.array(data)[:,1])
        pylab.show()            

