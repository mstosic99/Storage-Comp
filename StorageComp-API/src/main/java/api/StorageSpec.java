package api;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import model.Entity;

public abstract class StorageSpec {
	
	protected String folderName;
	protected int cap = 50;
	protected String currFileName;
	
	private boolean isAutoIncrement;
	
	/**
	* Sets the current folder that the StorageSpec works with, and checks if the folder matches the Implementation version.
	* Exits the program if it does not.
	* @param folderName folder Name in String format (absolute path).
	*/
	public abstract void runDB(String folderName) throws Exception; 

	/**
	* Saves one Entity in database.
	* @param entity Entity that you want to be saved.
	*/
	public abstract void save(Entity entity) throws IOException;
	/**
	* Saves a list of Entity in database.
	* @param entities List of Entity that you want to be saved.
	*/
	public abstract void save(List<Entity> entities);
	
	/**
	* Creates an entity out of paramateres and saves it to DB.
	* @param id ID of Entity that you want to be saved.
	* @param name Name of Entity that you want to be saved.
	* @param properties Properties of Entity that you want to be saved.
	* @param subEntities subEntities of Entity that you want to be saved.
	*/
	public void save(int id, String name, Map<String, String> properties, Map<String, List<Entity>> subEntities) throws IOException {
		
		if(isAutoIncrement) {
			System.out.println("Id je ignorisan(Autoincrement)");
			Entity entity = new Entity(name, properties, subEntities); 
			save(entity);
			
		} else {
			Entity entity = new Entity(id, name, properties, subEntities);
			save(entity);
		}
		
		
	}
	/**
	* Reads all Entity in database.
	* @return returns a list of Entities that was read.
	*/
	public abstract List<Entity> readAll() throws IOException;				// Ucitaj sve entitete
	
	/**
	* Reads one Entity in database.
	* @param  id Entity id that you want to be read.
	* @return returns an Entity that was read.
	*/
	public abstract Entity read(int id) throws IOException;
	
	/**
	* Reads one Entity in database.
	* @param  ids Entity id list that you want to be read.
	* @return returns a list of Entities that was read.
	*/
	public abstract List<Entity> read(int[] ids) throws IOException;  		// Ucitaj entitete za id
	
	/**
	* Updates the entity in the DB
	* @param  entity Entity id list that you want to be read.
	*/
	public void update(Entity entity) throws Exception {
		delete(entity.getId());
		save(entity);
	}
	
	/**
	* Adds a subentity to an Entity that you know ID of
	* @param  id Entity id list that you want to be read.
	* @param  subEntity Subentity that you want to put.
	* @param  key key for that subentity.
	*/
	public void addSubEntityToEntity(int id, Entity subEntity, String key) throws Exception {
		Entity entity = read(id);
		List<Entity> value = new ArrayList<Entity>();
		value.add(subEntity);
		if (entity.getSubEntities() != null) {
			List<Entity> lista = entity.getSubEntities().get(key);
			if (lista == null) {
				entity.getSubEntities().put(key, value);
			}else {
				lista.add(entity);
				entity.getSubEntities().put(key, lista);
			}
		}else {
			Map<String, List<Entity>> mapa = new HashMap<String, List<Entity>>();
			mapa.put(key, value);
			entity.setSubEntities(mapa);
		}
		
		update(entity);
	}
	
	/**
	* Returns a list of Entity that have that name.
	* @param  name Entity name that you want to find.
	* @return returns a List of all Entity objects that have that name in DB
	*/
	public List<Entity> findByName(String name) throws Exception{
		List<Entity> lista = readAll();
		List<Entity> listaRezultata = new ArrayList<Entity>();
		for (Entity en : lista) {
			if (en.getNaziv().equals(name)) {
				listaRezultata.add(en);
			}
		}
		
		return listaRezultata;
	}
	/**
	* Returns a list of Entity with that key value pair
	* @param  key Entity name that you want to find.
	* @param  value Entity name that you want to find.
	* @return List of all Entity objects that have that key:value combo in DB
	*/
	public List<Entity> findByKeyValue(String key, String value) throws Exception{
		List<Entity> lista = readAll();
		List<Entity> listaRezultata = new ArrayList<Entity>();
		for (Entity en : lista) {
			if (en.getProperties().get(key).equals(value)) {
				listaRezultata.add(en);
			}
		}
		
		return listaRezultata;
	}
	
