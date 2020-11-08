package impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
	public void save(Entity entity) {

		int count = 0;

		FileWriter fileWriter;

		try {

			List<Entity> oldEntities = null;
			try {
				FileReader fr = new FileReader(new File(currFileName));

				oldEntities = readCurrFile();
				count = oldEntities.size();

				fr.close();
			} catch (IOException e) {
			}
			
			if(count >= cap) {
				File dir = new File(folderName);
				int fileCount = dir.listFiles().length + 1;
				if(!dir.isDirectory() || !dir.getName().endsWith(".yaml"))
					throw new IllegalArgumentException();
				File newFile = new File(dir.getAbsolutePath()+"\\test" + fileCount + ".yaml");
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

	@Override
	public void save(List<Entity> entities) {
		for(Entity entity : entities) {
			save(entity);
		}
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
	public List<Entity> read(int[] ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(int[] ids) {
		// TODO Auto-generated method stub

	}

	@Override
	public Entity read(int id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(int id) throws IOException {
		// TODO Auto-generated method stub

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

	private List<Entity> readCurrFile() throws IOException {

		YAMLMapper mapper = new YAMLMapper(new YAMLFactory());
		EntityWrapper entitiesWrapper = mapper.readValue(new File(currFileName),
				EntityWrapper.class);

		if(entitiesWrapper != null)
			return entitiesWrapper.getEntities();
		return new ArrayList<Entity>();
	}

}
