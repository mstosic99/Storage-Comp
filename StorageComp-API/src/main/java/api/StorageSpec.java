package api;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import model.Entity;

public abstract class StorageSpec {
	
	protected String fileName;
	
	private boolean isAutoIncrement; // TODO mogucnost izbora
	
	
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
	
	public abstract Entity update(Entity entity);
	
	public abstract void delete(int[] ids) throws IOException;
	
	public abstract void delete(int id) throws IOException;
		
	
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}
	
	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}
	
}
