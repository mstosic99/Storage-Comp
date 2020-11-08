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
		ImplementorManager.registerImplementor(new JsonStorageImplementation());
	}

	private Gson gson = new Gson();
	private JsonArray jArray = new JsonArray();
	
	
	@Override
	public void save(Entity entity) {
		//ako u currFile ima preko 50 entiteta napravi novi fajl
		
		int count=0;
		
		
		FileWriter fileWriter;
		
		try {

			try {
				FileReader fr = new FileReader(new File(currFileName));
				
				count = readCurrFile().size();
//				readOneFile(); 		// Pre svega ucitavamo postojece podatke iz json fajla (ako ne postoji fajl,
												// znaci da jos nije kreiran, pa se nista ne desava
				fr.close();
			} catch (IOException e) {
			}
			
			if(count >= cap) {
				File dir = new File(folderName);
				int fileCount = dir.listFiles().length + 1;
				if(!dir.isDirectory() || !dir.getName().endsWith(".json"))
					throw new IllegalArgumentException();
				File newFile = new File(dir.getAbsolutePath()+"\\test" + fileCount + ".json");
				currFileName = newFile.getAbsolutePath();
				jArray = new JsonArray();
				
			}

			fileWriter = new FileWriter(new File(currFileName));

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
			
			System.out.println(count);

			

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
	public List<Entity> readAll() throws IOException{
		//lista fajlova = FILE.FINDFILES (.json)
		//for petlja kroz listu fajlova i za svaki uradi readOneFile
		//list <entity> lista , u svakom foru spojis liste appendujes sa listom koju vrati readonefile
		
		List<Entity> toReturn = new ArrayList<>();
		
		StringBuilder currFileBuffer = new StringBuilder(currFileName);
		File dir = new File(folderName);
		File[] files = dir.listFiles();
		if(files.length != 0) {
			for(File file : files) {
				if(file.getAbsolutePath().endsWith(".json")) {
					currFileName = file.getAbsolutePath();
					List<Entity> lista = readCurrFile();
					if(lista != null) {
						toReturn.addAll(lista);
					}
				}
			}
		}
		currFileName = currFileBuffer.toString();
		
		
		
		return toReturn;
	}
	
	
	private List<Entity> readCurrFile() throws IOException {
		//read from currFileName

		
		List<Entity> entities = new ArrayList<Entity>();

		FileReader reader = null;
		try {
			reader = new FileReader(new File(currFileName));
		} catch (Exception e) {
			return null;
		}
		JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
		JsonArray jsonArray = (JsonArray) jsonElement;
		jArray = jsonArray;

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
		
		StringBuilder currFileBuffer = new StringBuilder(currFileName);
		File dir = new File(folderName);
		File[] files = dir.listFiles();
		if(files.length != 0) {
			for(File file : files) {
				if(file.getAbsolutePath().endsWith(".json")) {
					currFileName = file.getAbsolutePath();
					toReturn  = readFromOneFileById(id);
				}
			}
		}
		currFileName = currFileBuffer.toString();
		
		return toReturn;
	}
	
	private Entity readFromOneFileById(int id) throws IOException {
		//prodjes kroz sve fajlove i vratis entitet
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
		
		return toReturn;
	}
	@Override
	public void delete(int id) throws IOException{
		
		StringBuilder currFileBuffer = new StringBuilder(currFileName);
		File dir = new File(folderName);
		File[] files = dir.listFiles();
		if(files.length != 0) {
			for(File file : files) {
				if(file.getAbsolutePath().endsWith(".json")) {
					currFileName = file.getAbsolutePath();
					deleteFromOneFile(id);
				}
			}
		}
		currFileName = currFileBuffer.toString();
		
	}
	
	private void deleteFromOneFile(int id) throws IOException {
		
		FileReader reader = null;
		try {
			reader = new FileReader(new File(currFileName));
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
				FileWriter fw = new FileWriter(new File(currFileName));
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
										FileWriter fw = new FileWriter(new File(currFileName));
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

	@Override
	public void runDB(String folderName) throws Exception {
		//proveri da li se ime foldera zavrsava sa .json
		//pokreni loadUsedIDS
		//set currFileName ako je prazan napravi novi, ako ne dodaj bilo koji
		
		File dir = new File(folderName);
		try {
		
			if(!dir.isDirectory() || !dir.getName().endsWith(".json"))
				throw new IllegalArgumentException();
		} catch (IllegalArgumentException e) {
			System.err.println("Ubacili ste pogresan JAR, RUNTIME DEPENDENCY FAILED");
			System.exit(0);
		}
		
				
		if(dir.listFiles().length == 0) {
			int fileCount = dir.listFiles().length + 1;
			File file = new File(dir.getAbsolutePath()+"\\test" + fileCount + ".json");
			currFileName = file.getAbsolutePath();
		} else {
			File[] files = dir.listFiles();
			currFileName = files[files.length-1].getAbsolutePath();
		}
		
		loadUsedIDs();
		
	}
	

}
