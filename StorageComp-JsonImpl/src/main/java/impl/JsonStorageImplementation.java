package impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.*;

import api.ImplementorManager;
import api.StorageSpec;
import model.Entity;

public class JsonStorageImplementation extends StorageSpec {
	
	static {
		ImplementorManager.registerImplementor(new JsonStorageImplementation());
	}
	
	JsonArray jArray = new JsonArray();

	@Override
	public void save(Entity entity){
		
        FileWriter fileWriter;
        
		try {
			
			
			
			fileWriter = new FileWriter(new File(fileName));
			
			JsonObject object = new JsonObject();
	        
	        object.addProperty("naziv", entity.getNaziv());
	        object.addProperty("id", entity.getId());
	        
	        if(!entity.getProperties().isEmpty()) {
	        	for(HashMap.Entry<String,String> entry : entity.getProperties().entrySet()) {
	        		object.addProperty(entry.getKey(), entry.getValue());
	        	}
	        }
	        
	        if(!entity.getSubEntities().isEmpty()) {
	        	// Prolazimo kroz sva polja koja kao value sadrze ugnjezdene entitete(moze ih biti vise, ali na istom hijerarhijskom nivou)
	        	for(HashMap.Entry<String, List<Entity>> subEntitiesPack : entity.getSubEntities().entrySet()) {
	        		
	        		JsonArray internalArray = new JsonArray();
	        		
	        		// Prolazimo kroz listu ugnjezdenih entiteta, da bi ih dodali u JsonArray
	        		for(Entity subEntity : subEntitiesPack.getValue()) {
	        			
	        			JsonObject internalObject = new JsonObject();
		        		
	        			internalObject.addProperty("naziv", subEntity.getNaziv());
		        		internalObject.addProperty("id", subEntity.getId());
		        		
		        		// Za svaki entitet prolazimo kroz njegove propertije (samo raw stringovi)
		        		for(HashMap.Entry<String, String> properties : subEntity.getProperties().entrySet())
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
		
		for(Entity entity : entities) {
			save(entity);
		}
		
	}

	@Override
	public List<Entity> read() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		
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
	
	private String beautifyJson(String json) throws IOException {
	    ObjectMapper mapper = new ObjectMapper();
	    Object obj = mapper.readValue(json, Object.class);
	    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
	}
	
}
