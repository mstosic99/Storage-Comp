package impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;

import api.ImplementorManager;
import api.StorageSpec;
import model.Entity;

public class JsonStorageImplementation extends StorageSpec {

	static {
		String filePath = new File("").getAbsolutePath();
//		filePath = filePath.substring(0, filePath.length() - 21); // Uzasna linija pls ignore hahahh
		filePath += "\\src\\test\\resources\\test1.json";
		ImplementorManager.registerImplementor(new JsonStorageImplementation(filePath));
	}

	private Gson gson = new Gson();
	private JsonArray jArray = new JsonArray();

	public JsonStorageImplementation(String filePath) {
		
		this.fileName = filePath;
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
	
	@Override
	public void save(Entity entity) {

		FileWriter fileWriter;

		try {

			try {
				FileReader fr = new FileReader(new File(fileName));
				readAll(); // Pre svega ucitavamo postojece podatke iz json fajla (ako ne postoji fajl,
							// znaci da jos nije kreiran, pa se nista ne desava
				fr.close();
			} catch (IOException e) {
			}

			fileWriter = new FileWriter(new File(fileName));

			JsonObject object = new JsonObject();

			object.addProperty("naziv", entity.getNaziv());
			object.addProperty("id", entity.getId());

			if (!entity.getProperties().isEmpty()) {
				for (HashMap.Entry<String, String> entry : entity.getProperties().entrySet()) {
					object.addProperty(entry.getKey(), entry.getValue());
				}
			}

			if (!entity.getSubEntities().isEmpty()) {
				// Prolazimo kroz sva polja koja kao value sadrze ugnjezdene entitete(moze ih
				// biti vise, ali na istom hijerarhijskom nivou)
				for (HashMap.Entry<String, List<Entity>> subEntitiesPack : entity.getSubEntities().entrySet()) {

					JsonArray internalArray = new JsonArray();

					// Prolazimo kroz listu ugnjezdenih entiteta, da bi ih dodali u JsonArray
					for (Entity subEntity : subEntitiesPack.getValue()) {

						JsonObject internalObject = new JsonObject();

						internalObject.addProperty("naziv", subEntity.getNaziv());
						internalObject.addProperty("id", subEntity.getId());

						// Za svaki entitet prolazimo kroz njegove propertije (samo raw stringovi)
						for (HashMap.Entry<String, String> properties : subEntity.getProperties().entrySet())
							internalObject.addProperty(properties.getKey(), properties.getValue());
						internalArray.add(internalObject);

					}
					object.add(subEntitiesPack.getKey(), internalArray);
				}
			}
			jArray.add(object);

			fileWriter.write(beautifyJson(jArray.toString()));
			fileWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void save(List<Entity> entities) {

		for (Entity entity : entities) {
			save(entity);
		}

	}

	@Override
	public List<Entity> readAll() throws IOException {

		List<Entity> entities = new ArrayList<Entity>();

		FileReader reader = null;
		try {
			reader = new FileReader(new File(fileName));
		} catch (Exception e) {
			return null;
		}
		JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
		JsonArray jsonArray = (JsonArray) jsonElement;
		jArray = jsonArray;

//		ArrayList<String> listdata = new ArrayList<String>();

//		if (jArray != null) {
//			for (int i = 0; i < jArray.size(); i++) {
//				listdata.add(jArray.get(i).toString());
//			}
//		}

//		for(String s : listdata) {
//			Entity e = gson.fromJson(s, Entity.class);
//			System.out.println(e.toString());
//		}

		for (JsonElement element : jsonArray) {

			JsonObject object = (JsonObject) element;
			Entity entity = new Entity();

			JsonPrimitive jp = object.getAsJsonPrimitive("id");
			entity.setId(jp.getAsInt());

			jp = object.getAsJsonPrimitive("naziv");
			entity.setNaziv(jp.getAsString());

			for (Entry<String, JsonElement> entry : object.entrySet()) {
				String attributeKey = entry.getKey();
				JsonElement attributeValue = (JsonElement) entry.getValue();

				if (attributeValue.isJsonArray()) {

					JsonArray array = (JsonArray) attributeValue;
					List<Entity> primEntities = convertFromJsonArrayPrimitiveEntity(array);
					entity.addSubEntity(attributeKey, primEntities);

				} else if (attributeValue.isJsonPrimitive()) {

					if (!(attributeKey.toString().equals("naziv") || attributeKey.toString().equals("id"))) {
						String s = attributeValue.toString();
						s = s.substring(1, s.length() - 1); // Brisanje " znaka sa pocetka i kraja
						entity.addProperty(attributeKey, s);
					}
				}

			}


			if (entity.getProperties().isEmpty())
				entity.setProperties(null);
			if (entity.getSubEntities().isEmpty())
				entity.setSubEntities(null);

			entities.add(entity);
		}

		reader.close();
		return entities;
	}

	@Override
	public List<Entity> read(int[] ids) throws IOException {

		List<Entity> entities = new ArrayList<>();
		
		for(int i = 0; i < ids.length; i++) {
			Entity e = null;
			e = read(ids[i]);
			if(e != null) 
				entities.add(e);
		}
		
		if(entities.isEmpty()) {
			return null;
		}
		return entities;
	}

	@Override
	public Entity read(int id) throws IOException {

		Entity toReturn = null;
		List<Entity> entities = new ArrayList<Entity>();
		
		entities = readAll();
		for(Entity entity : entities) {
			if(entity.getId() == id)
				toReturn = entity;
			for(HashMap.Entry<String, List<Entity>> subEntity : entity.getSubEntities().entrySet()) {
				for(Entity e : subEntity.getValue()) {
					if(e.getId() == id)
						toReturn = e;
				}
			}
				
		}
		
		if(toReturn == null)
			System.err.println("Id "+ id +" ne postoji.");
		
		return toReturn;
	}

	@Override
	public Entity update(Entity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(int id) throws IOException {
		
		FileReader reader = null;
		try {
			reader = new FileReader(new File(fileName));
		} catch (Exception e) {
			return;
		}
		JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
		JsonArray jsonArray = (JsonArray) jsonElement;
		
		for (JsonElement element : jsonArray) {

			JsonObject object = (JsonObject) element;
			JsonPrimitive jp = object.getAsJsonPrimitive("id");
			if(jp.getAsInt() == id) {
				jsonArray.remove(element);
				FileWriter fw = new FileWriter(new File(fileName));
				fw.write(beautifyJson(jsonArray.toString()));
				fw.close();
				return;
			}

			jp = object.getAsJsonPrimitive("naziv");

			for (Entry<String, JsonElement> entry : object.entrySet()) {
				JsonElement attributeValue = entry.getValue();

				if (attributeValue.isJsonArray()) {
					JsonArray ar = (JsonArray) attributeValue;
					for(JsonElement e : ar) {
						JsonObject o = (JsonObject) e;
						for (Entry<String, JsonElement> en : o.entrySet()) {
							String attributeKey1 = en.getKey();
							JsonElement attributeValue1 = en.getValue();

							if (attributeValue1.isJsonPrimitive()) {
								if (attributeKey1.toString().equals("id")) {
									if(attributeValue1.getAsInt() == id) {
										ar.remove(e);
										FileWriter fw = new FileWriter(new File(fileName));
										fw.write(beautifyJson(jsonArray.toString()));
										fw.close();
										return;
									}
												
								}
							}

						}
					}
				}
			}
		}
		
	}
	
	@Override
	public void delete(int[] ids) throws IOException {
		
	}


	private List<Entity> convertFromJsonArrayPrimitiveEntity(JsonArray jsonArray) {

		List<Entity> toReturn = new ArrayList<Entity>();
		for (JsonElement element : jsonArray) {

			JsonObject object = (JsonObject) element;
			Entity entity = new Entity();

			JsonPrimitive jp = object.getAsJsonPrimitive("id");
			entity.setId(jp.getAsInt());

			jp = object.getAsJsonPrimitive("naziv");
			entity.setNaziv(jp.getAsString());

			for (Entry<String, JsonElement> entry : object.entrySet()) {
				String attributeKey = entry.getKey();
				JsonElement attributeValue = entry.getValue();

				if (attributeValue.isJsonPrimitive()) {
					if (!(attributeKey.toString().equals("naziv") || attributeKey.toString().equals("id"))) {
						String s = attributeValue.toString();
						s = s.substring(1, s.length() - 1); // Brisanje " znaka sa pocetka i kraja
						entity.addProperty(attributeKey, s);
					}
				}

			}
			if (entity.getProperties().isEmpty())
				entity.setProperties(null);
			if (entity.getSubEntities().isEmpty())
				entity.setSubEntities(null);

			toReturn.add(entity);
		}

		return toReturn;
	}

	private String beautifyJson(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Object obj = mapper.readValue(json, Object.class);
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
	}

}
