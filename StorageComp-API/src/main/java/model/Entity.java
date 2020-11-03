package model;

import java.util.Map;
//import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Entity {

	private static AtomicLong id = new AtomicLong();
	private String naziv;
	private Map<String, String> attributes;
	private Map<String, Entity> subEntities;

	public Entity(String naziv) {

		this.naziv = naziv;
		generateAutoIncrementID();

	}
	
	public void addAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public void addSubEntities(Map<String, Entity> subEntities) {
		this.subEntities = subEntities;
	}

	public Entity(String naziv, String id) {

		this.naziv = naziv;
		// TODO dodavanje ID od strane korisnika, potrebna provera jedinstvenosti

	}

	
	// Nije human-readable (String)
//	private void generateAndSetID() {
//		this.id = UUID.randomUUID().toString();
//	}


	private static String generateAutoIncrementID() {
		return String.valueOf(id.getAndIncrement());
	}

	public long getId() {
		return id.longValue();
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
	
	public Map<String, String> getAttributes() {
		return attributes;
	}
}
