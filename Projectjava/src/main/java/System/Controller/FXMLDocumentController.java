package System.Controller;

import System.Model.Users;
import System.client.Client;
import System.client.SocketHandle;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {

    @FXML
    private Hyperlink L_forgotp;

    @FXML
    private AnchorPane L_loginForm;

    @FXML
    private PasswordField L_password;

    @FXML
    private TextField L_username;

    @FXML
    private Button Login_btn;

    @FXML
    private TextField p_Cpass;

    @FXML
    private Button p_done;

    @FXML
    private TextField p_pass;

    @FXML
    private TextField s_answer;

    @FXML
    private TextField s_answer1;

    @FXML
    private Button s_backsloginBtn;

    @FXML
    private Button s_backsloginBtn1;

    @FXML
    private Button s_continueBTN;

    @FXML
    private AnchorPane s_forgotForm;

    @FXML
    private AnchorPane s_forgotFormpass;

    @FXML
    private PasswordField s_password;

    @FXML
    private ComboBox<?> s_question;

    @FXML
    private ComboBox<?> s_question1;

    @FXML
    private Button s_signupBtn;

    @FXML
    private AnchorPane s_signupForm;

    @FXML
    private TextField s_username;

    @FXML
    private TextField s_username1;

    @FXML
    private Button side_CreateBtn;

    @FXML
    private Button side_alreadyHave;

    @FXML
    private AnchorPane side_form;

    private SocketHandle socketHandle;
    private static SessionFactory sessionFactory;
    private Client client;
    private String loggedInUsername;

    public FXMLDocumentController() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }


    public void setClient(Client client) {
        this.client = client;
    }

    public void setSocketHandle(SocketHandle socketHandle) {
        this.socketHandle = socketHandle;
    }

    private String[] questionList = {"What's ur favorite Color?", "What's ur favorite food?", "What's ur birth date?"};

    public void regLquestionList() {
        List<String> ListQ = new ArrayList<>();
        for (String q : questionList) {
            ListQ.add(q);
        }
        ObservableList ListData = FXCollections.observableArrayList(ListQ);
        s_question.setItems(ListData);
    }

    public void forgotPassQuestion() {
        List<String> Listq = new ArrayList<>();
        for (String q : questionList) {
            Listq.add(q);
        }
        ObservableList ListData = FXCollections.observableArrayList(Listq);
        s_question1.setItems(ListData);

    }

    public void switchForm(ActionEvent event) {
        TranslateTransition slider = new TranslateTransition();
        if (event.getSource() == side_CreateBtn) {
            slider.setNode(side_form);
            slider.setToX(300);
            slider.setDuration(Duration.seconds(.5));

            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(true);
                side_CreateBtn.setVisible(false);
                s_forgotForm.setVisible(false);
                s_forgotFormpass.setVisible(false);
                L_loginForm.setVisible(true);

                regLquestionList();
            });
            slider.play();
        } else if (event.getSource() == side_alreadyHave) {
            slider.setNode(side_form);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(.5));

            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(false);
                side_CreateBtn.setVisible(true);
                s_forgotForm.setVisible(false);
                s_forgotFormpass.setVisible(false);

            });
            slider.play();
        }
    }

    public void switchfForm() {
        System.out.println("Attempting to load forgot form.fxml");
        s_forgotForm.setVisible(true);
        L_loginForm.setVisible(false);
        forgotPassQuestion();
    }

    public void switchsForm() {
        System.out.println("Attempting to load login form.fxml");
        s_forgotForm.setVisible(false);
        s_forgotFormpass.setVisible(false);
        L_loginForm.setVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        regLquestionList();
        forgotPassQuestion();
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        String username = L_username.getText();
        setLoggedInUsername(username);
        String password = L_password.getText();

        // Kiểm tra dữ liệu nhập vào
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password", "Please check your credentials and try again.");
            return;
        }

        try {
            if (socketHandle == null) {
                throw new IOException("SocketHandle is not initialized.");
            }

            String loginMessage = "LOGIN:" + username + ":" + password; // Định dạng chuẩn
            socketHandle.sendMessage(loginMessage);

            String response = socketHandle.receiveMessage();
            System.out.println("Server response: " + response);


            if (response != null && response.equals("LOGIN_SUCCESS")) {
                loggedInUsername = username; // Store logged-in username
                client.showClientMainView(loggedInUsername);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password", "Please check your credentials and try again.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Unable to connect to server", "Please try again later.");
        }
    }

    @FXML
    private void handleSignupButton(ActionEvent event) {
        String username = s_username.getText();
        String password = s_password.getText();
        String answer = s_answer.getText();
        String question = "";

        if (s_question.getValue() != null) {
            question = s_question.getValue().toString();
        }

        if (username.isEmpty() || password.isEmpty() || question.isEmpty() || answer.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Incomplete Information", "Please fill in all fields.");
            return;
        }

        try {

            String registerMessage = "REGISTER:" + username + ":" + password + ":" + question + ":" + answer;
            socketHandle.sendMessage(registerMessage);


            String response = socketHandle.receiveMessage();
            System.out.println(response);

            if ("REGISTER_SUCCESS".equals(response)) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User Registered Successfully", "You have been registered successfully. Please log in.");

                TranslateTransition slider = new TranslateTransition();
                slider.setNode(side_form);
                slider.setToX(0);
                slider.setDuration(Duration.seconds(.5));

                slider.setOnFinished((ActionEvent e) -> {
                    side_alreadyHave.setVisible(false);
                    side_CreateBtn.setVisible(true);
                    s_forgotForm.setVisible(false);
                });
                slider.play();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Registration Failed", "An error occurred during registration. Please try again.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Unable to connect to server", "Please try again later.");
        }
    }

    @FXML
    private void handleContinueButton(ActionEvent event) {
        String username = s_username1.getText();
        String question = s_question1.getValue() != null ? s_question1.getValue().toString() : "";
        String answer = s_answer1.getText();

        if (username.isEmpty() || question.isEmpty() || answer.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Incomplete Information", "Please fill in all fields.");
            return;
        }

        try {

            String checkUserMessage = "CHECK_USER:" + username;
            socketHandle.sendMessage(checkUserMessage);

            String userExistResponse = socketHandle.receiveMessage();
            if ("USER_NOT_FOUND".equals(userExistResponse)) {
                showAlert(Alert.AlertType.ERROR, "Error", "User Not Found", "The username does not exist.");
                return;
            }

            String validateSecurityMessage = "VALIDATE_SECURITY:" + username + ":" + question + ":" + answer;
            socketHandle.sendMessage(validateSecurityMessage);

            String validateResponse = socketHandle.receiveMessage();
            if ("VALIDATION_SUCCESS".equals(validateResponse)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Security Check Passed", "Your security question and answer are correct.");
                s_forgotForm.setVisible(false);
                s_forgotFormpass.setVisible(true);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Security Check Failed", "Your security question and answer do not match our records.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Connection Error", "Unable to connect to server. Please try again later.");
        }
    }

    @FXML
    private void handlePasswordReset(ActionEvent event) {
        String newPass = p_pass.getText();
        String confirmPass = p_Cpass.getText();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Incomplete Information", "Please fill in all fields.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Password Mismatch", "Passwords do not match.");
            return;
        }

        String username = s_username1.getText();

        try {

            String updatePasswordMessage = "RESET_PASSWORD:" + username + ":" + newPass; // Điều chỉnh phần này
            socketHandle.sendMessage(updatePasswordMessage);

            String updateResponse = socketHandle.receiveMessage();
            if ("RESET_SUCCESS".equals(updateResponse)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password Changed", "Your password has been changed successfully.");
                s_forgotFormpass.setVisible(false);
                L_loginForm.setVisible(true);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Update Failed", "Failed to update the password.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Connection Error", "Unable to connect to server. Please try again later.");
        }
    }

    private boolean updateUserPassword(String username, String newPassword) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Users user = session.get(Users.class, username);
            if (user != null) {
                user.setPassword(newPassword);
                session.update(user);
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private boolean isUserExist(String username) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Users user = session.get(Users.class, username);
            transaction.commit();
            return user != null;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    private boolean validateSecurityAnswer(String username, String question, String answer) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Users user = session.get(Users.class, username);
            if (user != null && user.getQuestion().equals(question) && user.getAnswer().equals(answer)) {
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
                return false;
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

//    private boolean validateUser(String username, String password) {
//        Session session = sessionFactory.openSession();
//        Transaction transaction = null;
//        try {
//            transaction = session.beginTransaction();
//            Users user = session.get(Users.class, username);
//            if (user != null && user.getPassword().equals(password)) {
//                transaction.commit();
//                return true;
//            } else {
//                transaction.rollback();
//                return false;
//            }
//        } finally {
//            session.close();
//        }
//    }

    private void saveUser(String username, String password, String question, String answer) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Users user = new Users();
        user.setUsername(username);
        user.setPassword(password);
        user.setQuestion(question);
        user.setAnswer(answer);

        session.save(user);

        transaction.commit();
        session.close();
    }

    private void switchToMainScene() {
        try {
            System.out.println("Attempting to load mainForm.fxml");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/project/projectjava/mainForm.fxml"));
            Parent root = loader.load();

            mainFormController mainFormController = loader.getController();
            mainFormController.setLoggedInUsername(loggedInUsername); // Pass logged-in username

            Stage currentStage = (Stage) L_username.getScene().getWindow();

            Scene newScene = new Scene(root);

            currentStage.setScene(newScene);
            currentStage.show();

            System.out.println("Successfully loaded mainForm.fxml and switched scenes");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading mainForm.fxml: " + e.getMessage());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText("Unable to open mainForm.fxml");
            alert.setContentText("There was an error loading the mainForm.fxml file. Please check the file and try again.");
            alert.showAndWait();
        }
    }

    private void switchToClientMainScene() {
        try {
            System.out.println("Attempting to load mainForm.fxml");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/project/projectjava/ClientMainForm.fxml"));
            Parent root = loader.load();

            mainFormController mainFormController = loader.getController();
            mainFormController.setLoggedInUsername(loggedInUsername); // Pass logged-in username

            Stage currentStage = (Stage) L_username.getScene().getWindow();

            Scene newScene = new Scene(root);

            currentStage.setScene(newScene);
            currentStage.show();

            System.out.println("Successfully loaded mainForm.fxml and switched scenes");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading mainForm.fxml: " + e.getMessage());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText("Unable to open mainForm.fxml");
            alert.setContentText("There was an error loading the mainForm.fxml file. Please check the file and try again.");
            alert.showAndWait();
        }
    }

    private void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }
}
