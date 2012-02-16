import ca.nengo.ui.audio

enabled=False


class Clicker:
    def __init__(self,component,data):
        self.component=component
        self.data=data
        self.component.view.tick_queue.append(self.tick)
        self.selected=None
        self.click=ca.nengo.ui.audio.Clicker()
        self.click.start()
        
    def select(self,index):
        self.selected=index    
    
    def tick(self,time):
        if not self.component.visible:
            self.component.view.tick_queue.remove(self.tick)
        if self.selected is None: return
        
        d=self.data.get(start=self.component.view.current_tick,count=1,dt_tau=None)[0]
        
        if self.selected<0 or self.selected>=len(d): return
        
        value=d[self.selected]
        
        value=int(value*255)
        
        self.click.set(value)
        
        
        
    
    
