package System.client;

import System.Controller.FXMLDocumentController;
import System.Controller.mainFormController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {
    private SocketHandle socketHandle;
    private Stage primaryStage;
    private FXMLDocumentController fxmlDocumentController;
    private String loggedInUsername;

    public Client() {
        try {
            socketHandle = new SocketHandle("localhost", 12345); // Initialize SocketHandle with server address and port
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception gracefully (e.g., show error dialog, exit application)
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginView();
    }

    private void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/projectjava/login.fxml"));
            Parent root = loader.load();

            fxmlDocumentController = loader.getController();
            fxmlDocumentController.setClient(this); // Pass current client instance to FXMLDocumentController
            fxmlDocumentController.setSocketHandle(socketHandle); // Pass SocketHandle to FXMLDocumentController

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception gracefully (e.g., show error dialog)
        }
        System.out.println("1"+loggedInUsername);
    }

    public void showClientMainView(String loggedInUsername) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/projectjava/ClientMainForm.fxml"));
            Parent root = loader.load();

            mainFormController controller = loader.getController();
            controller.setSocketHandle(socketHandle, loggedInUsername); // Pass SocketHandle to mainFormController

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception gracefully (e.g., show error dialog)
        }
        System.out.println("2"+loggedInUsername);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public SocketHandle getSocketHandle() {
        return socketHandle;
    }

    // Method to get logged in username from FXMLDocumentController
    public String getLoggedInUsername() {
        if (fxmlDocumentController != null) {
            return fxmlDocumentController.getLoggedInUsername();
        }
        return null; // Handle case where fxmlDocumentController is null
    }
}