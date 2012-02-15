spike_strength=0.001
histogram_bits=16
histogram_n=1000
table_bits=16

generate_matrix_n=1000
verbose=True

import numeric
import random
import bisect
from ca.nengo.util import MU


def compute_histogram(d,e):
    hist={}
    for i in xrange(histogram_n):
        count=0
        for ee in e:
            if random.random()<abs(d*ee): count+=1
        if count not in hist:
            hist[count]=1
        else:
            hist[count]=hist[count]+1
    for k,v in hist.items():
        hist[k]=v/float(histogram_n)
    if verbose: print 'histogram:',hist
    return hist
         
def compute_cdf(hist):
    scale=2**histogram_bits
    cdf=[]
    i=0
    value=0
    while value<1.0 and len(hist)>0:
        p=hist.get(i,0)
        value+=p
        cdf.append(int(value*scale))
        if i in hist: del hist[i]
        i+=1
    if verbose: print i,'cdf', cdf
    return cdf    
 
def determine_spike_count(cdf):
    r=random.randrange(2**histogram_bits)
    i=bisect.bisect_left(cdf,r)
    return i
 
def compute_prob_table(prob):
    total=sum(prob)
    scale=2**table_bits
    table=[]
    value=0
    for p in prob:
        value+=p
        table.append(int(value*scale/total))
    return table

def make_output_table(encoder):
    prob=[]
    sign=[]
    for ee in encoder:
        if ee<0:
            sign.append(-spike_strength)
            prob.append(-ee)
        else:
            sign.append(spike_strength)
            prob.append(ee)
    table=compute_prob_table(prob)
    if verbose: print 'output table:',table
    if verbose: print 'sign:',sign
    return sign,table
    
    
def determine_target(table):
    r=random.randrange(2**table_bits)
    i=bisect.bisect_left(table,r)
    return i
    
            
def compute_weights(encoder,decoder):    
    N1=len(decoder[0])
    D=len(decoder)
    N2=len(encoder)
    w=numeric.zeros((N2,N1),typecode='f')
    
    for dim in range(D):
        sign,table=make_output_table([e[dim] for e in encoder])

        for i in range(N1):
            d=decoder[dim][i]/spike_strength
            if d<0:
                decoder_sign=-1
                d=-d
            else:
                decoder_sign=1
            histogram=compute_histogram(d,[e[dim] for e in encoder])
            cdf=compute_cdf(histogram)

            for k in range(generate_matrix_n):
                spike_count=determine_spike_count(cdf)
                for s in range(spike_count):
                    j=determine_target(table)
                    #TODO: check for multiple spikes to same target
                    w[j][i]+=decoder_sign*sign[j]
                    
    w/=generate_matrix_n
    #w2=numeric.array(MU.prod(encoder,decoder))
    return w

import nef
class DartboardConnection(nef.SimpleNode):
    def __init__(self,name):
        self.spikes=[]
        self.input_spike_count=0
        self.output_spike_count=0
        self.N2=1
        nef.SimpleNode.__init__(self,name)
    def calc_weights(self,encoder,decoder):
        self.N1=len(decoder[0])
        self.D=len(decoder)
        self.N2=len(encoder)
        self.getTermination('input').setDimensions(self.N1)
        self.getOrigin('output').setDimensions(self.N2)
        
        self.tables=[]
        self.histograms=[]
        for dim in range(self.D):
            cdfs=[]
            self.tables.append(make_output_table([e[dim] for e in encoder]))
            for i in range(self.N1):
                d=decoder[dim][i]/spike_strength
                if d<0:
                    decoder_sign=-1
                    d=-d
                else:
                    decoder_sign=1
                histogram=compute_histogram(d,[e[dim] for e in encoder])
                cdf=compute_cdf(histogram)
                cdfs.append((decoder_sign,cdf))
            self.histograms.append(cdfs)
        
        return numeric.array(MU.prod(encoder,decoder))
    
    def termination_input(self,x):
        self.spikes=x
    def origin_input_spikes(self):
        dt=self.t_end-self.t
        if dt==0: dt=0.001
        return [self.input_spike_count/dt]
    def origin_output_spikes(self):
        dt=self.t_end-self.t
        if dt==0: dt=0.001
        return [self.output_spike_count/dt]
    def origin_output(self):
        self.input_spike_count=0
        self.output_spike_count=0
        
        result=[0]*self.N2
        dt=self.t_end-self.t
        if dt==0: dt=0.001

        for i,s in enumerate(self.spikes):
            if s>0:
                self.input_spike_count+=1
                for dim in range(self.D):
                    decoder_sign,cdf=self.histograms[dim][i]
                    sign,table=self.tables[dim]
                    
                    targets=[]
                    spike_count=determine_spike_count(cdf)
                    for s in range(spike_count):
                        j=determine_target(table)

                        retry=1000
                        while j in targets and spike_count<self.N2 and retry>0:
                            j=determine_target(table)
                            retry-=1

                        result[j]+=decoder_sign*sign[j]/dt
                        self.output_spike_count+=1
        return result            
                    
            
        

def connect(net,A,B,transform=None):
    dartboard=DartboardConnection('Dartboard:%s:%s'%(A.name,B.name))
    o,t=net.connect(A,B,transform=transform,weight_func=lambda e,d: dartboard.calc_weights(e,d),create_projection=False)
    net.add(dartboard)
    net.connect(o,dartboard.getTermination('input'))
    B.removeTermination(t.name)
    t2=B.addTermination(t.name,numeric.eye(dartboard.N2),t.tau,False)
    net.connect(dartboard.getOrigin('output'),t2)
    
    
