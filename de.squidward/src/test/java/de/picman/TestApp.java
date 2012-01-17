package de.picman;

import java.awt.Desktop;
import java.awt.SplashScreen;
import java.net.URI;

import de.picman.backend.control.ApplicationControl;

public class TestApp {
	public static void main(String[] args) {
		testSplash();
	}

	public static void testSplash(){
			SplashScreen splashScreen = SplashScreen.getSplashScreen();
			System.out.println("xx" + splashScreen);
	}
	
	
	public static void testMail(){
		try {

			String mailto = "mail@jensd.de";
			String subject = "TEST";
			String body ="Klappt!";
			String uriStr = "mailto:" + mailto + "?subject="+ subject+"&body=" + body + "&attachment='file:///C:/EclipseWorkspace/PicMan/venedig.png'";
			
			Desktop.getDesktop().mail(new URI(uriStr));
		} catch (Exception e1) {
			ApplicationControl.displayErrorToUser(e1);
		}
	}
}
