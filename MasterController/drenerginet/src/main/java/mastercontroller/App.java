package mastercontroller;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



/**
 * JavaFX App
 */
public class App extends Application {

    Scene scene;
    Parent root;

    public App() throws IOException{
        root = FXMLLoader.load(getClass().getResource("MainGUI.fxml"));
        scene = new Scene(root, 800, 600);
    }


    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("VM Migrator");
        stage.setScene(this.scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


    /**
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }


    /**
     * @param scene the scene to set
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }


    /**
     * @return the root
     */
    public Parent getRoot() {
        return root;
    }


    /**
     * @param root the root to set
     */
    public void setRoot(Parent root) {
        this.root = root;
    }

}