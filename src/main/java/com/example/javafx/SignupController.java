package com.example.javafx;

import Domain.Utilizator;
import Service.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class SignupController {

    private Service service;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField signUpEmailField;

    @FXML
    private PasswordField signUpPasswordField;

    @FXML
    private Label signupMessage;

    public void setService(Service service) {
        this.service = service;
    }


    public void onSignUpClicked(){

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = signUpEmailField.getText();
        String password = signUpPasswordField.getText();

        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()){
            signupMessage.setText("Please fill all the fields");
        } else {

            Long id = service.get_user_id_by_email(email);
            Optional<Utilizator> user = service.find_user(id);

            if (user.isPresent()) {

                signupMessage.setText("User already exists");

            } else {

                Utilizator new_user = new Utilizator(firstName, lastName, password, email);
                service.add_user(new_user);
                signupMessage.setText("User created");
                firstNameField.clear();
                lastNameField.clear();
                signUpEmailField.clear();
                signUpPasswordField.clear();

            }
        }

    }

    public void onButtonBackClicked(){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Stage stage = (Stage) signupMessage.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(Loader.load(), 520, 550));

            LoginController controller = Loader.getController();
            controller.setService(service);

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
