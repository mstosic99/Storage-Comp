package model;

import java.util.Map;
//import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;


public class Entity {

	private static AtomicLong idIncrementing = new AtomicLong(); // Npr za gson ovaj atribut treba da se excluduje za serijalizaciju
	private int id;
	private String naziv;
	private Map<String, String> properties = null;
	private Map<String, Entity> subEntities = null;

	public Entity(String naziv, Map<String, String> properties, Map<String, Entity> subEntities) {

		this.naziv = naziv;
		this.properties = properties;
		this.subEntities = subEntities;
		
		generateAutoIncrementID();

	}
	
	public Entity(int id, String naziv, Map<String, String> properties, Map<String, Entity> subEntities) {
		
		// TODO dodavanje ID od strane korisnika, potrebna provera jedinstvenosti
		this.id = id;
		this.naziv = naziv;
		this.properties = properties;
		this.subEntities = subEntities;
		
	}
	
	public void addAttributes(Map<String, String> attributes) {
		this.properties = attributes;
	}
	
	public void addSubEntities(Map<String, Entity> subEntities) {
		this.subEntities = subEntities;
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

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
	
	public Map<String, Entity> getSubEntities() {
		return subEntities;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
}
