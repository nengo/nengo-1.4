import ca.nengo.ui.audio


class Clicker:
    def __init__(self, component, data):
        self.component = component
        self.data = data
        self.component.view.tick_queue.append(self.tick)
        self.selected = None
        self.click = ca.nengo.ui.audio.Clicker()
        self.click.start()

    def select(self, index):
        self.selected = index

    def tick(self, time):
        if not self.component.visible:
            self.component.view.tick_queue.remove(self.tick)
        if self.selected is None:
            return

        d = self.data.get(start=self.component.view.current_tick, count=1, dt_tau=None)[0]

        if self.selected < 0 or self.selected >= len(d):
            return

        value = d[self.selected]

        value = int(value * 255)

        self.click.set(value)


#this is here to provide the functionality of a "global" variable while keeping the global namespace clean
class ClickerEnabled:
    enabled = False

"""
from javax.sound.sampled import AudioSystem, AudioFormat, LineListener, LineEvent
from java.io import File
import jarray

class Clicker(LineListener):
    def __init__(self,component,data):
        self.component=component
        self.data=data
        self.component.view.tick_queue.append(self.tick)
        self.selected=None

        self.initialized=False



        #audioInput=AudioSystem.getAudioInputStream(File('click2.wav'))
        #self.clip=AudioSystem.getClip()
        #self.clip.open(audioInput)

    def initialize(self):
        self.format=AudioFormat(8000,16,2,True,True)
        self.clipdata=[0]*800000
        self.clipdata[0]=127
        self.clip=None
        self.clip=AudioSystem.getClip()
        #self.clip.addLineListener(self)
        self.clip.open(self.format,jarray.array(self.clipdata,'b'),0,800000)
        print 'format',self.clip.format,self.clip.bufferSize,self.clip.getMicrosecondLength()
        self.initialized=True


    def select(self,index):
        self.selected=index

    def tick(self,time):
        if not self.component.visible:
            self.component.view.tick_queue.remove(self.tick)
        if self.selected is None: return
        if not self.initialized: self.initialize()

        d=self.data.get(start=self.component.view.current_tick,count=1,dt_tau=None)[0]

        if self.selected<0 or self.selected>=len(d): return

        value=d[self.selected]

        if value>0:
            #if self.clip.isActive():
                #print 'running',self.clip.framePosition
                #self.clip.drain()
                #self.clip.stop()
            #self.clip=AudioSystem.getClip()
            #self.clip.addLineListener(self)
            #self.clip.open(self.format,jarray.array(self.clipdata,'b'),0,80000)

            self.clip.stop()
            self.clip.setFramePosition(0)
            self.clip.setMicrosecondPosition(0)
            if not self.clip.isRunning():
                self.clip.start()
            print '...',self.clip.getFramePosition()
            #self.clip.start()

    def update(self,event):
        if event.type==LineEvent.Type.STOP:
            print event.line
            event.line.close()
            print '...closed'

    """
