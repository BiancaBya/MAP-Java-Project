package com.example.javafx;

import Domain.Friendship;
import Domain.Tuple;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private Button friendRequestButton;

    @FXML
    private TextField friendsFirstNameField;

    @FXML
    private Label messageLabel;

    @FXML
    private HBox HBoxDeleteModify;

    @FXML
    private Button Delete;

    @FXML
    private Button Modify;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;


    private Utilizator user;

    public void setService(Service service) {
        this.service = service;
    }

    public void setUser(Utilizator user){
        this.user = user;
        loadFriends();
    }

    @FXML
    private void initialize() {

        friendFirstNameColumn.prefWidthProperty().bind(friendTable.widthProperty().multiply(0.5));
        friendLastNameColumn.prefWidthProperty().bind(friendTable.widthProperty().multiply(0.5));

    }

    private void loadFriends(){

        friendTable.getItems().clear();

        List<Utilizator> lista = service.get_users_friends(user);
        List<Utilizator> userFriends = new ArrayList<>();
        for(Utilizator u : lista){
            if(Objects.equals(service.find_friendship(new Tuple<>(user.getId(), u.getId())).get().getStatus(), "Friends"))
                userFriends.add(u);
        }
        ObservableList<Utilizator> friends = FXCollections.observableList(userFriends);
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
                    if(friendship.get().getStatus().equals("Friends")) {
                        messageLabel.setText("Friend already exists");
                    }
                    else if (friendship.get().getStatus().equals("Requested")) {
                        friendship.get().setStatus("Friends");
                        friendship.get().setDate(LocalDateTime.now());
                        service.update_friendship(friendship.get());
                        messageLabel.setText("Friend added");
                        loadFriends();
                    }
                }
                else{
                    service.add_friendship(user.getId(), friend.get().getId(), user.getId());
                    messageLabel.setText("Friend request sent");
                    loadFriends();
                }

            } else{
                messageLabel.setText("User not found");
            }


        }

        friendsFirstNameField.clear();

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

        friendsFirstNameField.clear();

    }

    public void onButtonDelete(){

        service.remove_user(user);

        onButtonBackClicked();
    }

    public void onButtonModify(){

        String firstName = firstNameField.getText().isEmpty() ? user.getFirstName() : firstNameField.getText();
        String lastName = lastNameField.getText().isEmpty() ? user.getLastName() : lastNameField.getText();
        String email = emailField.getText().isEmpty() ? user.getEmail() : emailField.getText();
        Long id = user.getId();

        Utilizator new_user = new Utilizator(firstName, lastName, user.getPassword(), email);
        new_user.setId(id);

        service.update_user(new_user);

        firstNameField.clear();;
        lastNameField.clear();
        emailField.clear();

    }

    public void onFriendRequestButton(){
        openMainScene();
    }


    private void openMainScene(){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("requests-view.fxml"));
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setTitle("Friend Requests");
            stage.setScene(new Scene(Loader.load()));

            RequestsController requestsController = Loader.getController();
            requestsController.setService(service);
            requestsController.setUser(user);

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void onButtonBackClicked(){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setTitle("Social Network");
            stage.setScene(new Scene(Loader.load(), 420, 480));

            LoginController controller = Loader.getController();
            controller.setService(service);

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

}



