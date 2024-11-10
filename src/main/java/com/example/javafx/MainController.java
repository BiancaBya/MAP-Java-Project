package com.example.javafx;

import Domain.Friendship;
import Domain.Tuple;
import Service.Service;
import Domain.Utilizator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainController {

    private Service service;

    @FXML
    private TableView<Utilizator> friendTable;

    @FXML
    private TableColumn<Utilizator, String> friendFirstNameColumn;

    @FXML
    private TableColumn<Utilizator, String> friendLastNameColumn;

    @FXML
    private HBox HBox;

    @FXML
    private Button addFriendButton;

    @FXML
    private Button deleteFriendButton;

    @FXML
    private TextField friendsFirstNameField;

    @FXML
    private Label messageLabel;


    private Utilizator user;

    public void setService(Service service) {
        this.service = service;
    }

    public void setUser(Utilizator user){
        this.user = user;
        loadFriends();
    }

    private void loadFriends(){

        friendTable.getItems().clear();

        List<Utilizator> lista = service.get_users_friends(user);
        ObservableList<Utilizator> friends = FXCollections.observableList(lista);
        friendTable.setItems(friends);

        friendFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        friendLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

    }


    public void onButtonAddFriend() {

        if(friendsFirstNameField.textProperty().isEmpty().get()){
            messageLabel.setText("Add friend's first name");
        }

        else{
            messageLabel.setText("");

            String firstName = friendsFirstNameField.textProperty().get();
            Long id = service.get_user_id_by_name(firstName);
            Optional<Utilizator> friend = service.find_user(id);
            if(friend.isPresent()){

                Optional<Friendship> friendship = service.find_friendship(new Tuple<>(user.getId(), friend.get().getId()));
                if(friendship.isPresent()){
                    messageLabel.setText("Friend already exists");
                }
                else{
                    service.add_friendship(user.getId(), friend.get().getId());
                    loadFriends();
                }

            } else{
                messageLabel.setText("User not found");
            }


        }

    }

    public void onButtonDeleteFriend() {

        if(friendsFirstNameField.textProperty().isEmpty().get()){
            messageLabel.setText("Add friend's first name");
        }

        else {
            messageLabel.setText("");

            String firstName = friendsFirstNameField.textProperty().get();
            Long id = service.get_user_id_by_name(firstName);
            Optional<Utilizator> friend = service.find_user(id);

            if(friend.isPresent()){

                Optional<Friendship> friendship = service.find_friendship(new Tuple<>(user.getId(), friend.get().getId()));
                if(friendship.isPresent()){
                    service.remove_friendship(user.getId(), friend.get().getId());
                    loadFriends();
                } else{
                    messageLabel.setText("Friend not found");
                }

            } else{
                messageLabel.setText("User not found");
            }

        }

    }

}

