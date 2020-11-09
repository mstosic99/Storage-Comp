package impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import api.ImplementorManager;
import api.StorageSpec;
import model.Entity;

public class CustomStorageImplementation extends StorageSpec{
	
	
	static {
		ImplementorManager.registerImplementor(new CustomStorageImplementation());
	}
	
	List<Entity> lista = new ArrayList<Entity>();
	
	
	
	@Override
	public void delete(int[] ids) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int id) throws IOException {
		// TODO Auto-generated method stub
		
	}
	private void deleteFromOneFileById(int id) throws IOException {
		
	}
	@Override
	public Entity read(int id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	private Entity readFromOneFileById(int id) throws IOException {
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
	private List<Entity> readCurrFile() throws IOException {
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

		int count=0;
		
		
		FileWriter fileWriter;
		
		try {

			try {
				FileReader fr = new FileReader(new File(currFileName));
				
				count = readCurrFile().size();
//				readOneFile(); 		// Pre svega ucitavamo postojece podatke iz json fajla (ako ne postoji fajl,
												// znaci da jos nije kreiran, pa se nista ne desava
				fr.close();
			} catch (IOException e) {
			}
			
			if(count >= cap) {
				File dir = new File(folderName);
				int fileCount = dir.listFiles().length + 1;
				if(!dir.isDirectory() || !dir.getName().endsWith(".custom"))
					throw new IllegalArgumentException();
				File newFile = new File(dir.getAbsolutePath()+"\\test" + fileCount + ".custom");
				currFileName = newFile.getAbsolutePath();
				
				
			}

			fileWriter = new FileWriter(new File(currFileName));

			
			fileWriter.close();
			
			System.out.println(count);

			

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void save(List<Entity> entities) {
		// TODO Auto-generated method stub
		
	}

}
