package ca.nengo.math.impl;

import ca.nengo.math.Function;
import ca.nengo.plot.Plotter;
import org.junit.Test;

public class ConvolutionTest {

	@Test
    public void testNothing() {
    }

    //functional test ...
    public static void main(String[] args) {
        Function one = new PiecewiseConstantFunction(new float[]{0.1f}, new float[]{0, 1});
        Function two = new AbstractFunction(1) {
            private static final long serialVersionUID = 1L;
            public float map(float[] from) {
                float t = from[0];
                float tau = .05f;
                return (1 - t/tau) * (float) Math.exp(-t/tau);
            }
        };

        Function conv = new Convolution(one, two, .0001f, .5f);

        Plotter.plot(conv, 0, .001f, 1f, "convolution of step and differentiator impulse response");
    }
}
