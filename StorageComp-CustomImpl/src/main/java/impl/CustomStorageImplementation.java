package impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		for (int i = 0; i < ids.length; i++) {
			delete(ids[i]);
		}
	}

	@Override
	public void delete(int id) throws IOException {
		// TODO Auto-generated method stub
		lista = readAll();
		Entity k = null;
		for (Entity en : lista) {
			if (en.getId() == id) {
				k = en;
				break;
			}
		}
		if (k!= null) {
			lista.remove(k);
		}
		
		File dir = new File(folderName);
		try {
		
			if(!dir.isDirectory() || !dir.getName().endsWith(".custom"))
				throw new IllegalArgumentException();
		} catch (IllegalArgumentException e) {
			System.err.println("Ubacili ste pogresan JAR, RUNTIME DEPENDENCY FAILED");
			System.exit(0);
		}
		for(File file: dir.listFiles()) 
		    if (!file.isDirectory()) {
		    	file.delete();
		    }
		        
		loadUsedIDs();
		save(lista);
		
	}
	private void deleteFromOneFileById(int id) throws IOException {
		
	}
	@Override
	public Entity read(int id) throws IOException {
		lista = readAll();
		for (Entity en : lista) {
			if (en.getId() == id) {
				return en;
			}
		}
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
		List<Entity> toReturn = new ArrayList<>();
		
		StringBuilder currFileBuffer = new StringBuilder(currFileName);
		File dir = new File(folderName);
		File[] files = dir.listFiles();
		if(files.length != 0) {
			for(File file : files) {
				if(file.getAbsolutePath().endsWith(".custom")) {
					currFileName = file.getAbsolutePath();
					List<Entity> lista = readCurrFile();
					if(lista != null) {
						toReturn.addAll(lista);
					}
				}
			}
		}
		currFileName = currFileBuffer.toString();
		
		
		
		return toReturn;
		
	}
	
	
	private List<Entity> readCurrFile() throws IOException {
		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> listaPod = new ArrayList<Entity>();
		HashMap<String, List<Entity>> podentiteti = new HashMap<String,List<Entity>>();
		FileReader readerF = null;
		try {
			readerF = new FileReader(new File(currFileName));
		} catch (Exception e) {
			return null;
		}
		BufferedReader reader = new BufferedReader(readerF);
		String str;
		
		while((str=reader.readLine())!=null) {
			
			String line = reader.readLine();
			System.out.println(line);
			String[] idSplit = line.split(":");
			String id = idSplit[1];
			line = reader.readLine();
			System.out.println(line);
			String[] nameSplit = line.split(":");
			String name = nameSplit[1];
			line = reader.readLine();
			
			HashMap<String, List<Entity>> test1 = new HashMap<String, List<Entity>>();
			HashMap<String, String> mapa = new HashMap<String,String>();
			while (!(line.equals("end."))) {
				Entity subEntity = null;
				String key = null;
				
				if (line.equals("subs")) {
					key = reader.readLine();
					line = reader.readLine();
					
				
				
						while (line.equals("sub")) {
							line = reader.readLine();
							System.out.println(line);
							String[] idSplit1 = line.split(":");
							String id1 = idSplit1[1];
							line = reader.readLine();
							System.out.println(line);
							String[] nameSplit1 = line.split(":");
							String name1 = nameSplit1[1];
							line = reader.readLine();
							Map<String,String> mapa1 = new HashMap<String,String>();
							while (!(line.equals("end,"))) {
								String[] split1 = line.split(":");
								mapa1.put(split1[0], split1[1]);
								line = reader.readLine();
							}
							line = reader.readLine();
							HashMap<String, List<Entity>> mapa2 = new HashMap<String,List<Entity>>();
							subEntity = new Entity(name1, mapa1, mapa2);
							subEntity.setId(Integer.parseInt(id1));
							listaPod.add(subEntity);
						}
						List<Entity> listaBrza = new ArrayList<Entity>(listaPod);
						listaPod.clear();
						test1.put(key, listaBrza);
						System.out.println(line);
				}else {

					String[] split = line.split(":");
					System.out.println(line);
					mapa.put(split[0], split[1]);
					line = reader.readLine();
				}
				
			}
			
			HashMap<String, List<Entity>> mapa1 = new HashMap<String,List<Entity>>(test1);
			Entity ent = new Entity(name,mapa,mapa1);
			test1.clear();
			ent.setId(Integer.parseInt(id));
			entities.add(ent);
		}
		reader.close();
		readerF.close();
		return entities;
		
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

			fileWriter = new FileWriter(new File(currFileName),true);
			fileWriter.write("begin");
			fileWriter.write(System.getProperty( "line.separator" ));
			fileWriter.write("id:" + entity.getId());
			fileWriter.write(System.getProperty( "line.separator" ));
			fileWriter.write("naziv:" + entity.getNaziv());
			fileWriter.write(System.getProperty( "line.separator" ));
			for (Entry<String, String> entry : entity.getProperties().entrySet()) {
			    fileWriter.write(entry.getKey()+ ":"+ entry.getValue());
			    fileWriter.write(System.getProperty( "line.separator" ));
			}
			
			for (Entry<String, List<Entity>> entry : entity.getSubEntities().entrySet()) {
				
				
				fileWriter.write( "subs");
				fileWriter.write(System.getProperty( "line.separator" ));
				fileWriter.write(entry.getKey());
				fileWriter.write(System.getProperty( "line.separator" ));
				
			    for (Entity ent : entry.getValue()) {
			    	fileWriter.write( "sub");
			    	fileWriter.write(System.getProperty( "line.separator" ));
			    	fileWriter.write("id:" + ent.getId());
					fileWriter.write(System.getProperty( "line.separator" ));
					fileWriter.write("naziv:" + ent.getNaziv());
					fileWriter.write(System.getProperty( "line.separator" ));
					for (Entry<String, String> entry1 : ent.getProperties().entrySet()) {
					    fileWriter.write(entry1.getKey()+ ":"+ entry1.getValue());
					    fileWriter.write(System.getProperty( "line.separator" ));
					}
					fileWriter.write("naziv:" + entity.getNaziv());
					fileWriter.write(System.getProperty( "line.separator" ));
					fileWriter.write("end,");
					fileWriter.write(System.getProperty( "line.separator" ));
			    }
			    
			}

			fileWriter.write("end.");
			fileWriter.write(System.getProperty( "line.separator" ));
			fileWriter.close();
			
			System.out.println(count);

			

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void save(List<Entity> entities) {
		// TODO Auto-generated method stub
		for (Entity en : entities) {
			try {
				save(en);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
