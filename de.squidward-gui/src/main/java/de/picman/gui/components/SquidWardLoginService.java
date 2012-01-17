package de.picman.gui.components;

import org.jdesktop.swingx.auth.LoginService;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.User;


public class SquidWardLoginService extends LoginService {


	
	public SquidWardLoginService() {
	}

	@Override
	public boolean authenticate(String name, char[] password, String server)
			throws Exception {

		String pass = new String(password);
		User user = ApplicationControl.getInstance().login(name, pass);
		return (user != null);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SquidWardLoginService squidWardLoginService = new SquidWardLoginService();
		
		char[] pass = {'g','e','h','e','i','m'};
		boolean result = false;
		try {
			result = squidWardLoginService.authenticate("jens", pass, "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(result);
		
	}

}
