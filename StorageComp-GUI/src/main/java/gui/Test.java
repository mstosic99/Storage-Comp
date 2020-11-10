package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.ImplementorManager;
import api.StorageSpec;
import model.Entity;

public class Test {

	public static void main(String[] args) {
		
		try {
			//Class.forName("impl.JsonStorageImplementation");
			//Class.forName("impl.YamlStorageImplementation");
			Class.forName("impl.CustomStorageImplementation");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StorageSpec spec = ImplementorManager.getStorageSpec();
		try {
			spec.setFolderNameAndStart("C:\\Users\\Majkic\\Desktop\\1.custom");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String, String> properties = new HashMap<>();
		Map<String, List<Entity>> subEntitiesPack = new HashMap<>();
		Map<String, String> internalProperties = new HashMap<>();
		
		internalProperties.put("broj", "35");
		internalProperties.put("neki podatak", "ja sam taj podatak");
		
		properties.put("ime", "Pera");
		properties.put("prezime", "Peric");
		properties.put("jmbg", "2308993548392");
		properties.put("email", "peraperic@gmail.com");
		
		List<Entity> prijatelji = new ArrayList<>();
		
		Map<String, String> props = new HashMap<>();
		
		props.put("age", "25");
		props.put("nesto", "neko");
		prijatelji.add(new Entity("Mixa", props, null));
		
		Map<String, String> props2 = new HashMap<>();
		props2.put("age", "37");
		props2.put("nesto", "sta bre");
		props2.put("jmbg", "24356345634345");
		prijatelji.add(new Entity("Sone", props2, null));
		
		List<Entity> lista = new ArrayList<>();
		lista.add(new Entity("Vule Antica", internalProperties, null));
		subEntitiesPack.put("adresa", lista);
		subEntitiesPack.put("prijatelji", prijatelji);
		
		Entity finalEntity1 = new Entity("student", properties, subEntitiesPack);
		Map<String, String> properties2 = new HashMap<>();
		Map<String, List<Entity>> subEntitiesPack2 = new HashMap<>();
		Map<String, String> internalProperties2 = new HashMap<>();
		
		internalProperties2.put("broj", "35");
		internalProperties2.put("neki podatak", "ja sam taj podatak");
		
		properties2.put("ime", "Djoka");
		properties2.put("prezime", "Djokic");
		properties2.put("jmbg", "23082345648392");
		properties2.put("email", "djokadjokic@gmail.com");
		
		List<Entity> prijatelji2 = new ArrayList<>();
		
		Map<String, String> props3 = new HashMap<>();
		
		props3.put("age", "53");
		props3.put("nesto", "dsfgsdfg");
		prijatelji2.add(new Entity("Zika", props3, null));
		
		Map<String, String> props4 = new HashMap<>();
		props4.put("age", "1");
		props4.put("nesto", "ddeqwerdc");
		props4.put("jmbg", "243563453472345");
		prijatelji2.add(new Entity("Neko", props4, null));
		
		List<Entity> lista2 = new ArrayList<>();
		lista2.add(new Entity("Dr kopse", internalProperties2, null));
		subEntitiesPack2.put("adresa", lista2);
		subEntitiesPack2.put("prijatelji", prijatelji2);
		
		Entity finalEntity2 = new Entity("osoba", properties2, subEntitiesPack2);
		try {
			spec.save(finalEntity1);				// testovi
			spec.save(finalEntity2);
			
			//finalEntity1.getProperties().put("ime", "Andreja");
			//spec.update(finalEntity1);
			//spec.deleteSpecial("ime=Andreja");
			//spec.deleteSpecial("jmbg=23082345648392");
			//List<Entity> listaE = new ArrayList<Entity>();
			//listaE = spec.sortSpecial("ime", true);
			//System.out.println(listaE.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
