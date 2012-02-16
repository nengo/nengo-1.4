package ca.nengo.ui.audio;
import javax.sound.sampled.*;
import java.lang.Thread;
import java.lang.Runnable;


public class Clicker {
	boolean started=false;
	SourceDataLine line;
	byte[] value=new byte[bufferSize];

	private static final int bufferSize=400;
	private static final int Hz=14400;
	
	public Clicker() {		
	}
	
	public boolean start() {
		if (started) return false;
		AudioFormat format=new AudioFormat(Hz,8,1,false,false);
		DataLine.Info info=new DataLine.Info(SourceDataLine.class, format,400);
		if (!AudioSystem.isLineSupported(info)) return false;
		
		try {
			line=(SourceDataLine)AudioSystem.getLine(info);
			line.open(format);
		} catch (LineUnavailableException e) {
			return false;
		}
		
		line.start();
		
		return true;
	}
	
	public void set(byte b) {
		int avail=line.available();
		if (avail<=0) return;
		int steps;
		
		if (avail<100) {
			steps=1;
		} else if (avail>700) {
			steps=100;
		} else {
			steps=(avail-100)/7+1;
		}
		
		
		/*
		steps=avail;
		if (steps<0) steps=0;
		if (steps>bufferSize) steps=bufferSize;
		*/
		//System.err.println("steps: "+steps);
		
		
		value[bufferSize-1]=b;
		
		
		line.write(value,(int)(bufferSize-steps),(int)steps);
	}
	
	public void stop() {
		line.close();
	}
	
	
}
