package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import api.StorageSpec;
import model.Entity;


public class MainFrame extends JFrame {
	 private static MainFrame instance = null;
	 private static String currFolder;
	 private static StorageSpec spec = MainApp.spec;
	 private JTable table;
	 String[] columnNames = { "Name and ID", "Properties", "SubEntities" }; 
	 JPanel dugmad;
	 
	 public static String getCurrFolder() {
		return currFolder;
	}


	public static void setCurrFolder(String currFolder) {
		MainFrame.currFolder = currFolder;
	}


	public static MainFrame getInstance(){
	        if (instance==null){
	            instance=new MainFrame();
	            instance.initialise();
	        }
	        return instance;
	    }


	    private void initialise() {

	        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        this.setTitle("DataBase Management");
	        this.setLocationRelativeTo(null);
	        this.setVisible(true);

	    }
	    
	    public void maximize() {
	        setExtendedState(Frame.MAXIMIZED_BOTH);
	    }
	    
	    public void createUI(final JFrame frame){  
	        JPanel panel = new JPanel();
	        LayoutManager layout = new FlowLayout();  
	        panel.setLayout(layout);       
	        
	        JButton button = new JButton("Choose a folder");
	        final JLabel label = new JLabel();

	        button.addActionListener(new ActionListener() {
	           @Override
	           public void actionPerformed(ActionEvent e) {
	              JFileChooser fileChooser = new JFileChooser();
	              fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	              int option = fileChooser.showOpenDialog(frame);
	              if(option == JFileChooser.APPROVE_OPTION){
	                 File file = fileChooser.getSelectedFile();
	                 label.setText("Folder Selected: " + file.getName());
	                 currFolder = file.getAbsolutePath();
	                 try {
						spec.setFolderNameAndStart(currFolder);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                 
	                 showTable();
	                 System.out.println(currFolder);
	              }else{
	                 label.setText("FAIL");
	              }
	           }
	        });
	        panel.add(button);
	        panel.add(label);
	        frame.getContentPane().add(panel);    
	     }  
	    
	    
	    public void showTable() {
	    	
			
	    	dugmad = new JPanel();
	    	
	        this.getContentPane().removeAll();
	        JButton button1 = new JButton("Create Entity");
	        JButton button2 = new JButton("Remove Entity");
	        JButton button3 = new JButton("Search by name");
	        JButton button4 = new JButton("Read All");
	        JButton button5 = new JButton("Search by phrase");
	        
	        button2.addActionListener(new DeleteEntity());
	        button4.addActionListener(new ReadAll());
	        button5.addActionListener(new SearchEntityKeyValue());
	        button3.addActionListener(new SearchEntityName());
	        button1.addActionListener(new CreateEntity());
	        
	        dugmad.add(button1);
	        dugmad.add(button2);
	        dugmad.add(button3);
	        dugmad.add(button4);
	        dugmad.add(button5);
	        
	        this.getContentPane().add(dugmad, BorderLayout.NORTH); 
	        
	        
	    }
	    
	    public class CreateEntity implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ArrayList<JTextField> fields = new ArrayList<JTextField>();
				Sample2 test = new Sample2();
				
			} 
	    }
	    
	    public void creatingEn(Sample2 test) {
	    	Map<String, String> mapa = test.returnInfo();
			
			String id = mapa.get("id");
			mapa.remove("id");
			String naziv = mapa.get("naziv");
			mapa.remove("naziv");
			if (naziv == null | id == null) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Niste uneli naziv ili identiteta", "Warning",
				        JOptionPane.WARNING_MESSAGE);
			}
			Integer parentID = null;
			try {
				parentID = Integer.parseInt(mapa.get("parentID"));
				mapa.remove("parentID");
			} catch (Exception e) {
				// TODO: handle exception
			}
			String parentName = mapa.get("parentName");
			mapa.remove("parentName");
			Entity en;
			int iD = 0;
			try {
				iD = Integer.parseInt(id);
			} catch (Exception e2) {
				// TODO: handle exception
			}
			Map<String,List<Entity>> prazno = new HashMap<String, List<Entity>>();
			if (id == null) {
				en = new Entity(naziv, mapa, prazno);
			}else {
				en = new Entity(iD, naziv, mapa, prazno);
			}
			if (parentID != null && parentName != null) {
				
				try {
					spec.addSubEntityToEntity(parentID,en,parentName);
					spec.update(spec.read(parentID));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else {
				 try {
					spec.save(en);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			List<Entity> lista = new ArrayList<Entity>();
			try {
				lista = spec.readAll();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			createTable(lista);
			
	    }
	    public class DeleteEntity implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String id=JOptionPane.showInputDialog("Enter ID for deletion"); 
				List<Entity> lista = new ArrayList <Entity>();
				try {
					int idint = Integer.parseInt(id);
					spec.delete(idint);
				} catch (Exception e2) {
					// TODO: handle exception
				}
				try {
					lista = spec.readAll();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				createTable(lista);
			} 
	    }
	    public class SearchEntityName implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				String id=JOptionPane.showInputDialog("Enter name"); 
				List<Entity> lista = new ArrayList <Entity>();
				
				try {
					lista = spec.findByName(id);
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
				
				createTable(lista);
			} 
	    }
	    
	    public class SearchEntityKeyValue implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				String id=JOptionPane.showInputDialog("Enter name"); 
				List<Entity> lista = new ArrayList <Entity>();
					try {
						lista = spec.findSpecial(id);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
				createTable(lista);
			} 
	    }
	    public class ReadAll implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stubList<Entity> lista = new ArrayList <Entity>();
				List<Entity> lista = new ArrayList <Entity>();
				try {
					lista = spec.readAll();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				createTable(lista);
			} 
	    }
	    
	    private void createTable(List<Entity> lista) {
	    
			
		String[][] data = new String[1000][3];
		
		int i = 0;
			for (Entity en : lista) {
				data[i][0] = " Naziv : " + en.getNaziv() + ", ID : " + en.getId();
				data[i][1] = "";
				for (Entry<String, String> entry : en.getProperties().entrySet()) {
				    String key = entry.getKey();
				    String value = entry.getValue();
				    data[i][1] += key + " : " + value + "    ";
				    
				}
				data[i][2] = "";
				if (en.getSubEntities() != null) {
					for (Entry<String, List<Entity>> entry : en.getSubEntities().entrySet()) {
					    String key = entry.getKey();
					    List<Entity> listaPod = (List<Entity>) entry.getValue();
					    data[i][2] += key + ":";
					    for (Entity ent : listaPod) {
					    	data[i][2] += " Naziv : " + ent.getNaziv() + ", ID : " + ent.getId();
					    	for (Entry<String, String> entry2 : ent.getProperties().entrySet()) {
							    String kljuc = entry2.getKey();
							    String value = entry2.getValue();
							    data[i][2] += kljuc + " : " + value + "    ";
							}
					    	
					    }
					    
					}
				}
				
				i++;
			}
		table = new JTable(data, columnNames);
		table.setAutoscrolls(true);
		JScrollPane panel = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
		MainFrame.getInstance().getContentPane().add(panel, BorderLayout.CENTER);
		
		panel.setViewportView(table);
		panel.createHorizontalScrollBar();
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setVisible(true);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	    }
	    
	    public class Sample2 implements ActionListener {
	        JFrame mainFrame;

	        JPanel bottom;
	        JPanel center;
	        JPanel centerPanel1;
	        JPanel centerPanel2;

	         int count = 0;
	         int width_textfield = 10;
	         int height_textfield = 40;
	        int height = 0;

	        JButton addTextField, submit;
	        JTextField virtualDirectories;
	        JTextField virtualDirectoriesName;

	        ArrayList<JTextField> fields1 = new ArrayList<JTextField>();
	        ArrayList<JTextField> fields2 = new ArrayList<JTextField>();
	        ArrayList<String> keys = new ArrayList<String>();
	        ArrayList<String> values = new ArrayList<String>();
	        ArrayList<String> key = new ArrayList<String>();
            ArrayList<String> value = new ArrayList<String>();
	        int maxFields = 10;
	        
	        boolean submitted = false;
	        
	        public Sample2() {
	            mainFrame = new JFrame("Create Entity");
	            mainFrame.setSize(640, 640);
	            mainFrame.setResizable(false);

	            addTextField = new JButton();
	            addTextField.setText("Add Another Pair");
	            addTextField.setBounds(10, 10, 200, 25);
	            addTextField.addActionListener(this);

	            submit = new JButton();
	            submit.setText("Submit");
	            submit.setBounds(180, 560, 100, 25);
	            submit.addActionListener(this);

	            center = new JPanel(new GridLayout(1, 2));

	            centerPanel1 = new JPanel(new GridLayout(maxFields, 1, 0, 20));
	            centerPanel2 = new JPanel();

	            center.add(centerPanel1);
	            center.add(centerPanel2);

	            bottom = new JPanel(new FlowLayout());
	            bottom.add(addTextField);
	            bottom.add(submit);

	            mainFrame.getContentPane().add(bottom, BorderLayout.SOUTH);
	            mainFrame.getContentPane().add(center, BorderLayout.CENTER);
	            mainFrame.setVisible(true);

	        }
	        
	        public Map<String,String> returnInfo(){
	        	
	        	Map<String, String> mapa = new HashMap<String,String>();
	        	for (int i = 0; i < key.size(); i++) {
	        		mapa.put(key.get(i), value.get(i));
	        	}
	        	return mapa;
	        }
	        public void actionPerformed(ActionEvent e) {
	            if (e.getActionCommand() == "Add Another Pair") {

	                if (count < 100) {

	                    JPanel p = new JPanel(new GridLayout(1, 2));

	                    virtualDirectoriesName =  new JTextField();
	                    virtualDirectories = new JTextField();

	                    p.add(virtualDirectoriesName);
	                    p.add(virtualDirectories);

	                    centerPanel1.add(p);
	                    
	                    keys.add(virtualDirectoriesName.getText());
	                    values.add(virtualDirectories.getText());
	                    fields1.add(virtualDirectories);
	                    fields2.add(virtualDirectoriesName);

	                    count++;
	                    // width_textfield++;
	                    height_textfield = height_textfield + 60;

	                    mainFrame.revalidate();
	                    mainFrame.repaint();

	                    // http://www.dreamincode.net/forums/topic/381446-getting-the-values-from-mutiple-textfields-in-java-swing/
	                } else {
	                    JOptionPane.showMessageDialog(mainFrame, "can only add " + maxFields + " virtual Directories");
	                }

	            }
	            if (e.getActionCommand() == "Submit") {
	                for (int i = 0; i < count; i++) {
	                    key.add(fields2.get(i).getText());
	                    value.add(fields1.get(i).getText());
	                }

	                System.out.println(value.toString());
	                System.out.println(key.toString());
	                submitted = true;
	                MainFrame.getInstance().creatingEn(this);
	            }
	        }

	       

	    }
}
