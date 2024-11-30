package com.example.javafx;

import Domain.Friendship;
import Domain.Tuple;
import Service.Service;
import Domain.Utilizator;
import Utils.Observer.Observable;
import Utils.Observer.Observer;
import Utils.Events.EntityChangeEvent;

import Utils.Paging.Page;
import Utils.Paging.Pageable;
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

public class MainController implements Observer<EntityChangeEvent>{

    private Service service;

    private Utilizator user;

    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    private int currentPage = 0;

    private int pageSize = 10;

    @FXML
    private Label usersNamesLabel;

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
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private Label pageNumber;



    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        initModel();
    }

    public void setUser(Utilizator user){
        this.user = user;
    }

    @FXML
    private void initialize() {

        friendFirstNameColumn.prefWidthProperty().bind(friendTable.widthProperty().multiply(0.5));
        friendLastNameColumn.prefWidthProperty().bind(friendTable.widthProperty().multiply(0.5));

        friendFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        friendLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        friendTable.setItems(model);

    }


    private void initModel(){

        boolean hasRequests = false;

        for (Utilizator u : service.getUsersFriends(user)){
            Friendship friendship = service.findFriendship(new Tuple<>(u.getId(), user.getId())).get();
            if(friendship.getStatus().equals("Requested") && !friendship.getId_request().equals(user.getId()))
                hasRequests = true;
        }

        if(hasRequests){
            friendRequestButton.setText("Friend Requests (new)");
        }

        if(service.findUser(user.getId()).isPresent()){
            user = service.findUser(user.getId()).get();
            usersNamesLabel.setText(user.getFirstName() + " " + user.getLastName());
        }


        Page<Friendship> pageFriendships = service.findUsersFriends(new Pageable(pageSize, currentPage), user);

        int maximumFriendships = (int) Math.ceil((double) pageFriendships.getTotalNumberOfElements() / pageSize) - 1;
        if(maximumFriendships == -1){
            maximumFriendships = 0;
        }

        if(currentPage > maximumFriendships){
            currentPage = maximumFriendships;
            pageFriendships = service.getAllFriendships(new Pageable(pageSize, currentPage));
        }

        int nrOfElements = pageFriendships.getTotalNumberOfElements();

        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * pageSize >= nrOfElements);

        ObservableList<Utilizator> friends = FXCollections.observableArrayList();

        for (Friendship friendship : pageFriendships.getElementsOnPage()){

            Utilizator friend = friendship.getId_user_1().equals(user.getId()) ? service.findUser(friendship.getId_user_2()).get()
                    : service.findUser(friendship.getId_user_1()).get();
            friends.add(friend);
        }

        friendTable.setItems(friends);
        pageNumber.setText((currentPage + 1) + " / " + (maximumFriendships + 1));

    }


    public void onButtonAddFriend() {

        if(friendsFirstNameField.textProperty().isEmpty().get()){
            messageLabel.setText("Add friend's first name");
        }

        else{
            messageLabel.setText("");

            String firstName = friendsFirstNameField.textProperty().get();
            Long id = service.getUserIdByName(firstName);
            Optional<Utilizator> friend = service.findUser(id);
            if(friend.isPresent()){

                Optional<Friendship> friendship = service.findFriendship(new Tuple<>(user.getId(), friend.get().getId()));
                if(friendship.isPresent()){

                    if(friendship.get().getStatus().equals("Friends")) {
                        messageLabel.setText("Friend already exists");
                    }

                    else if (friendship.get().getStatus().equals("Requested")) {

                        if (friendship.get().getId_request().equals(user.getId())){
                            messageLabel.setText("Wait until the user accepts your request");
                        }

                        else {
                            friendship.get().setStatus("Friends");
                            friendship.get().setDate(LocalDateTime.now());
                            service.updateFriendship(friendship.get());
                            messageLabel.setText("Friend added");
                        }

                    }

                }
                else{
                    service.addFriendship(user.getId(), friend.get().getId(), user.getId());
                    messageLabel.setText("Friend request sent");
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
            Long id = service.getUserIdByName(firstName);
            Optional<Utilizator> friend = service.findUser(id);

            if(friend.isPresent()){

                Optional<Friendship> friendship = service.findFriendship(new Tuple<>(user.getId(), friend.get().getId()));
                if(friendship.isPresent()){
                    service.removeFriendship(user.getId(), friend.get().getId());
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

        service.removeUser(user);

        onButtonBackClicked();
    }

    public void onButtonModify(){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("modify-view.fxml"));
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setTitle("Modify Account");
            stage.setScene(new Scene(Loader.load(), 520, 550));

            ModifyController modifyController = Loader.getController();
            modifyController.setUser(user);
            modifyController.setService(service);

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

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
            requestsController.setUser(user);
            requestsController.setService(service);

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
            stage.setScene(new Scene(Loader.load(), 520, 550));

            LoginController controller = Loader.getController();
            controller.setService(service);

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void openChat(Utilizator friend){

        try{

            FXMLLoader Loader = new FXMLLoader(getClass().getResource("message-view.fxml"));
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setTitle("Chat");
            stage.setScene(new Scene(Loader.load(), 520, 550));

            MessageController controller = Loader.getController();
            controller.setUser(user);
            controller.setFriend(friend);
            controller.setService(service);

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }


    public void onButtonChatClicked(){

        String friendName = friendsFirstNameField.getText();
        Long id_friend = service.getUserIdByName(friendName);
        Optional<Utilizator> friend = service.findUser(id_friend);

        if(friend.isPresent()) {
            openChat(friend.get());
        }

        else{
            messageLabel.setText("Friend not found");
        }

    }


    public void onButtonNextClicked(){
        currentPage++;
        initModel();
    }

    public void onButtonPreviousClicked(){
        currentPage--;
        initModel();
    }



    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        initModel();
        friendTable.refresh();
    }

}



