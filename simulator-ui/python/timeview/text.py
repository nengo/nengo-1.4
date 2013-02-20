import nef

class TextNode(nef.SimpleNode):
    def __init__(self,name,text):
        nef.SimpleNode.__init__(self,name)
        self.text=text
    def current_text(self):
        if callable(self.text): return self.text(self.t)
        else: return self.text



