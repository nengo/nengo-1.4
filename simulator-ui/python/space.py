from __future__ import generators

import sys
for p in ['python/jar/jpct.jar','python/jar/jbullet.jar','python/jar/vecmath.jar']:
    if p not in sys.path: 
        sys.path.append(p)

import java
from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
from com.threed.jpct import *
from com.threed.jpct.util import *

from com.bulletphysics.collision.broadphase import *
from com.bulletphysics.collision.dispatch import *
from com.bulletphysics.collision.shapes import *
from com.bulletphysics.dynamics import *
from com.bulletphysics.dynamics.constraintsolver import *
from com.bulletphysics.dynamics.vehicle import *
from com.bulletphysics.linearmath import *
from javax.vecmath import Vector3f, Matrix4f

import ccm


def createBox(x, y, z):
    box=Object3D(12)
    box.addTriangle(SimpleVector(x, y, -z), SimpleVector(x, -y, -z), SimpleVector(-x, y, -z))
    box.addTriangle(SimpleVector(-x, -y, -z), SimpleVector(-x, y, -z), SimpleVector(x, -y, -z))

    box.addTriangle(SimpleVector(x, y, z), SimpleVector(-x, y, z), SimpleVector(x, -y, z))
    box.addTriangle(SimpleVector(-x, -y, z), SimpleVector(x, -y, z), SimpleVector(-x, y, z))

    box.addTriangle(SimpleVector(x, -y, -z), SimpleVector(x, y, -z), SimpleVector(x, -y, z))
    box.addTriangle(SimpleVector(x, y, z), SimpleVector(x, -y, z), SimpleVector(x, y, -z))

    box.addTriangle(SimpleVector(-x, y, z), SimpleVector(-x, y, -z), SimpleVector(-x, -y, z))
    box.addTriangle(SimpleVector(-x, -y, -z), SimpleVector(-x, -y, z), SimpleVector(-x, y, -z))

    box.addTriangle(SimpleVector(-x, -y, -z), SimpleVector(x, -y, -z), SimpleVector(-x, -y, z))
    box.addTriangle(SimpleVector(x, -y, z), SimpleVector(-x, -y, z), SimpleVector(x, -y, -z))

    box.addTriangle(SimpleVector(x, y, z), SimpleVector(x, y, -z), SimpleVector(-x, y, z))
    box.addTriangle(SimpleVector(-x, y, -z), SimpleVector(-x, y, z), SimpleVector(x, y, -z))

    return box
    
def createBoxPieces(x,y,z):
    pieces=[]
    box=Object3D(12)
    box.addTriangle(SimpleVector(x, y, -z), SimpleVector(x, -y, -z), SimpleVector(-x, y, -z))
    box.addTriangle(SimpleVector(-x, -y, -z), SimpleVector(-x, y, -z), SimpleVector(x, -y, -z))
    pieces.append(box)

    box=Object3D(12)
    box.addTriangle(SimpleVector(x, y, z), SimpleVector(-x, y, z), SimpleVector(x, -y, z))
    box.addTriangle(SimpleVector(-x, -y, z), SimpleVector(x, -y, z), SimpleVector(-x, y, z))
    pieces.append(box)

    box=Object3D(12)
    box.addTriangle(SimpleVector(x, -y, -z), SimpleVector(x, y, -z), SimpleVector(x, -y, z))
    box.addTriangle(SimpleVector(x, y, z), SimpleVector(x, -y, z), SimpleVector(x, y, -z))
    pieces.append(box)

    box=Object3D(12)
    box.addTriangle(SimpleVector(-x, y, z), SimpleVector(-x, y, -z), SimpleVector(-x, -y, z))
    box.addTriangle(SimpleVector(-x, -y, -z), SimpleVector(-x, -y, z), SimpleVector(-x, y, -z))
    pieces.append(box)

    box=Object3D(12)
    box.addTriangle(SimpleVector(-x, -y, -z), SimpleVector(x, -y, -z), SimpleVector(-x, -y, z))
    box.addTriangle(SimpleVector(x, -y, z), SimpleVector(-x, -y, z), SimpleVector(x, -y, -z))
    pieces.append(box)

    box=Object3D(12)
    box.addTriangle(SimpleVector(x, y, z), SimpleVector(x, y, -z), SimpleVector(-x, y, z))
    box.addTriangle(SimpleVector(-x, y, -z), SimpleVector(-x, y, z), SimpleVector(x, y, -z))
    pieces.append(box)

    return pieces
    


