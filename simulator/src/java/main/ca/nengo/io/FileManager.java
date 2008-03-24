/*
 * Created on 7-Jun-2006
 */
package ca.nengo.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ca.nengo.model.Node;
import ca.nengo.util.TimeSeries;

/**
 * Handles saving and loading of Node   
 * 
 * TODO: a better job (this is a quick one)
 * TODO: is there any metadata to store? 
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class FileManager {

	public static final String ENSEMBLE_EXTENSION = "nef";
	
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

	public void save(Node node, File destination) throws IOException {
		saveObject(node, destination);
	}
	
	public void save(TimeSeries timeSeries, File destination) throws IOException {
		saveObject(timeSeries, destination);
	}
	
	private static void saveObject(Object object, File destination) throws IOException {		
		FileOutputStream fos = new FileOutputStream(destination);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(object);
		oos.flush();
		fos.close();
	}
	
	public Object load(File source) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(source);
		
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		return ois.readObject();
	}
	
}
