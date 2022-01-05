package map.socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import map.socialnetwork.Main;
import map.socialnetwork.domain.model.User;
import map.socialnetwork.domain.validator.UserValidator;
import map.socialnetwork.repository.database.UserDatabaseRepository;
import map.socialnetwork.util.PasswordUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LogInController {

    @FXML
    public Button createNewAccountButton;
    @FXML
    private Button logInButton;
    @FXML
    private Label wrongLogIn;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    private final UserDatabaseRepository userDatabaseRepository = new UserDatabaseRepository(new UserValidator());

    public LogInController() {
    }

    public void userLogIn(ActionEvent event) throws IOException {
        checkLogIn();
        createNewAccountButton.setVisible(true);
    }

    private void checkLogIn() throws IOException {

        if(!username.getText().isBlank() && !password.getText().isBlank()) {
            User user = userDatabaseRepository.findOneByUsername(username.getText());
            Long id = userDatabaseRepository.findOneByUsername(username.getText()).getId();
            if(user != null && PasswordUtil.authenticate(password.getText().toCharArray(), user.getPassword())) {
                Main.switchSceneWithParameters("main-view.fxml", Collections.singletonMap("userId", id));
            } else {
                wrongLogIn.setText("Incorrect username or password!");
            }
        }
        else {
            wrongLogIn.setText("Please enter username and password!");
        }
    }


    public void userCreateNewAccount(ActionEvent event) throws IOException {
        Main.switchScene("register-view.fxml");
    }

}
