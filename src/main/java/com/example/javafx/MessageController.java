package com.example.javafx;

import Domain.Message;
import Domain.Utilizator;
import Service.Service;

import Utils.Observer.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class MessageController {

    private Service service;

    private Utilizator user;

    private Utilizator friend;

    private ObservableList<Message> model = FXCollections.observableArrayList();


    @FXML
    private ListView<Message> messageListView;

    @FXML
    private TextField messageTextField;


    public void setService(Service service){
        this.service = service;
        initModel();
    }

    public void setUser(Utilizator user){
        this.user = user;
    }

    public void setFriend(Utilizator friend){
        this.friend = friend;
    }


    private void initModel(){

        model.setAll(service.getMessagesBetween(user, friend));

    }

    @FXML
    private void initialize(){

        messageListView.setCellFactory(param -> new ListCell<>() {

            @Override
            protected void updateItem(Message message, boolean empty) {

                super.updateItem(message, empty);
                if(empty || message == null){

                    setText(null);
                    setGraphic(null);

                }else{

                    if(message.getFrom().equals(user)){
                        setText("You: " + message.getMessage());
                    }else{
                        setText(friend.getFirstName() + ": " + message.getMessage());
                    }

                }

            }

        });

        messageListView.setItems(model);

    }

    private void addMessage(Message message){

        if(service.addMessage(user, friend, message.getMessage()))
            model.add(message);

    }


    public void onSendButtonClicked(){

        String text = messageTextField.getText();
        if(!text.isEmpty()){
            Message message = new Message(user, List.of(friend), text, LocalDateTime.now());
            addMessage(message);
            messageTextField.clear();
        }

    }

    public void onBackButtonClicked(){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Stage stage = (Stage) messageTextField.getScene().getWindow();
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


