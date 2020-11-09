package gui;

import api.ImplementorManager;
import api.StorageSpec;

public class MainApp {
	
	static StorageSpec spec;
	
	public static void main(String[] args) {
		
		spec = ImplementorManager.getStorageSpec();
		MainFrame mainFrame = MainFrame.getInstance();
		mainFrame.maximize();
		mainFrame.createUI(mainFrame);		
	}
	
	
	
	
	
}
