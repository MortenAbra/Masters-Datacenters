package mastercontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import mastercontroller.Models.Workload;

public class ComponentGenerator extends JComponent {


    private ArrayList<JComponent> componentList;
    private JTextArea vmListTextArea;
    private JLabel vmListLabel;
    private JLabel vmProperties;
    private JLabel vmName;
    private JLabel vmStatus;
    private JLabel vmIP;
    private JLabel vmPort;

    public ComponentGenerator() {
        componentList = new ArrayList<>();
    }


    public JButton addButton(String title, int x, int y, int width, int height){
        JButton button = new JButton(title);
        button.setBounds(x, y, width, height);
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

    public JTextArea addTextArea(int rows, int columns){
        JTextArea textArea = new JTextArea(rows, columns);
        componentList.add(textArea);
        return textArea;
    }

    public JScrollPane addScrollPane(JTextArea textarea){
        JScrollPane scrollPane = new JScrollPane(textarea);
        componentList.add(scrollPane);
        return scrollPane;
    }

    public void initComponents(){
        vmListTextArea = addTextArea(1, 1);
        addScrollPane(vmListTextArea);
        addLabel("VM List", 5, 5, 50, 30);
    }

    public void fillVMList(ArrayList<Workload> workloadList){
        for (Workload workload : workloadList) {
            vmListTextArea.append(workload.toString() + '\n');
            //TextOutPut("Workload added to text area...");
        }
    }

    public void generateComponents(ArrayList<JComponent> list, JFrame frame, ArrayList<Workload> workloadList){
        initComponents();
        for (JComponent jComponent : list) {
            frame.add(jComponent);
            //TextOutPut(jComponent.getClass().toGenericString() + " have been added to frame...");
            frame.setVisible(true);
        }
        if(!workloadList.isEmpty()){
            //TextOutPut(String.valueOf(workloadList.size()));
            fillVMList(workloadList);
        } else {
            //TextOutPut("Workload list is empty...");
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