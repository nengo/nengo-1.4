import nef
import nps


net=nef.Network('Sequence')
p=nps.ProductionSet()
p.add(lhs=dict(memory='A'),rhs=dict(memory='B'))
p.add(lhs=dict(memory='B'),rhs=dict(memory='C'))
p.add(lhs=dict(memory='C'),rhs=dict(memory='D'))
p.add(lhs=dict(memory='D'),rhs=dict(memory='E'))
p.add(lhs=dict(memory='E'),rhs=dict(memory='A'))
model=nps.NPS(net,p,dimensions=10,neurons_buffer=30,align_hrr=True)
model.set_initial(0.1,memory='A')
model.add_buffer_feedback(memory=1)
net.add_to(world)



