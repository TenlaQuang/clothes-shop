package System.View;

import System.Controller.FXMLDocumentController;
import System.client.SocketHandle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private SocketHandle socketHandle;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            socketHandle = new SocketHandle("localhost", 12345); // Điều chỉnh địa chỉ server và port theo nhu cầu
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/projectjava/login.fxml"));
            Parent root = loader.load();

            FXMLDocumentController controller = loader.getController();
            controller.setSocketHandle(socketHandle);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSocketHandle(SocketHandle socketHandle) {
        this.socketHandle = socketHandle;
    }
}