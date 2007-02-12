/*
 * Created on 7-Jun-2006
 */
package ca.neo.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ca.neo.model.Ensemble;
import ca.neo.model.Network;
import ca.neo.model.neuron.Neuron;

/**
 * Handles saving and loading of Networks, Ensembles, and individual Neurons.   
 * 
 * TODO: a better job (this is a quick one)
 * TODO: is there any metadata to store? 
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class FileManager {

	public static final String NETWORK_EXTENSION = "neonetwork";
	public static final String ENSEMBLE_EXTENSION = "neoensemble";
	public static final String NEURON_EXTENSION = "neoneuron";	
	
	private static File ourDefaultLocation = new File("./work");
	static {
		ourDefaultLocation.mkdirs();
	}
	
	public static File getDefaultLocation() {
		return ourDefaultLocation;
	}
	
	public static void setDefaultLocation(File location) {
		ourDefaultLocation = location;
	}

	public void save(Network network, File destination) throws IOException {
		saveObject(network, destination);
	}
	
	public void save(Ensemble ensemble, File destination) throws IOException {
		saveObject(ensemble, destination);
	}
	
	public void save(Neuron neuron, File destination) throws IOException {
		saveObject(neuron, destination);
	}	
	
	private static void saveObject(Object object, File destination) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(object);
		
		FileOutputStream fos = new FileOutputStream(destination);
		fos.write(bos.toByteArray());
		fos.flush();
		fos.close();
	}
	
	public Object load(File source) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(source);
		
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		return ois.readObject();
	}
	
}
