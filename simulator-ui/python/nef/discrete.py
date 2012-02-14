
import random

def get_max_weight(term):
    maxw=max([max(t.weights) for t in term.nodeTerminations])
    minw=min([min(t.weights) for t in term.nodeTerminations])
    return max(maxw,-minw)

def convert_to_probability(term,strength=None):
    if strength is None:
        strength=get_max_weight(term)
        
    for t in term.nodeTerminations:
        wp=[]
        w=[]
        for ww in t.weights:
            if ww<0: 
                w.append(-strength)
                ww=-ww
            else:
                w.append(strength)
            wp.append(ww/strength)
        t.setWeights(w,True)
        t.weightProbabilities=wp
        
def discretize(term,bits=16,shift=0,randomize_rounding=True):
    maxp=2.0**(-shift)
    scale=(2.0**bits)/maxp
    for t in term.nodeTerminations:
        wp=[]
        for p in t.weightProbabilities:
            r=0
            if randomize_rounding: r=random.random()
            p=int(p*scale+r)/scale
            if p>maxp: p=maxp
            
            wp.append(p)
        t.weightProbabilities=wp
            
