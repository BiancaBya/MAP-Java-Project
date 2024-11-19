package com.example.javafx;

import Domain.Friendship;
import Domain.Tuple;
import Domain.Utilizator;
import Service.Service;
import Utils.Observer.Observable;
import Utils.Observer.Observer;
import Utils.Events.ChangeEventType;
import Utils.Events.EntityChangeEvent;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestsController implements Observer<EntityChangeEvent> {

    private Service service;

    private Utilizator user;

    private ObservableList<Friendship> model = FXCollections.observableArrayList();

    @FXML
    private Label messageLabel;

    @FXML
    private HBox buttonsHBox;

    @FXML
    private Button acceptFriendshipButton;

    @FXML
    private Button rejectFriendshipButton;

    @FXML
    private TextField friendsFirstNameTextField;

    @FXML
    private TableView<Friendship> requestsTable;

    @FXML
    private TableColumn<Friendship, String> firstNameColumn;

    @FXML
    private TableColumn<Friendship, String> lastNameColumn;

    @FXML
    private TableColumn<Friendship, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Friendship, String> statusColumn;


    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        initModel();
    }

    public void setUser(Utilizator user) {
        this.user = user;
    }

    void initModel() {

        List<Friendship> friendships = getUsersFriends();

        ObservableList<Friendship> friends = FXCollections.observableList(friendships);
        requestsTable.setItems(friends);

    }


    @FXML
    public void initialize() {

        firstNameColumn.prefWidthProperty().bind(requestsTable.widthProperty().multiply(0.25));
        lastNameColumn.prefWidthProperty().bind(requestsTable.widthProperty().multiply(0.25));
        dateColumn.prefWidthProperty().bind(requestsTable.widthProperty().multiply(0.25));
        statusColumn.prefWidthProperty().bind(requestsTable.widthProperty().multiply(0.25));

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        firstNameColumn.setCellValueFactory(data -> {
            Friendship friendship = data.getValue();
            Utilizator friend = (friendship.getId_user_1().equals(user.getId())) ?
                    service.find_user(friendship.getId_user_2()).get() :
                    service.find_user(friendship.getId_user_1()).get();
            return new SimpleStringProperty(friend.getFirstName());

        });

        lastNameColumn.setCellValueFactory(data -> {
            Friendship friendship = data.getValue();
            Utilizator friend = (friendship.getId_user_1().equals(user.getId())) ?
                    service.find_user(friendship.getId_user_2()).get() :
                    service.find_user(friendship.getId_user_1()).get();
            return new SimpleStringProperty(friend.getLastName());

        });

        requestsTable.setItems(model);

    }


    public void onAcceptButtonClicked() {

        String firstName = friendsFirstNameTextField.getText();

        if(firstName.isEmpty()){
            messageLabel.setText("Please enter the first name");
        }
        else{
            Long id = service.get_user_id_by_name(firstName);
            Optional<Utilizator> friend = service.find_user(id);
            if(friend.isPresent()){

                Optional<Friendship> friendship = service.find_friendship(new Tuple<>(user.getId(), friend.get().getId()));
                if(friendship.isPresent()){
                    if(friendship.get().getStatus().equals("Requested")){

                        if (friendship.get().getId_request().equals(user.getId())){
                            messageLabel.setText("Wait until the user accepts the request");
                        }
                        else {
                            friendship.get().setStatus("Friends");
                            friendship.get().setDate(LocalDateTime.now());
                            service.update_friendship(friendship.get());
                        }

                    }
                    else{
                        messageLabel.setText("Friendship already accepted");
                    }
                }
                else{
                    messageLabel.setText("Friendship not found");
                }

            }
            else{
                messageLabel.setText("User not found");
            }

        }

    }

    public void onRejectButtonClicked() {

        String firstName = friendsFirstNameTextField.getText();

        if(firstName.isEmpty()){
            messageLabel.setText("Please enter the first name");
        }
        else{
            Long id = service.get_user_id_by_name(firstName);
            Optional<Utilizator> friend = service.find_user(id);
            if(friend.isPresent()){

                Optional<Friendship> friendship = service.find_friendship(new Tuple<>(user.getId(), friend.get().getId()));
                if(friendship.isPresent()){

                    if(friendship.get().getStatus().equals("Requested")) {
                        service.remove_friendship(friendship.get().getId_user_1(), friendship.get().getId_user_2());
                    }
                    else if(friendship.get().getStatus().equals("Friends")) {
                        messageLabel.setText("You are already friends");
                    }

                }
                else{
                    messageLabel.setText("Friendship not found");
                }

            }
            else{
                messageLabel.setText("User not found");
            }

        }

    }


    private List<Friendship> getUsersFriends(){

        Iterable<Friendship> iterable = service.findAll_friendships();

        List<Friendship> friendships = new ArrayList<>();
        for (Friendship friendship : iterable) {
            if(friendship.getId_user_1().equals(user.getId()) || friendship.getId_user_2().equals(user.getId()))
                if(friendship.getStatus().equals("Friends") || !friendship.getId_request().equals(user.getId()))
                    friendships.add(friendship);
        }

        return friendships;
    }

    public void onButtonBackClicked(){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Stage stage = (Stage) messageLabel.getScene().getWindow();
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

    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        initModel();
        requestsTable.refresh();
    }
}



