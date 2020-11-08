package impl;

import java.util.ArrayList;
import java.util.List;

import model.Entity;

public class EntityWrapper {
	
	private List<Entity> entities;
	
	public EntityWrapper() {
		entities = new ArrayList<>();
	}
	
	public EntityWrapper(List<Entity> entities) {
		this.entities = entities;
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
}