class Room(ccm.Model):
    def __init__(self, x, y, z=1,gravity=10,color=[Color(0xFFFFFF),Color(0xFFFFFF),Color(0xEEEEEE),Color(0xDDDDDD),Color(0xCCCCCC),Color(0xBBBBBB)],camera=(0,0,10),dt=0.0001):
        ccm.Model.__init__(self)
        self.size=(x, y, z)
        self.world=World()
        self.dt=dt
        brightness=180
        self.world.setAmbientLight(brightness, brightness, brightness)
        self.world.addLight(SimpleVector(2, 2, 5), 10,10,10)
        self.world.camera.setPosition(*camera)
        self.world.camera.setOrientation(SimpleVector(0,0,-1),SimpleVector(0,-1,0))

        self.objects=[]

        collisionConfiguration=DefaultCollisionConfiguration()
        dispatcher=CollisionDispatcher(collisionConfiguration)

        overlappingPairCache=AxisSweep3(Vector3f(-x/2.0, -y/2.0, -z/2.0), Vector3f(x/2.0,y/2.0,z/2.0), 1024)
        solver=SequentialImpulseConstraintSolver()
        self.physics=DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration)
        self.physics.setGravity(Vector3f(0, 0, -gravity))

        t=Transform()
        t.setIdentity()
        rbi=RigidBodyConstructionInfo(0,DefaultMotionState(t),StaticPlaneShape(Vector3f(0,0,1),0),Vector3f(0,0,0))
        self.physics.addRigidBody(RigidBody(rbi))
        rbi=RigidBodyConstructionInfo(0,DefaultMotionState(t),StaticPlaneShape(Vector3f(0,1,0),-y/2.0),Vector3f(0,0,0))
        self.physics.addRigidBody(RigidBody(rbi))
        rbi=RigidBodyConstructionInfo(0,DefaultMotionState(t),StaticPlaneShape(Vector3f(0,-1,0),-y/2.0),Vector3f(0,0,0))
        self.physics.addRigidBody(RigidBody(rbi))
        rbi=RigidBodyConstructionInfo(0,DefaultMotionState(t),StaticPlaneShape(Vector3f(1,0,0),-x/2.0),Vector3f(0,0,0))
        self.physics.addRigidBody(RigidBody(rbi))
        rbi=RigidBodyConstructionInfo(0,DefaultMotionState(t),StaticPlaneShape(Vector3f(-1,0,0),-x/2.0),Vector3f(0,0,0))
        self.physics.addRigidBody(RigidBody(rbi))
        
        self.vehicle_raycaster=DefaultVehicleRaycaster(self.physics)
        
        if not isinstance(color,(list,tuple)):
            room=createBox(x/2.0, y/2.0, z/2.0)
            room.translate(0, 0, z/2.0)
            room.invert()        
            colorname='room%d'%id(self)
            TextureManager.getInstance().addTexture(colorname,Texture(1, 1, color))
            room.setTexture(colorname)
            room.shadingMode=Object3D.SHADING_FAKED_FLAT
            room.build()
            self.world.addObject(room)
        else:
            room=createBoxPieces(x/2.0, y/2.0, z/2.0)
            for i,r in enumerate(room):
                r.translate(0, 0, z/2.0)
                r.invert()        
                colorname='room%d_%d'%(id(self),i)
                TextureManager.getInstance().addTexture(colorname,Texture(1, 1, color[i%len(color)]))
                r.setTexture(colorname)
                r.shadingMode=Object3D.SHADING_FAKED_FLAT
                r.build()
                self.world.addObject(r)

        self.ratelimit=RateLimit()
    
    def add(self, obj, x,y,z=None):
        if z is None:
            t=Transform()
            min=Vector3f()
            max=Vector3f()
            obj.physics.getAabb(min,max)
            z=-min.z
        self.temp=obj
        if hasattr(obj,'physics'):
            t=Transform()
            ms=obj.physics.motionState            
            ms.getWorldTransform(t)
            m=Matrix4f()
            t.getMatrix(m)
            m.m03=x
            m.m13=y
            m.m23=z
            t.set(m)
            ms.worldTransform=t
            obj.physics.motionState=ms
            
            self.physics.addRigidBody(obj.physics)

            if hasattr(obj,'wheels'):                
                tuning=VehicleTuning()
                obj.vehicle=RaycastVehicle(tuning,obj.physics,self.vehicle_raycaster)
                obj.physics.setActivationState(CollisionObject.DISABLE_DEACTIVATION)
                self.physics.addVehicle(obj.vehicle)
                obj.vehicle.setCoordinateSystem(1,2,0)
                
                for w in obj.wheels:
                    wheel=obj.vehicle.addWheel(Vector3f(w.x,w.y,w.z),Vector3f(w.dir),Vector3f(w.axle),w.suspension_rest_len,w.radius,tuning,False)
                    wheel.suspensionStiffness=w.suspension_stiffness
                    wheel.maxSuspensionTravelCm=w.suspension_max_travel*100
                    wheel.frictionSlip=w.friction
                    wheel.wheelsDampingRelaxation=w.damping_relaxation
                    wheel.wheelsDampingCompression=w.damping_compression
                    wheel.rollInfluence=w.roll_influence
                    
        self.objects.append(obj)
        self.world.addObject(obj.shape)
        
            

    def start(self):
        dt=self.dt
        while True:
            p=self.physics_dump()
            #self.update_shapes(p)
            for obj in self.objects:
                if hasattr(obj,'vehicle'):
                    for i,w in enumerate(obj.wheels):
                        obj.vehicle.updateWheelTransform(i, True);
                        obj.vehicle.applyEngineForce(w.force,i)
                        #obj.vehicle.setBrake(w.brake,i)

            self.physics.stepSimulation(dt,10,dt*0.1)    
            yield dt
    def physics_dump(self):
        d={}
        for obj in self.objects:
            if hasattr(obj,'physics'):
                t=Transform()
                obj.physics.getMotionState().getWorldTransform(t)
                m=Matrix4f()
                t.getMatrix(m)
                
                trans=Matrix()
                trans.set(3,0,m.m03)
                trans.set(3,1,m.m13)
                trans.set(3,2,m.m23)
                rot=Matrix()
                for i in range(3):
                    for j in range(3):
                        rot.set(i,j,m.getElement(j,i))
                d[obj]=(trans,rot)
        return d                    

    def update_shapes(self,physics):
        for obj in self.objects:
            if hasattr(obj,'physics') and obj in physics:
                trans,rot=physics[obj]
                obj.shape.translationMatrix=trans
                obj.shape.rotationMatrix=rot
                if hasattr(obj,'extra_shapes'):
                    for s,x,y,z in obj.extra_shapes:
                        p=SimpleVector(x,y,z)
                        p.matMul(rot)
                        m4=Matrix(trans)
                        m4.translate(p)
                        s.translationMatrix=m4
                        s.rotationMatrix=rot
                    

