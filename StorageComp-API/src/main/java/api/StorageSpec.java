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
	

	public abstract void runDB(String fileName) throws Exception; 

	
	public abstract void save(Entity entity) throws IOException;
	
	public abstract void save(List<Entity> entities);
	
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
	
	public abstract List<Entity> readAll() throws IOException;				// Ucitaj sve entitete
	
	public abstract Entity read(int id) throws IOException;
	
	public abstract List<Entity> read(int[] ids) throws IOException;  		// Ucitaj entitete za id
	
	public void update(Entity entity) throws Exception {
		delete(entity.getId());
		save(entity);
	}
	
	public void addSubEntityToEntity(int id, Entity subEntity, String key) throws Exception {
		Entity entity = read(id);
		List<Entity> value = new ArrayList<Entity>();
		value.add(subEntity);
		entity.addSubEntity(key, value);
		update(entity);
	}
	
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
		
		return listaRezultata;
	}
	
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
	
	
	public abstract void delete(int[] ids) throws IOException;
	
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
				
				for(HashMap.Entry<String, List<Entity>> subEntity : entity.getSubEntities().entrySet()) {
					for(Entity e : subEntity.getValue()) {
						Entity.getUsedIDs().add((Integer) e.getId());
					}
				}
			}
			
		}catch (Exception e) {
			System.err.println("Gadan Exception");
		}
	}
	
	
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
