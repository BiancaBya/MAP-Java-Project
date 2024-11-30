package com.example.javafx;

import Console.Console;
import Domain.Friendship;
import Domain.Message;
import Domain.Tuple;
import Domain.Utilizator;
import Domain.Validators.FriendshipValidator;
import Domain.Validators.UtilizatorValidator;
import Repository.DataBase.FriendshipDataBaseRepository;
import Repository.DataBase.MessageDataBaseRepository;
import Repository.DataBase.UserDataBaseRepository;
import Repository.FriendshipPagingRepository;
import Repository.Repository;
import Service.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        String username = "postgres";
        String password = "parola";
        String url = "jdbc:postgresql://localhost:5432/LabMAP";

        Repository<Long, Utilizator> userDBRepo = new UserDataBaseRepository(url, username, password, new UtilizatorValidator());
        FriendshipPagingRepository<Tuple<Long, Long>, Friendship> friendDBRepo = new FriendshipDataBaseRepository(url, username, password, new FriendshipValidator());
        Repository<Long, Message> messageDBRepo = new MessageDataBaseRepository(userDBRepo, url, username, password);

        Service service = new Service(userDBRepo, friendDBRepo, messageDBRepo);


        FXMLLoader Loader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(Loader.load(), 520, 550);

        LoginController controller = Loader.getController();
        controller.setService(service);

        stage.setTitle("Social Network");
        stage.setScene(scene);
        stage.show();

    }

}



