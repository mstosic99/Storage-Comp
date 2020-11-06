package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;


public class Entity {

	

	private static AtomicLong idIncrementing = new AtomicLong(); // Npr za gson ovaj atribut treba da se excluduje za serijalizaciju
	private int id;
	private String naziv;
	private Map<String, String> properties = new HashMap<String, String>();
	private Map<String, List<Entity>> subEntities = new HashMap<String, List<Entity>>();
	
	public Entity() {
		
	}

	public Entity(String naziv, Map<String, String> properties, Map<String, List<Entity>> subEntities) {

		this.naziv = naziv;
		this.properties = properties;
		this.subEntities = subEntities;
		
		generateAutoIncrementID();

	}
	
	public Entity(int id, String naziv, Map<String, String> properties, Map<String, List<Entity>> subEntities) {
		
		// TODO dodavanje ID od strane korisnika, potrebna provera jedinstvenosti
		this.id = id;
		this.naziv = naziv;
		this.properties = properties;
		this.subEntities = subEntities;
		
	}
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public void setSubEntities(Map<String, List<Entity>> subEntities) {
		this.subEntities = subEntities;
	}
	
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}

	public void addSubEntity(String key, List<Entity> value) {
		subEntities.put(key, value);
	}
	
	//	Nije human-readable (String)
//	private void generateAndSetID() {
//		this.id = UUID.randomUUID().toString();
//	}


	private void generateAutoIncrementID() {
		long x = idIncrementing.getAndIncrement();
		this.id = (int) x;
	}

	public long getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
	
	public Map<String, List<Entity>> getSubEntities() {
		return subEntities;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
	
	@Override
	public String toString() {
		return "Entity [id=" + id + ", naziv=" + naziv + ", properties=" + properties + ", subEntities=" + subEntities
				+ "]";
	}
}
