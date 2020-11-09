package gui;

import api.ImplementorManager;
import api.StorageSpec;

public class MainApp {
	
	static StorageSpec spec;
	
	public static void main(String[] args) {
		try {
			//Class.forName("impl.JsonStorageImplementation");
			Class.forName("impl.YamlStorageImplementation");
			//Class.forName("impl.CustomStorageImplementation");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		spec = ImplementorManager.getStorageSpec();
		MainFrame mainFrame = MainFrame.getInstance();
		mainFrame.maximize();
		mainFrame.createUI(mainFrame);		
	}
	
	
	
	
	
}
