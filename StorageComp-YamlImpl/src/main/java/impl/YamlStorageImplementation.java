package impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import api.ImplementorManager;
import api.StorageSpec;
import model.Entity;

public class YamlStorageImplementation extends StorageSpec {

	static {
		ImplementorManager.registerImplementor(new YamlStorageImplementation());
	}

	@Override
	public void save(List<Entity> entities) {
		for (Entity entity : entities) {
			save(entity);
		}
	}

	@Override
	public void save(Entity entity) {

		try {

			int count = 0;
			List<Entity> oldEntities = null;
			try {
				FileReader fr = new FileReader(new File(currFileName));

				oldEntities = readCurrFile();
				count = oldEntities.size();

				fr.close();
			} catch (IOException e) {
			}

			if (count >= 10) {
				File dir = new File(folderName);
				int fileCount = dir.listFiles().length + 1;
				if (!dir.isDirectory() || !dir.getName().endsWith(".yaml"))
					throw new IllegalArgumentException();
				File newFile = new File(dir.getAbsolutePath() + "\\test" + fileCount + ".yaml");
				currFileName = newFile.getAbsolutePath();
				oldEntities = null;

			}

			EntityWrapper entityWrap = new EntityWrapper();
			if (oldEntities != null) {
				entityWrap.getEntities().addAll(oldEntities);
			}

			entityWrap.getEntities().add(entity);
			YAMLMapper mapper = new YAMLMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));
			mapper.writeValue(new File(currFileName), entityWrap);

			System.out.println(count);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<Entity> readCurrFile() throws IOException {

		YAMLMapper mapper = new YAMLMapper(new YAMLFactory());
		EntityWrapper entitiesWrapper = mapper.readValue(new File(currFileName), EntityWrapper.class);

		if (entitiesWrapper != null)
			return entitiesWrapper.getEntities();
		return new ArrayList<Entity>();
	}

	@Override
	public List<Entity> readAll() throws IOException {

		List<Entity> toReturn = new ArrayList<>();

		StringBuilder currFileBuffer = new StringBuilder(currFileName);
		File dir = new File(folderName);
		File[] files = dir.listFiles();
		if (files.length != 0) {
			for (File file : files) {
				if (file.getAbsolutePath().endsWith(".yaml")) {
					currFileName = file.getAbsolutePath();
					List<Entity> lista = readCurrFile();
					if (lista != null) {
						toReturn.addAll(lista);
					}
				}
			}
		}
		currFileName = currFileBuffer.toString();

		return toReturn;
	}

	@Override
	public List<Entity> read(int[] ids) throws IOException {
		
		// TODO Ovo moze da se prebaci u API (Iste metode u svim implementacijama)
		
		List<Entity> entities = new ArrayList<>();

		for (int i = 0; i < ids.length; i++) {
			Entity e = null;
			e = read(ids[i]);
			if (e != null)
				entities.add(e);
		}

		if (entities.isEmpty()) {
			return null;
		}
		return entities;
	}

	@Override
	public Entity read(int id) throws IOException {
		
		Entity Roger = null;
		EntityWrapper entityWrap = new EntityWrapper();
		List<Entity> entities = readAll();
		entityWrap.getEntities().addAll(entities);
		
		for(Entity entity : entityWrap.getEntities()) {
			if(entity.getId() == id)
				Roger = entity;
		}
		
		try {
			if(Roger == null)
				throw new IllegalArgumentException();
		} catch (IllegalArgumentException e) {
			System.err.println("Id koji ste uneli za citanje ne postoji");
		}
		
		return Roger;
	}

	@Override
	public void delete(int[] ids) throws IOException {
		
		// TODO Ovo moze da se prebaci u API (Iste metode u svim implementacijama)
		
		for(int i = 0; i < ids.length; i++) {
			delete(ids[i]);
		}
		
	}

	@Override
	public void delete(int id) throws IOException {
		
		AbstractMap.SimpleEntry<Boolean, Entity> par = null;
		StringBuilder currFileBuffer = new StringBuilder(currFileName);
		
		File dir = new File(folderName);
		File[] files = dir.listFiles();
		if(files.length != 0) {
			for(File file : files) {
				if(file.getAbsolutePath().endsWith(".yaml")) {
					currFileName = file.getAbsolutePath();
					par = findFromOneFileById(id);
					if(par != null && par.getValue() != null)
						break;
				}
			}
		}
		
		try {
			if(par.getValue() == null) {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException e) {
			System.err.println("Id koji ste uneli za brisanje ne postoji");
			return;
		}
		
		EntityWrapper entityWrap = new EntityWrapper();
		entityWrap.getEntities().addAll(readCurrFile());
		
		
		
		if(par.getKey() == false) {
			for(Entity entity : entityWrap.getEntities()) {
				if(entity.getId() == id) {
					entityWrap.getEntities().remove(entity);
					break;
				}
			}
		} else {
			boolean done = false;
			for(Entity entity : entityWrap.getEntities()) {
				for(HashMap.Entry<String, List<Entity>> subEntityPack : entity.getSubEntities().entrySet()) {
					for(Entity subEntity : subEntityPack.getValue()) {
						if(subEntity.getId() == par.getValue().getId()) {
							subEntityPack.getValue().remove(subEntity);
							done = true;
							break;
						}
						
					}
					if(done)
						break;
				}
				if(done)
					break;
			}
		}	
		
		
		YAMLMapper mapper = new YAMLMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));
		mapper.writeValue(new File(currFileName), entityWrap);
		
		currFileName = currFileBuffer.toString();
		System.out.println();
	}

	private AbstractMap.SimpleEntry<Boolean, Entity> findFromOneFileById(int id) throws IOException {
		
		boolean isSub = false;
		Entity Roger = null;
		List<Entity> entities = readCurrFile();
		for(Entity entity : entities) {
			if(entity.getId() == id) {
				Roger = entity;
				break;
			}
			for(HashMap.Entry<String, List<Entity>> subEntityPack : entity.getSubEntities().entrySet()) {
				for(Entity subEntity : subEntityPack.getValue()) {
					if(subEntity.getId() == id) {
						Roger = subEntity;
						isSub = true;
						break;
					}
				}
				if(Roger != null)
					break;
			}
			if(Roger != null)
				break;
		}
		AbstractMap.SimpleEntry<Boolean, Entity> par = new AbstractMap.SimpleEntry<Boolean, Entity>(isSub, Roger);
		
		
		return par;
		
		
		
	}

	@Override
	public void runDB(String fileName) {
		File dir = new File(folderName);
		try {

			if (!dir.isDirectory() || !dir.getName().endsWith(".yaml"))
				throw new IllegalArgumentException();
		} catch (IllegalArgumentException e) {
			System.err.println("Ubacili ste pogresan JAR, RUNTIME DEPENDENCY FAILED");
			System.exit(0);
		}

		if (dir.listFiles().length == 0) {
			int fileCount = dir.listFiles().length + 1;
			File file = new File(dir.getAbsolutePath() + "\\test" + fileCount + ".yaml");
			currFileName = file.getAbsolutePath();
		} else {
			File[] files = dir.listFiles();
			currFileName = files[files.length - 1].getAbsolutePath();
		}

		loadUsedIDs();
	}

}
