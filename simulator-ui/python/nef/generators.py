from ca.nengo.util import VectorGenerator
from java.io import Serializable

import math

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