class Wheel:
    def __init__(self,x,y,z,dir=(0,0,-1),axle=(0,-1,0),radius=0.3,
                        suspension_stiffness=20,suspension_max_travel=0.1,suspension_rest_len=0,
                        friction=10,damping_relaxation=2.3,damping_compression=4.4,roll_influence=0.1):
        self.x=x
        self.y=y
        self.z=z
        self.dir=dir
        self.axle=axle
        self.radius=radius
        self.suspension_stiffness=suspension_stiffness
        self.suspension_max_travel=suspension_max_travel
        self.suspension_rest_len=suspension_rest_len
        self.friction=friction
        self.damping_relaxation=damping_relaxation
        self.damping_compression=damping_compression
        self.roll_influence=roll_influence     
        
        self.force=0
        self.brake=0                   


class RangeSensorCallback(CollisionWorld.RayResultCallback):
    def __init__(self,sensor):
        CollisionWorld.RayResultCallback.__init__(self)
        self.sensor=sensor
    def addSingleResult(self,result,normal):
        print result

class RangeSensor(ccm.Model):
    def __init__(self,x,y,z,origin=(0,0,0),maximum=100,timestep=0.01):
        ccm.Model.__init__(self)
        self.dir=Vector3f(x,y,z)
        self.dir.normalize()
        self.dir.scale(maximum)
        
        self.origin=Vector3f(origin)
        
        self.range=0
        self.timestep=timestep
        
    def start(self):
        while True:
            physics=self.parent.parent.physics
            
            t=Transform()
            self.parent.physics.getWorldTransform(t)
            pt1=Vector3f(self.origin)
            pt2=Vector3f(self.dir)
            t.transform(pt1)
            t.transform(pt2)
            
            callback=CollisionWorld.ClosestRayResultCallback(pt1,pt2)
            physics.rayTest(pt1,pt2,callback)
            
            delta=Vector3f(callback.hitPointWorld)
            delta.sub(pt1)
            self.range=delta.length()
        
            yield self.timestep
            


        
        
            
    
        
