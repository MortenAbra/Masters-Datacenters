package drenerginet.GUI;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import drenerginet.FileHandler;

public class AppGUI extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static ArrayList<JComponent> compList = new ArrayList<>();
    private static FileHandler fh;

    public AppGUI(String title){
        super(title);
        setSize(800,600);
        setVisible(true);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fh = new FileHandler();
    }

    public static ArrayList<JComponent> addButton(String text, int xCoord, int yCoord, int width, int height){
        JButton button = new JButton(text);
        button.setBounds(xCoord, yCoord, width, height);
        compList.add(button);
        return compList;
    }

    public static ArrayList<JComponent> addTextField(String text, int xCoord, int yCoord, int width, int height){
        JTextField textField = new JTextField(text);
        textField.setBounds(xCoord, yCoord, width, height);
        compList.add(textField);
        return compList;
    }

    public static ArrayList<JComponent> addPasswordField(String text, int xCoord, int yCoord, int width, int height){
        JPasswordField passField = new JPasswordField(text);
        passField.setBounds(xCoord, yCoord, width, height);
        compList.add(passField);
        return compList;
    }

    public static ArrayList<JComponent> addLabel(String text, int xCoord, int yCoord, int width, int height){
        JLabel label = new JLabel(text);
        label.setBounds(xCoord, yCoord, width, height);
        compList.add(label);
        return compList;
    }

    public static void initJComponentsFromList(JFrame frame, ArrayList<JComponent> componentList){
        ExecutorService es = Executors.newCachedThreadPool();
        es.submit(() -> {
            for (JComponent jComponent : componentList) {
                frame.add(jComponent);
                fh.TextOutPut("Thread: " + Thread.currentThread().getName() + " - " + jComponent.getName());
                frame.repaint();
            }
        });
        frame.setVisible(true);     
        es.shutdownNow();
        if(es.isTerminated()){
            fh.TextOutPut("GUI Executor shutting down!");
        }
    }

    public static void main(String[] args) {
        AppGUI app = new AppGUI("VM Manager");
        addButton("Login", 300, 275, 100, 30);
        addButton("Test2", 410, 275, 100, 30);
        addTextField("Enter username", 350, 200, 100, 30);
        addPasswordField("Password", 350, 240, 100, 30);
        addLabel("Login credentials", 350, 160, 100, 30);


        initJComponentsFromList(app, compList);
    }

    
}
