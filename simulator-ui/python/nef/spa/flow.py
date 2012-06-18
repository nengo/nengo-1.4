import re
import nef
import hrr
import numeric
import math

class Flow:
    def __init__(self):
        self.nodes={}
        self.connect={}
        self.ands=[]
        self.inhibit={}
        self.excite={}
        
    def add(self,flow):
        for line in flow.splitlines():
            line=line.strip()
            if len(line)==0: continue
            pattern=line.split('->')
            if len(pattern)!=2:
                print 'Error parsing line:',line
                continue
            pre,post=pattern
            
            pre=pre.split(' and ')
            pre=[self.parse_term(p) for p in pre]
            post=self.parse_term(post)
            if None in pre or post is None:
                print 'Error parsing line:',line
            if len(pre)==1:
                pre=pre[0]
                key=pre[0],post[0]
                if key not in self.connect: self.connect[key]=[]
                self.connect[key].append((pre[1],post[1]))    
            else:
                self.ands.append((pre,post))
    def mutual_inhibit(self,**params):
        for k,v in params.items():
            self.inhibit[k]=v
            if (k,k) not in self.connect:
                self.connect[(k,k)]=[]   # add entry to list so that it'll be handled in create()
    def self_excite(self,**params):
        for k,v in params.items():
            self.excite[k]=v
            if (k,k) not in self.connect:
                self.connect[(k,k)]=[]   # add entry to list so that it'll be handled in create()
            
    def parse_term(self,term):
        if '.' not in term:
            return None
        name,term=term.split('.',1)
        if name not in self.nodes:
            self.nodes[name]=set()
        for t in re.split("[+|*|-]",term):
            self.nodes[name].add(t)
        return name,term
        
    def create_cleanup_inhibit(self,net,**params):
        for name,value in params.items():
            node=net.get(name)
            vocab=hrr.Vocabulary.registered[id(node)]
            cleanup=net.make_array('clean_'+name,50,len(vocab.keys),intercept=(0,1),encoders=[[1]])
            transform=[vocab.parse(k).v for k in vocab.keys]
            net.connect(node,cleanup,transform=transform)

            t=numeric.zeros((vocab.dimensions,len(vocab.keys)),typecode='f')
            for i in range(len(vocab.keys)):
                for j in range(len(vocab.keys)):
                    if i!=j:
                        t[:,i]+=vocab.parse(vocab.keys[j]).v*value
            net.connect(cleanup,node,transform=t)            
            
        
    def create(self,net,N=50,dimensions=8,randomize=False):
        vocab={}
        for k in self.nodes.keys():
            node=net.get(k,None)
            if node is None:
                dim=dimensions
                if randomize is False and len(self.nodes[k])+1>dim:
                    dim=len(self.nodes[k])+1
                node=net.make_array(k,N,dim)
            if not hrr.Vocabulary.registered.has_key(id(node)):
                v=hrr.Vocabulary(node.dimension,randomize=randomize)
                v.register(node)
            vocab[k]=hrr.Vocabulary.registered[id(node)]

        # ensure all terms are parsed before starting
        for k,v in self.connect.items():
            pre_name,post_name=k
            for pre_term,post_term in v:
                pre=vocab[pre_name].parse(pre_term).v
                post=vocab[post_name].parse(post_term).v
        
        for k,v in self.connect.items():
            pre_name,post_name=k
            
            t=numeric.zeros((vocab[post_name].dimensions,vocab[pre_name].dimensions),typecode='f')
            for pre_term,post_term in v:
                pre=vocab[pre_name].parse(pre_term).v
                post=vocab[post_name].parse(post_term).v
                t+=numeric.array([pre*bb for bb in post])

            if pre_name==post_name:         
                if pre_name in self.inhibit:
                    for pre_term in vocab[pre_name].keys:
                        pre=vocab[pre_name].parse(pre_term).v*self.inhibit[pre_name]
                        post_value=numeric.zeros(vocab[post_name].dimensions,typecode='f')
                        for post_term in vocab[pre_name].keys:
                            if pre_term!=post_term:
                                post_value+=vocab[post_name].parse(post_term).v
                        t+=numeric.array([pre*bb for bb in post_value])
                if pre_name in self.excite:
                    t+=numeric.eye(len(t))*self.excite[pre_name]
                    
            net.connect(net.get(pre_name),net.get(post_name),transform=t)    
        
        for i,(pre,post) in enumerate(self.ands):
            D=len(pre)
            node=net.make('and%02d'%i,D*N,D)
            for j,p in enumerate(pre):
                t=numeric.zeros((D,vocab[p[0]].dimensions),typecode='f')
                t[j,:]=vocab[p[0]].parse(p[1]).v*math.sqrt(D)
                net.connect(net.get(p[0]),node,transform=t)                
            def result(x,v=vocab[post[0]].parse(post[1]).v):
                for xx in x:
                    if xx<0.4: return [0]*len(v)  #TODO: This is pretty arbitrary....
                return v
            net.connect(node,net.get(post[0]),func=result)    
                
        return net    

