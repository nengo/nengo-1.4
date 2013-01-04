import core

from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import java


class View3D(core.DataViewComponent, ComponentListener, KeyListener):
    def __init__(self, view, name, func, args=()):
        core.DataViewComponent.__init__(self)
        self.addComponentListener(self)
        self.view = view
        self.view.frame.addKeyListener(self)
        self.name = name
        self.func = func
        self.data = self.view.watcher.watch(name, func, args=args)
        self.clearColor = Color(0x666666)
        self.setSize(300, 200)
        self.location = (0, 0, 10)
        self.initialize()

    def initialize(self):
        from com.threed.jpct import FrameBuffer
        self.buffer = FrameBuffer(self.width, self.height, FrameBuffer.SAMPLINGMODE_NORMAL)

    def componentHidden(self, event):
        pass

    def componentMoved(self, event):
        pass

    def componentResized(self, event):
        self.initialize()

    def componentShown(self, event):
        pass

    def keyPressed(self, event):
        #java.lang.System.out.println("key: "%event.keyCode)
        if event.keyCode == KeyEvent.VK_W:
            camera = self.view.watcher.objects[self.name]._simulator.model.world.camera
            camera.moveCamera(camera.getZAxis(), 1)

    def keyReleased(self, event):
        pass

    def keyTyped(self, event):
        pass

    def mouseWheelMoved(self, event):
        delta = event.wheelRotation

        camera = self.view.watcher.objects[self.name]._simulator.model.world.camera
        camera.moveCamera(camera.getZAxis(), delta * 0.5)

    def mouseDragged(self, event):
        if event.isControlDown():
            camera = self.view.watcher.objects[self.name]._simulator.model.world.camera
            dx = event.x - self.mouse_pressed_x
            dy = event.y - self.mouse_pressed_y
            self.mouse_pressed_x = event.x
            self.mouse_pressed_y = event.y
            #java.lang.System.out.println("event %s "%(event))
            if event.modifiersEx & InputEvent.BUTTON3_DOWN_MASK:
                from com.threed.jpct import SimpleVector
                camera.rotateCameraAxis(SimpleVector(-dy, dx, 0), 0.003)
                #camera.align(self.view.watcher.objects[self.name]._simulator.model.room)
                #camera.setOrientation(camera.getDirection(),SimpleVector(0,0,1))
            else:
                camera.moveCamera(camera.getXAxis(), dx * 0.01)
                camera.moveCamera(camera.getYAxis(), -dy * 0.01)

                #camera.moveCamera(SimpleVector(dx,-dy,0),0.01)

        else:
            core.DataViewComponent.mouseDragged(self, event)

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)

        obj = self.view.watcher.objects[self.name]._simulator.model
        self.buffer.clear(self.clearColor)

        physics = self.data.get(start=self.view.current_tick, count=1)[0]
        if physics is not None:
            obj.update_shapes(physics)

            obj.world.renderScene(self.buffer)
            obj.world.draw(self.buffer)
        self.buffer.update()
        self.buffer.display(g)
        self.repaint()
