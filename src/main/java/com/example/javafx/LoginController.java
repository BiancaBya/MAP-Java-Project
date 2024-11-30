package com.example.javafx;

import Service.Service;
import Domain.Utilizator;
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

        Long id = service.getUserIdByEmail(email);
        Optional<Utilizator> user = service.findUser(id);

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

    private void openMainScene(Utilizator user){

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


