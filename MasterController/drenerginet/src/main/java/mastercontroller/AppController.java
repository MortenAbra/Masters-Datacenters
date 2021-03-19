package mastercontroller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class AppController {
    @FXML private Text someText;
    @FXML private Button btn = new Button();
    private App app;

    public AppController() throws IOException{
        app = new App();
    }

    @FXML private void getBtn(Button button){
        
        
    }
    
}
