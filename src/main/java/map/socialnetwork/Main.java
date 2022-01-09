package map.socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import map.socialnetwork.controller.InitializableController;
import map.socialnetwork.views.ViewResolver;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class Main extends Application {

    private static Stage stage;
    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        primaryStage.setResizable(false); // user can't change window size
        FXMLLoader fxmlLoader = new FXMLLoader(ViewResolver.getView("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("[Owlett]");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void switchScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewResolver.getView(fxml));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    public static void switchSceneWithParameters(String fxml, Map<String, Object> parameters) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewResolver.getView(fxml));
        Scene scene = new Scene(fxmlLoader.load());

        // access controller and call method
        Object controller = fxmlLoader.getController();
        if(controller == null) {
            throw new UnsupportedOperationException("No controller has been found for the given view:" + fxml);
        }
        if(controller instanceof InitializableController) {
            ((InitializableController)controller).initializeData(parameters);
            stage.setScene(scene);
        } else {
            throw new UnsupportedOperationException("This controller does not support initializing:" + controller.getClass().getSimpleName());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}