package ca.nengo.ui;

public class NengoLauncher {

    /**
     * Runs NengoGraphics with a default name
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Nengo");
        new NengoGraphics();
    }
}
