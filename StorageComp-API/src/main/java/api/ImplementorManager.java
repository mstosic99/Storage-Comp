package api;


public class ImplementorManager {

	private static StorageSpec storageSpec;
	
	public static void registerImplementor(StorageSpec spec) {
		storageSpec = spec;	
	}
	/**
	* Runtime dependency start
	* @return returns an instance of StorageSpec depending on your Implementation
	*/
	public static StorageSpec getStorageSpec() {
		return storageSpec;
	}
		
}