	/**
	* Returns a list of Entity that were found. The format for this is key=value (and key=value)
	* @param  string Entity keyvalue pairs that you want to find.
	* @return List of all Entity objects that have that key:value combo in DB
	*/
	public List<Entity> findSpecial(String string) throws Exception{
		
		List<Entity> lista = readAll();
		
		List<String> tokens = new ArrayList<>(Arrays.asList(string.split(" and ")));
		String[][] doubleTokens = new String[1000][1000];
		int i = 0;
		for (; i < tokens.size(); i++) {
			doubleTokens[i] = tokens.get(i).split("=");
		}
		List<Entity> zaBrisanje = new ArrayList<Entity>();
		
		for (int j = 0; j < i; j++) {
			for (Entity en : lista) {
				if (!(en.getProperties().get(doubleTokens[j][0]).startsWith(doubleTokens[j][1]))) {
					zaBrisanje.add(en);
				}
			}
		}
		
		for(Entity ent : zaBrisanje) {
			lista.remove(ent);
		}
		
		return lista;
	}
	
	/**
	* Returns a sorted list of Entity that were found containing that key.
	* @param  key Key by which you are sorting
	* @param  rising True for rising, False for falling order
	* @return Sorted list of entities containing that key
	*/
	public List<Entity> sortSpecial(String key, Boolean rising) throws Exception{
		List<Entity> listaRezultata = new ArrayList<Entity>();
		List<Entity> lista = readAll();
		Map<Integer, String> mapa = new HashMap<Integer, String>();
		List<Entity> zaBrisanje = new ArrayList<Entity>();
		for (Entity ent : lista) {
			if (ent.getProperties().get(key) == null) {
				zaBrisanje.add(ent);
			}
		}
		for(Entity ent : zaBrisanje) {
			lista.remove(ent);
		}
		for (Entity en : lista) {
			mapa.put(Integer.valueOf(en.getId()),en.getProperties().get(key));
		}
		
		Map<Integer, String> sortedMap = 
			     mapa.entrySet().stream()
			    .sorted(Entry.comparingByValue())
			    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
			                              (e1, e2) -> e1, LinkedHashMap::new));
		
		for(Entry<Integer, String> entry : sortedMap.entrySet()) {
		    Integer id = entry.getKey();
		    String value = entry.getValue();
		    listaRezultata.add(read(id));
		}
		
		if (!(rising)) {
			Collections.reverse(listaRezultata);
		}
		return listaRezultata;
	}
	/**
	* Deletes all Entity that were found. The format for this is key=value (and key=value)
	* @param  string Entity keyvalue pairs that you want to delete.
	*/
	public void deleteSpecial(String string) throws Exception{
		List<Integer> listaRezultata = new ArrayList<Integer>();
		List<Entity> lista = readAll();
		List<Entity> zaBrisanje = new ArrayList<Entity>();
		List<String> tokens = new ArrayList<>(Arrays.asList(string.split(" and ")));
		String[][] doubleTokens = new String[1000][1000];
		int i = 0;
		for (; i < tokens.size(); i++) {
			doubleTokens[i] = tokens.get(i).split("=");
		}
		
		for (int j = 0; j < i; j++) {
			for (Entity en : lista) {
				if (!(en.getProperties().get(doubleTokens[j][0]).startsWith(doubleTokens[j][1]))) {
					zaBrisanje.add(en);
				}
			}
		}
		for(Entity ent : zaBrisanje) {
			lista.remove(ent);
		}
		for (Entity ent : lista) {
			listaRezultata.add(ent.getId());
		}
		
		for (Integer inte : listaRezultata) {
			delete(inte);
		}
	}
	
	/**
	* Deletes all Entity that were found with these IDs
	* @param  ids List of IDs that you want deleted
	*/
	public abstract void delete(int[] ids) throws IOException;
	/**
	* Deletesa all Entity that were found with this ID.
	* @param  id ID by which to delete
	*/
	public abstract void delete(int id) throws IOException;
	
	protected void loadUsedIDs() {
		//citanje iz svih fajlova unutar foldera
		
		try {
			List<Entity> entities = readAll();
			if(entities == null)
				throw new NullPointerException();
			
			for(Entity entity : entities) {
				Entity.getUsedIDs().add((Integer) entity.getId());
				for(HashMap.Entry<String, String> property : entity.getProperties().entrySet()) {
					if(property.getKey().equals("id"))
						Entity.getUsedIDs().add(Integer.parseInt(property.getValue()));
				}
				if(entity.getSubEntities() != null) {
					for(HashMap.Entry<String, List<Entity>> subEntity : entity.getSubEntities().entrySet()) {
						for(Entity e : subEntity.getValue()) {
							Entity.getUsedIDs().add((Integer) e.getId());
						}
					}
				}
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("Gadan Exception");
		}
	}
	
	/**
	* Starts the database for this folder
	* @param folderName absolutePath of folder
	*/
	public void setFolderNameAndStart(String folderName) throws Exception {
		this.folderName = folderName;
		runDB(folderName);
	}
	
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}


	public void setCap(int cap) {
		this.cap = cap;
	}


	public String getFolderName() {
		return folderName;
	}
	
	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}
	
	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}

	public int getCap() {
		return cap;
	}



	public String getCurrFileName() {
		return currFileName;
	}

	public void setCurrFileName(String currFileName) {
		this.currFileName = currFileName;
	}


	
}
