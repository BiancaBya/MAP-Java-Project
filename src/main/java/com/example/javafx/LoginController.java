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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoginController {

    private Service service;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMessage;


    public void setService(Service service) {
        this.service = service;
    }

    public void onLoginClicked(){

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String password = passwordField.getText();

        Long id = service.get_user_id_by_name(firstName);
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

    private void openMainScene(Utilizator user){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Stage stage = (Stage) loginMessage.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(Loader.load()));

            MainController mainController = Loader.getController();
            mainController.setService(service);
            mainController.setUser(user);

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