class Box(ccm.Model):      
    def __init__(self, x, y, z, mass=1,scale=1, draw_as_cylinder=False,color=Color.blue,overdraw_length=1,overdraw_radius=1,flat_shading=False):
        
        if draw_as_cylinder:
            if y is max(x,y,z):
                radius=(x+z)/4*overdraw_radius
                self.shape=Primitives.getCylinder(90,radius,y/(radius*2)*overdraw_length)
            if z is max(x,y,z):
                radius=(x+y)/4*overdraw_radius
                self.shape=Primitives.getCylinder(90,radius,z/(radius*2)*overdraw_length)
                self.shape.rotateX(math.pi/2)
                self.shape.rotateMesh()
        else:
            self.shape=createBox(x/2.0*scale, y/2.0*scale, z/2.0*scale)
        if flat_shading: self.shape.shadingMode=Object3D.SHADING_FAKED_FLAT
            
        colorname='box%d'%id(self)
        TextureManager.getInstance().addTexture(colorname,Texture(1, 1, color))
        self.shape.setTexture(colorname)
        self.shape.build()

        shape=BoxShape(Vector3f(x/2.0,y/2.0,z/2.0))

        inertia=Vector3f(0,0,0)
        shape.calculateLocalInertia(mass,inertia)

        t=Transform()
        t.setIdentity()
        
        ms=DefaultMotionState(t)
        rb=RigidBodyConstructionInfo(mass,ms,shape,inertia)
        self.physics=RigidBody(rb)
        
    def add_sphere_at(self,x,y,z,radius,color,room):
        s=Primitives.getSphere(radius)
        
        colorname='added_sphere_%d'%id(s)
        TextureManager.getInstance().addTexture(colorname,Texture(1,1,color))
        s.setTexture(colorname)
        s.build()
        if not hasattr(self,'extra_shapes'):
            self.extra_shapes=[]
        self.extra_shapes.append((s,x,y,z))
        room.world.addObject(s)
        
        
class Sphere(ccm.Model):      
    def __init__(self, r, mass=1,color=Color.red):
        colorname='sphere_%d'%id(self)
        self.shape=Primitives.getSphere(r)
        TextureManager.getInstance().addTexture(colorname,Texture(1, 1, color))
        self.shape.setTexture(colorname)
        self.shape.build()
        
        shape=BoxShape(Vector3f(r/2.0,r/2.0,r/2.0))

        inertia=Vector3f(0,0,0)
        shape.calculateLocalInertia(mass,inertia)

        t=Transform()
        t.setIdentity()
        
        ms=DefaultMotionState(t)
        rb=RigidBodyConstructionInfo(mass,ms,shape,inertia)
        self.physics=RigidBody(rb)
        

import math
class MD2(ccm.Model):
    _shapes={}
    def __init__(self, filename, texture, scale=1.0, mass=1,overdraw_scale=1.0):        
        if not TextureManager.getInstance().containsTexture(texture):
            TextureManager.getInstance().addTexture(texture,Texture(texture))
        if (filename,scale) not in MD2._shapes:
            shape=Loader.loadMD2(filename, scale)
            shape.rotateAxis(SimpleVector(1, 0, 0), math.pi/2)
            shape.rotateAxis(SimpleVector(0, 0, 1), -math.pi/2)
            shape.rotateMesh()
            shape.rotationMatrix=Matrix()
            
            MD2._shapes[(filename,scale)]=shape
        #self.shape=MD2._shapes[(filename,scale)].cloneObject()
        self.shape=Object3D(MD2._shapes[(filename,scale)],False)
        self.shape.build()
        self.shape.setTexture(texture)
        minx,maxx,miny,maxy,minz,maxz=self.shape.mesh.boundingBox
        shape=BoxShape(Vector3f((maxx-minx)/overdraw_scale/2.0,(maxy-miny)/overdraw_scale/2.0,(maxz-minz)/2.0))
        t=Transform()
        t.setIdentity()
        t.origin.x=(maxx+minx)/2
        t.origin.y=(maxy+miny)/2
        t.origin.z=(maxz+minz)/2
        ms=DefaultMotionState(t)
        inertia=Vector3f(0,0,0)
        shape.calculateLocalInertia(mass,inertia)
        rb=RigidBodyConstructionInfo(mass,ms,shape,inertia)
        self.physics=RigidBody(rb)
        self.shape.setOrigin(SimpleVector(-(maxx+minx)/2,-(maxy+miny)/2,-(maxz+minz)/2))
        
        
    def start(self):    
        self.frame=0
        self.sch.add(self.animate)
            
    def animate(self):
        self.frame=(self.frame+0.002)
        while self.frame>1: self.frame-=1
        self.shape.animate(self.frame, 1)        
        return 0.01
        

