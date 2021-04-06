package mastercontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import mastercontroller.Models.Workload;
import mastercontroller.Services.VMManager;

public class ComponentGenerator extends JComponent {


    private ArrayList<JComponent> componentList;

    public ComponentGenerator() {
        componentList = new ArrayList<>();
    }


    public JButton addButton(String title, int x, int y, int width, int height){
        JButton button = new JButton(title);
        button.setBounds(x, y, width, height);
        componentList.add(button);
        return button;
    }

    public JButton addButton(String title){
        JButton button = new JButton(title);
        componentList.add(button);
        return button;
    }

    public JTextField addTextField(String title, int x, int y, int width, int height){
        JTextField textField = new JTextField(title);
        textField.setBounds(x, y, width, height);
        componentList.add(textField);
        return textField;
    }

    public JLabel addLabel(String title, int x, int y, int width, int height){
        JLabel label = new JLabel(title);
        label.setBounds(x, y, width, height);
        componentList.add(label);
        return label;
    }

    public JLabel addLabel(String title){
        JLabel label = new JLabel(title);
        componentList.add(label);
        return label;
    }

    public JComboBox addComboBox(String[] list){
        JComboBox box = new JComboBox(list);
        componentList.add(box);
        return box;
    }

    public JTextArea addTextArea(int rows, int columns){
        JTextArea textArea = new JTextArea(rows, columns);
        componentList.add(textArea);
        return textArea;
    }

    public JList addList(String[] listdata){
        JList list = new JList(listdata);
        componentList.add(list);
        return list;
    }

    public JScrollPane addScrollPane(JList list){
        JScrollPane scrollPane = new JScrollPane(list);
        componentList.add(scrollPane);
        return scrollPane;
    }


    public String[] fillVMList(ArrayList<Workload> workloadList){
        ArrayList<String> vms = new ArrayList<>();
        if(workloadList.isEmpty()){
            VMManager manager = new VMManager();
            try {
                manager.addJSONWorkloadToList();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            for (Workload workload : workloadList){
                System.out.println(workload.toString());
                vms.add(workload.toString());
            }
        }
        return vms.toArray(String[]::new);
    }

    public void generateComponents(ArrayList<JComponent> list, JPanel frame){
        for (JComponent jComponent : list) {
            System.out.println(jComponent.getClass());
            frame.add(jComponent);
            //TextOutPut(jComponent.getClass().toGenericString() + " have been added to frame...");
            frame.setVisible(true);
        }
    }


    /**
     * @return the componentList
     */
    public ArrayList<JComponent> getComponentList() {
        return componentList;
    }


    /**
     * @param componentList the componentList to set
     */
    public void setComponentList(ArrayList<JComponent> componentList) {
        this.componentList = componentList;
    }


}