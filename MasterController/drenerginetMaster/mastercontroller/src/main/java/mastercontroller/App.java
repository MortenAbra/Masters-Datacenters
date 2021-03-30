package mastercontroller;

import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

import mastercontroller.Models.Workload;
import mastercontroller.Services.VMManager;

/**
 * Hello world!
 *
 */
public class App extends JFrame{

    private JFrame frame;
    private JPanel panel;

    public App(){
        frame = new JFrame("VM Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setResizable(false);
        panel = new JPanel();
    }

    
    
    public static void main( String[] args )
    {
        App app = new App();
        ComponentGenerator generator = new ComponentGenerator();
        VMManager vmManager = new VMManager();

        try {
            vmManager.addJSONWorkloadToList();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        generator.generateComponents(generator.getComponentList(), app.getFrame(), vmManager.getWorkloadList());
        


        


    }



    /**
     * @return the frame
     */
    public JFrame getFrame() {
        return frame;
    }



    /**
     * @param frame the frame to set
     */
    public void setFrame(JFrame frame) {
        this.frame = frame;
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
