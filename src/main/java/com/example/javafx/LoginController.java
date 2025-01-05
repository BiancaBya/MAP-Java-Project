package com.example.javafx;

import Service.Service;
import Domain.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    private Service service;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMessage;


    public void setService(Service service) {
        this.service = service;
    }

    public void onLoginClicked(){

        String email = emailField.getText();
        String password = passwordField.getText();

        User logedInUser = service.login(email, password);

        if(logedInUser != null){

            openMainScene(logedInUser);

        } else{
            loginMessage.setText("Wrong credentials");
        }
    }

    public void onSignUpClicked(){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("signup-view.fxml"));
            Stage stage = (Stage) loginMessage.getScene().getWindow();
            stage.setTitle("Sign Up");
            stage.setScene(new Scene(Loader.load(), 520, 550));

            SignupController signupController = Loader.getController();
            signupController.setService(service);

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void openMainScene(User user){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Stage stage = (Stage) loginMessage.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(Loader.load(), 790, 720));

            MainController mainController = Loader.getController();
            mainController.setUser(user);
            mainController.setService(service);

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

}


