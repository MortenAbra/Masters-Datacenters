package mastercontroller;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import mastercontroller.Services.VMManager;

/**
 * Hello world!
 *
 */
public class App extends JFrame {

    private JPanel panel;
    private JLabel vmNameLabel;
    private JLabel vmProperties;
    private JLabel vmIP;
    private JLabel vmStatus;
    private JLabel vmMigrationLabel;
    private JComboBox cmbAvailableVMList;
    private JButton btConfirmMigration;
    private JList lsVmList;
    private JScrollPane scpVmList;
    private GridBagLayout gbLayout;
    private GridBagConstraints gbConstraints;
    private ComponentGenerator generator;
    private VMManager manager;


    public App() {
        manager = new VMManager();
        generator = new ComponentGenerator();
        try {
            manager.addJSONWorkloadToList();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        addComponenets(generator);
    }

    private void addComponenets(ComponentGenerator generator){
        String[] dataVmList = { "Chocolate", "Ice Cream", "Apple Pie" };
        String[] dataAvailableVMList = { "Chocolate", "Ice Cream", "Apple Pie" };

        String[] test = generator.fillVMList(manager.getWorkloadList());


        
        panel = new JPanel();
        this.add(panel);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(800,400);
        this.setVisible(true);
        this.setResizable(true);
        this.setTitle("VM Manager");
        panel.setBorder(BorderFactory.createTitledBorder("VM List"));
        gbLayout = new GridBagLayout();
        gbConstraints = new GridBagConstraints();
        panel.setLayout(gbLayout);

        lsVmList = generator.addList(test);
        scpVmList = generator.addScrollPane(lsVmList);
        vmNameLabel = generator.addLabel("VM Name: ");
        vmProperties = generator.addLabel("VM Properties");
        vmIP = generator.addLabel("VM IP:");
        vmStatus = generator.addLabel("VM Status");
        vmMigrationLabel = generator.addLabel("Migration To: ");
        cmbAvailableVMList = generator.addComboBox(dataAvailableVMList);
        btConfirmMigration = generator.addButton("Migrate");
        
        Setup(generator, gbLayout, gbConstraints);
    }

    private void Setup(ComponentGenerator generator, GridBagLayout gbLayout, GridBagConstraints gbConstraints) {
        
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 1;
        gbConstraints.gridwidth = 2;
        gbConstraints.gridheight = 1;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1;
        gbConstraints.weighty = 1;
        gbConstraints.anchor = GridBagConstraints.NORTH;
        gbLayout.setConstraints(vmNameLabel, gbConstraints);


         
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 0;
        gbConstraints.gridwidth = 2;
        gbConstraints.gridheight = 1;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1;
        gbConstraints.weighty = 1;
        gbConstraints.anchor = GridBagConstraints.NORTH;
        gbLayout.setConstraints(vmProperties, gbConstraints);


         
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 2;
        gbConstraints.gridwidth = 2;
        gbConstraints.gridheight = 1;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1;
        gbConstraints.weighty = 1;
        gbConstraints.anchor = GridBagConstraints.NORTH;
        gbLayout.setConstraints(vmIP, gbConstraints);


         
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 3;
        gbConstraints.gridwidth = 2;
        gbConstraints.gridheight = 1;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1;
        gbConstraints.weighty = 1;
        gbConstraints.anchor = GridBagConstraints.NORTH;
        gbLayout.setConstraints(vmStatus, gbConstraints);


         
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 4;
        gbConstraints.gridwidth = 1;
        gbConstraints.gridheight = 1;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1;
        gbConstraints.weighty = 1;
        gbConstraints.anchor = GridBagConstraints.NORTH;
        gbLayout.setConstraints(vmMigrationLabel, gbConstraints);


        
         
        gbConstraints.gridx = 2;
        gbConstraints.gridy = 4;
        gbConstraints.gridwidth = 1;
        gbConstraints.gridheight = 1;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1;
        gbConstraints.weighty = 0;
        gbConstraints.anchor = GridBagConstraints.NORTH;
        gbLayout.setConstraints(cmbAvailableVMList, gbConstraints);


        gbConstraints.gridx = 1;
        gbConstraints.gridy = 5;
        gbConstraints.gridwidth = 2;
        gbConstraints.gridheight = 1;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1;
        gbConstraints.weighty = 0;
        gbConstraints.anchor = GridBagConstraints.NORTH;
        gbLayout.setConstraints(btConfirmMigration, gbConstraints);


        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;
        gbConstraints.gridwidth = 1;
        gbConstraints.gridheight = 6;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1;
        gbConstraints.weighty = 1;
        gbConstraints.anchor = GridBagConstraints.NORTH;
        gbLayout.setConstraints(scpVmList, gbConstraints);


    }

    public static void main(String[] args) {
        App app = new App();
        app.generator.generateComponents(app.generator.getComponentList(), app.getPanel());


    }

    /**
     * @return the panel
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * @param panel the panel to set
     */
    public void setPanel(JPanel panel) {
        this.panel = panel;
    }
}