class RateLimit(ccm.Model):
    def start(self):
        if not hasattr(self.sch,'rate'): self.sch.rate=1
        dt=0.001
        while True:
            real1=java.lang.System.nanoTime()*0.000000001
            yield dt
            real2=java.lang.System.nanoTime()*0.000000001
            while (real2-real1)*self.sch.rate<dt:
                real2=java.lang.System.nanoTime()*0.000000001

class RateLimit(ccm.Model):
    def start(self):
        if not hasattr(self.sch,'rate'): self.sch.rate=1
        dt=0.01
        while True:
            real1=java.lang.System.currentTimeMillis()*0.001
            yield dt
            real2=java.lang.System.currentTimeMillis()*0.001
            while (real2-real1)*self.sch.rate<dt:
                real2=java.lang.System.currentTimeMillis()*0.001
                java.lang.Thread.currentThread().sleep(1)


        
        
        
        
class View(JComponent): 
    def __init__(self, obj, location, size=(800, 600),keys=None):
        JComponent.__init__(self)
        self.location=list(location)
        self.buffer=FrameBuffer(size[0], size[1], FrameBuffer.SAMPLINGMODE_NORMAL)
        self.obj=obj
        self.frame=JFrame(keyPressed=self.keyPressed,keyReleased=self.keyReleased)
        self.frame.add(self)
        self.size=size
        self.frame.pack()
        self.frame.size=size
        self.frame.visible=True
        self.clearColor=Color(0x666666)
        self.keys=keys

    def paint(self, g):
        x, y, z=self.location
        self.obj.world.camera.setPosition(x, -y, z)
        self.obj.world.camera.lookAt(SimpleVector(0, 0, 0))
        
        self.buffer.clear(self.clearColor)
        self.obj.world.renderScene(self.buffer)
        self.obj.world.draw(self.buffer)
        self.buffer.update()
        self.buffer.display(g)
        self.repaint()

    def keyReleased(self,event):
        if self.keys is not None and hasattr(self.keys,'key_released'):
            self.keys.key_released(KeyEvent.getKeyText(event.keyCode))

        
    def keyPressed(self,event):
        if self.keys is not None and hasattr(self.keys,'key_pressed'):
            self.keys.key_pressed(KeyEvent.getKeyText(event.keyCode))
        if event.keyCode==KeyEvent.VK_ESCAPE:
            self.frame.dispose()
        elif event.keyCode==KeyEvent.VK_PAGE_UP:
            self.obj.sch.rate*=1.1
            self.frame.title='rate: %1.3f'%self.obj.sch.rate
        elif event.keyCode==KeyEvent.VK_PAGE_DOWN:
            self.obj.sch.rate/=1.1
            self.frame.title='rate: %1.3f'%self.obj.sch.rate
        elif event.keyCode==KeyEvent.VK_Z:
            self.location[2]+=1
        elif event.keyCode==KeyEvent.VK_X:
            self.location[2]-=1
        elif event.keyCode==KeyEvent.VK_W:
            self.location[1]+=1
        elif event.keyCode==KeyEvent.VK_S:
            self.location[1]-=1
        elif event.keyCode==KeyEvent.VK_A:
            self.location[0]+=1
        elif event.keyCode==KeyEvent.VK_D:
            self.location[0]-=1
        
        
