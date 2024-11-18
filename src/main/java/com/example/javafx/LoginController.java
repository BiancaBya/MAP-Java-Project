package com.example.javafx;

import Service.Service;
import Domain.Utilizator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoginController {

    private Service service;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField signUpEmailField;

    @FXML
    private PasswordField signUpPasswordField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMessage;

    @FXML
    private Label signupMessage;


    public void setService(Service service) {
        this.service = service;
    }

    public void onLoginClicked(){

        String email = emailField.getText();
        String password = passwordField.getText();

        Long id = service.get_user_id_by_email(email);
        Optional<Utilizator> user = service.find_user(id);

        if(user.isPresent()){

            if(password.equals(user.get().getPassword())){
                openMainScene(user.get());
            }else{
                loginMessage.setText("Wrong Password");
            }

        } else{
            loginMessage.setText("User not found");
        }
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

    private void openMainScene(Utilizator user){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Stage stage = (Stage) loginMessage.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(Loader.load()));

            MainController mainController = Loader.getController();
            mainController.setUser(user);
            mainController.setService(service);

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

}


