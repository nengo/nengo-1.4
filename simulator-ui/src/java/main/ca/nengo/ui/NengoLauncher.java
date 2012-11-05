package ca.nengo.ui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.DefaultApplication;

public class NengoLauncher {

    /**
     * Runs NengoGraphics with a default name
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Nengo");
        Application application = new DefaultApplication();
     
        NengoGraphics ng = new NengoGraphics();
        ng.setApplication(application);

    }
}
