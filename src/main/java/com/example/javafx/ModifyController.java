package com.example.javafx;

import Domain.User;
import Service.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ModifyController {

    private Service service;

    private User user;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;


    public void setService(Service service) {
        this.service = service;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void onButtonModify(){

        String firstName = firstNameField.getText().isEmpty() ? user.getFirstName() : firstNameField.getText();
        String lastName = lastNameField.getText().isEmpty() ? user.getLastName() : lastNameField.getText();
        String email = emailField.getText().isEmpty() ? user.getEmail() : emailField.getText();
        Long id = user.getId();

        User new_user = new User(firstName, lastName, user.getPassword(), email);
        new_user.setId(id);

        service.updateUser(new_user);
        messageLabel.setText("User updated");

        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();

    }


    public void onButtonBack(){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Stage stage = (Stage) firstNameField.getScene().getWindow();
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
