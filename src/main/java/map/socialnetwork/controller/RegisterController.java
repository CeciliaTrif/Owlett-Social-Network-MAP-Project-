package map.socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import map.socialnetwork.Main;
import map.socialnetwork.domain.model.User;
import map.socialnetwork.domain.validator.UserValidator;
import map.socialnetwork.domain.validator.ValidationException;
import map.socialnetwork.repository.database.UserDatabaseRepository;
import map.socialnetwork.util.Container;
import map.socialnetwork.service.UserService;

import java.io.IOException;

public class RegisterController {

    @FXML
    public TextField firstName;
    @FXML
    public TextField lastName;
    @FXML
    public TextField newPassword;
    @FXML
    public TextField newUsername;
    @FXML
    public Button signUpButton;
    @FXML
    public Label signUpMessage;
    private final UserDatabaseRepository userDatabaseRepository = new UserDatabaseRepository(new UserValidator());
    private final UserService userService = (UserService) Container.getService("user");
    public Button backButton;

    public void userSignUp(ActionEvent event) throws IOException {
        checkSignUp();
    }

    private void checkSignUp() throws ValidationException, IOException {
        if (!firstName.getText().matches("[a-zA-Z]+") || !lastName.getText().matches("[a-zA-Z]+")) {
            signUpMessage.setText("First and last name can only contain letters!");
        } else if (userDatabaseRepository.getUsername(newUsername.getText()) != null) {
            signUpMessage.setText("Username already taken!");
        } else if (newPassword.getText().isBlank() && newUsername.getText().isBlank()) {
            signUpMessage.setText("Please enter username and password!");
        } else if (newUsername.getText().isBlank()) {
            signUpMessage.setText("Please enter a username!");
        } else if (newPassword.getText().isBlank()) {
            signUpMessage.setText("Please enter a password!");
        } else {
            User user = new User(newUsername.getText(), newPassword.getText(), firstName.getText(), lastName.getText());
            user.setId(userDatabaseRepository.generateNextID());
            userDatabaseRepository.save(user);
            signUpMessage.setText("Account created.");
        }
    }


    public void backToLogIn(ActionEvent event) throws IOException {
        Main.switchScene("login-view.fxml");
    }
}
