package api;


import java.util.List;

import model.Entity;

public abstract class StorageExport {
	
	protected String fileName;
	
	public abstract void save(Entity entity);
	
	public abstract void save(List<Entity> entities);
	
	
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
