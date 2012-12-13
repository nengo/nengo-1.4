package ca.nengo.model.plasticity.impl;

import junit.framework.TestCase;
import ca.nengo.model.StructuralException;
import ca.nengo.model.impl.LinearExponentialTermination;
import ca.nengo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.nengo.model.nef.impl.NEFEnsembleImpl;
import ca.nengo.model.neuron.impl.SpikingNeuron;

public class PlasticEnsembleTerminationTest extends TestCase {

    public void testGetTransform() throws StructuralException {
        float[][] transform = new float[10][];
        for(int i = 0; i < transform.length; i++) {
            transform[i] = new float[]{1.0f, 1.0f, 1.0f};
        }

        NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
        NEFEnsembleImpl c = (NEFEnsembleImpl)ef.make("c", 10, 1);

        LinearExponentialTermination[] nodeterms = new LinearExponentialTermination[10];
        for(int i = 0; i < nodeterms.length; i++) {
            nodeterms[i] = new LinearExponentialTermination(new SpikingNeuron(null, null, 0.0f, 0.0f, null), null, transform[i], 0.0f);
        }

        PlasticEnsembleTermination term = new PESTermination(c, null, nodeterms);
        float[][] rettransform = term.getTransform();

        assertTrue(rettransform.length == transform.length);

        for(int i = 0; i < transform.length; i++)
        {
            assertTrue(rettransform[i].length == transform[i].length);
            for(int j = 0; j < transform[i].length; j++) {
                assertTrue(rettransform[i][j] == transform[i][j]);
            }
        }
    }

    public void testSetTransform() throws StructuralException{
        float[][] transform = new float[10][];
        float[][] newtransform = new float[10][];
        for(int i = 0; i < transform.length; i++)
        {
            transform[i] = new float[]{1.0f, 1.0f, 1.0f};
            newtransform[i] = new float[]{0.0f, 0.0f, 0.0f};
        }

        NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
        NEFEnsembleImpl c = (NEFEnsembleImpl)ef.make("c", 10, 1);

        LinearExponentialTermination[] nodeterms = new LinearExponentialTermination[10];
        for(int i = 0; i < nodeterms.length; i++) {
            nodeterms[i] = new LinearExponentialTermination(new SpikingNeuron(null, null, 0.0f, 0.0f, null), null, transform[i], 0.0f);
        }

        PlasticEnsembleTermination term = new PESTermination(c, null, nodeterms);
        term.setTransform(newtransform, true);

        float[][] rettransform = term.getTransform();

        assertTrue(rettransform.length == newtransform.length);

        for(int i = 0; i < newtransform.length; i++)
        {
            assertTrue(rettransform[i].length == newtransform[i].length);
            for(int j = 0; j < newtransform[i].length; j++) {
                assertTrue(rettransform[i][j] == newtransform[i][j]);
            }
        }

        term.reset(false);
        rettransform = term.getTransform();

        assertTrue(rettransform.length == newtransform.length);

        for(int i = 0; i < newtransform.length; i++)
        {
            assertTrue(rettransform[i].length == newtransform[i].length);
            for(int j = 0; j < newtransform[i].length; j++) {
                assertTrue(rettransform[i][j] == newtransform[i][j]);
            }
        }
    }
}
