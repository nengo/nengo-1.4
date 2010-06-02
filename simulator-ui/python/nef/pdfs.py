from ca.nengo.math import PDF

class ListPDF(PDF):
    serialVersionUID=1
    def __init__(self,values):
        self.values=values
        self.index=0
    def sample(self):
        s=self.values[self.index]
        self.index=(self.index+1)%len(self.values)
        return [s]
    def clone(self):
        c=ListPDF(self.values)
        c.index=self.index
        return c

