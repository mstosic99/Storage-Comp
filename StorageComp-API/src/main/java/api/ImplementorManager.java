package api;


public class ImplementorManager {

private static StorageSpec storageSpec;
	
	public static void registerImplementor(StorageSpec spec) {
		storageSpec = spec;		
	}
	
	public static StorageSpec getStorageSpec(String fileName) {
		storageSpec.setFileName(fileName);
		return storageSpec;
	}
	
}
