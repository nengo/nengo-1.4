from ca.nengo.math.impl import IndicatorPDF

class Uniform(IndicatorPDF):
    def sample(self, num_samples = 1):
        if( num_samples == 1 ):
            return IndicatorPDF.sample(self)
        else:
            return [IndicatorPDF.sample(self)[0] for _ in range(num_samples)]