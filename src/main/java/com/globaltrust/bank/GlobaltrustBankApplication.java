package com.globaltrust.bank;

import com.globaltrust.bank.frontend.ui.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class GlobaltrustBankApplication {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater(() -> {
			new LoginFrame().setVisible(true);
		});
	}

}
