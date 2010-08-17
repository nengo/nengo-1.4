import nef
import nps


net=nef.Network('Sequence with Routing')
p=nps.ProductionSet()
p.add(lhs=dict(visual='letter',memory='-A-B-C-D-E'),rhs=dict(visual_to_memory=True))
p.add(lhs=dict(memory='A'),rhs=dict(memory='B'))
p.add(lhs=dict(memory='B'),rhs=dict(memory='C'))
p.add(lhs=dict(memory='C'),rhs=dict(memory='D'))
p.add(lhs=dict(memory='D'),rhs=dict(memory='E'))
p.add(lhs=dict(memory='E'),rhs=dict(memory='A'))
model=nps.NPS(net,p,dimensions=10,neurons_buffer=30,align_hrr=True)
model.add_buffer_feedback(memory=0.98)
net.add_to(world)



