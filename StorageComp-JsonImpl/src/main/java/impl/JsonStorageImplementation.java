package impl;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import api.ImplementorManager;
import api.StorageSpec;
import model.Entity;

public class JsonStorageImplementation extends StorageSpec {
	
	static {
		ImplementorManager.registerImplementor(new JsonStorageImplementation());
	}

	@Override
	public void save(Entity entity) {
		
		
		
	}

	@Override
	public void save(List<Entity> entities) {
		
		
		
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
	
	private String beautifyJson(String json) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		JsonNode tree = objectMapper.readTree(json);
		String formattedJson = objectMapper.writeValueAsString(tree);

		return formattedJson;
	}
	
}
