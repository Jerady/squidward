package de.picman;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class StartMailCient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			String mailto = "mail@jensd.de";
			String subject = "TEST";
			String body ="Klappt!";
			String uriStr = "mailto:" + mailto + "?subject="+ subject+"&body=" + body;
			Desktop.getDesktop().mail(new URI(uriStr));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

}
