package api;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Entity;

public abstract class StorageSpec {
	
	protected String folderName;
	protected int cap = 50;
	protected String currFileName;
	
	private boolean isAutoIncrement; // TODO mogucnost izbora
	
	public abstract void runDB(String fileName) throws Exception; //unutar implementacije proveriti da li se file zavrsava (.xxxx) sa dobrim nastavkom 
	//
	
	public abstract void save(Entity entity) throws IOException;
	
	public abstract void save(List<Entity> entities);
	
	public void save(int id, String name, Map<String, String> properties, Map<String, List<Entity>> subEntities) throws IOException {
		
		if(isAutoIncrement) {
			System.out.println("Id je ignorisan(Autoincrement)");
			Entity entity = new Entity(name, properties, subEntities); // properties ili subEntities moze da bude null (handluje svaki slucaj)
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
	
	public List<Entity> findSpecial(String string){
		List<Entity> listaRezultata = new ArrayList<Entity>();
				//TODO
		return listaRezultata;
	}
	
	public List<Entity> sortSpecial(String string){
		List<Entity> listaRezultata = new ArrayList<Entity>();
		//TODO
		return listaRezultata;
	}
	
	public void deleteSpecial(String string) throws Exception{
		List<Integer> listaRezultata = new ArrayList<Integer>();
		List<Entity> lista = readAll();
		//TODO
		
		for (Integer i : listaRezultata) {
			delete(i);
		}
	}
	
	
	public abstract void delete(int[] ids) throws IOException;
	
	public abstract void delete(int id) throws IOException;
	
	public void loadUsedIDs() {
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

	public void setCap(int cap) {
		this.cap = cap;
	}

	public String getCurrFileName() {
		return currFileName;
	}

	public void setCurrFileName(String currFileName) {
		this.currFileName = currFileName;
	}

	
	
	
}
