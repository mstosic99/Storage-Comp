package impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;

import api.ImplementorManager;
import api.StorageSpec;
import model.Entity;

public class JsonStorageImplementation extends StorageSpec {

	static {
		ImplementorManager.registerImplementor(new JsonStorageImplementation());
	}

	private Gson gson = new Gson();
	JsonArray jArray = new JsonArray();

	@Override
	public void save(Entity entity) {

		FileWriter fileWriter;

		try {

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
		
		FileReader reader = new FileReader(new File(fileName));
		JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
		JsonArray jArray = (JsonArray) jsonElement;

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


		for (JsonElement element : jArray) {

			JsonObject object = (JsonObject) element;
			Entity entity = new Entity();

			JsonPrimitive jp = object.getAsJsonPrimitive("id");
			entity.setId(jp.getAsInt());

			jp = object.getAsJsonPrimitive("naziv");
			entity.setNaziv(jp.getAsString());


			for (Entry<String, JsonElement> entry : object.entrySet()) {
				String attributeKey = entry.getKey();
				JsonElement attributeValue = (JsonElement) entry.getValue();
				
				if(attributeValue.isJsonArray()) {
					
					JsonArray array = (JsonArray) attributeValue;
					List<Entity> primEntities = convertFromJsonArrayPrimitiveEntity(array);
					entity.addSubEntity(attributeKey, primEntities);
					
				} else if(attributeValue.isJsonPrimitive()) {
					
					if(!(attributeKey.toString().equals("naziv") || attributeKey.toString().equals("id"))) {
						String s = attributeValue.toString();
						s = s.substring(1, s.length()-1); 			// Brisanje " znaka sa pocetka i kraja
						entity.addProperty(attributeKey, s);
					}
				}

			}

			System.out.println(entity.toString());
//			System.out.println("*************************************************************");
//			System.out.println(object.getAsJsonObject().toString());
			
			if(entity.getProperties().isEmpty()) 
				entity.setProperties(null);
			if(entity.getSubEntities().isEmpty())
				entity.setSubEntities(null);
			
			entities.add(entity);
		}

		
		reader.close();
		return entities;
	}

	@Override
	public List<Entity> read(int[] ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity update(Entity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(int[] ids) {

	}

//	private String beautifyJson(String json) throws IOException {
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//		JsonNode tree = objectMapper.readTree(json);
//		String formattedJson = objectMapper.writeValueAsString(tree);
//
//		return formattedJson;
//		
//		
//	}
	
	private List<Entity> convertFromJsonArrayPrimitiveEntity(JsonArray jArray) {
		
		List<Entity> toReturn = new ArrayList<Entity>();
		for(JsonElement element : jArray) {
			
			JsonObject object = (JsonObject) element;
			Entity entity = new Entity();

			JsonPrimitive jp = object.getAsJsonPrimitive("id");
			entity.setId(jp.getAsInt());

			jp = object.getAsJsonPrimitive("naziv");
			entity.setNaziv(jp.getAsString());
			
			for(Entry<String, JsonElement> entry : object.entrySet()) {
				String attributeKey = entry.getKey();
				JsonElement attributeValue = entry.getValue();
				
				if(attributeValue.isJsonPrimitive()) {
					if(!(attributeKey.toString().equals("naziv") || attributeKey.toString().equals("id"))) {
						String s = attributeValue.toString();
						s = s.substring(1, s.length()-1); 			// Brisanje " znaka sa pocetka i kraja
						entity.addProperty(attributeKey, s);
					}
				}
				
			}
			if(entity.getProperties().isEmpty()) 
				entity.setProperties(null);
			if(entity.getSubEntities().isEmpty())
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
