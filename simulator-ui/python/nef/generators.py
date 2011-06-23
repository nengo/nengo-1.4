from ca.nengo.util import VectorGenerator
from java.io import Serializable

import math

class FixedVectorGenerator(VectorGenerator,Serializable):
    serialVersionUID=1
    def __init__(self,vectors):
        self.vectors=[]
        for v in vectors:
            length=math.sqrt(sum([x*x for x in v]))
            self.vectors.append([x/length for x in v])
        
    def genVectors(self,number,dimensions):        
        vectors=[]
        while len(vectors)<number:
            vectors.extend(self.vectors)    
        return vectors[:number]

class FixedEvalPointGenerator(VectorGenerator,Serializable):
    serialVersionUID=1
    def __init__(self,points):
        self.points=points
        
    def genVectors(self,number,dimensions):        
        # points=[]
        # while len(points)<number:
            # points.extend(self.points)
        # return points[:number]
        return  self.points
