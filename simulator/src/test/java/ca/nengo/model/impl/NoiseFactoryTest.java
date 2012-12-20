package ca.nengo.model.impl;

import ca.nengo.dynamics.DynamicalSystem;
import ca.nengo.dynamics.Integrator;
import ca.nengo.dynamics.impl.EulerIntegrator;
import ca.nengo.dynamics.impl.SimpleLTISystem;
import ca.nengo.math.impl.GaussianPDF;
import ca.nengo.model.Noise;
import ca.nengo.model.Units;
import ca.nengo.plot.Plotter;
import ca.nengo.util.MU;
import org.junit.Test;

public class NoiseFactoryTest {
	@Test
    public void testNothing() {
    }

    //functional test ...
    public static void main(String[] args) {
        float tau = .01f;
        DynamicalSystem dynamics = new SimpleLTISystem(new float[]{-1f/tau}, new float[][]{new float[]{1f/tau}}, MU.I(1), new float[1], new Units[]{Units.UNK});
        Integrator integrator = new EulerIntegrator(.0001f);
        Noise noise = NoiseFactory.makeRandomNoise(1000, new GaussianPDF(0, 1), dynamics, integrator);

        float elapsedTime = .001f;
        int steps = 1000;
        float[] output = new float[steps];
        for (int i = 0; i < steps; i++) {
            output[i] = noise.getValue(i*elapsedTime, (i+1)*elapsedTime, 1);
        }

        Plotter.plot(output, "noise");
    }
}
