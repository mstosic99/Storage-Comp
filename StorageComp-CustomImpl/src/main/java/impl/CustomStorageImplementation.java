package impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import api.ImplementorManager;
import api.StorageSpec;
import model.Entity;

public class CustomStorageImplementation extends StorageSpec{
	static {
		ImplementorManager.registerImplementor(new CustomStorageImplementation());
	}
	@Override
	public void delete(int[] ids) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int id) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Entity read(int id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> read(int[] ids) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> readAll() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void runDB(String folderName) throws Exception {
		File dir = new File(folderName);
		try {
		
			if(!dir.isDirectory() || !dir.getName().endsWith(".custom"))
				throw new IllegalArgumentException();
		} catch (IllegalArgumentException e) {
			System.err.println("Ubacili ste pogresan JAR, RUNTIME DEPENDENCY FAILED");
			System.exit(0);
		}
		
				
		if(dir.listFiles().length == 0) {
			int fileCount = dir.listFiles().length + 1;
			File file = new File(dir.getAbsolutePath()+"\\test" + fileCount + ".custom");
			currFileName = file.getAbsolutePath();
		} else {
			File[] files = dir.listFiles();
			currFileName = files[files.length-1].getAbsolutePath();
		}
		
		loadUsedIDs();
		
	}

	@Override
	public void save(Entity entity) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(List<Entity> entities) {
		// TODO Auto-generated method stub
		
	}

}
