package Repository.DataBase;

import Domain.Validators.Validator;
import Repository.Repository;
import Domain.Utilizator;

import java.sql.*;
import java.util.*;

public class UserDataBaseRepository implements Repository<Long, Utilizator>{

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Utilizator> validator;
    Map<Long, Utilizator> users = new HashMap<>();


    public UserDataBaseRepository(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.password = password;
        this.username = username;
        this.validator = validator;
        loadData();
    }


    @Override
    public Optional<Utilizator> findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("id cannot be null");
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Iterable<Utilizator> findAll() {

        Set<Utilizator> usersDataBase = new HashSet<>();

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String password = resultSet.getString("password");

                Utilizator utilizator = new Utilizator(firstName, lastName, password);
                utilizator.setId(id);
                usersDataBase.add(utilizator);
            }

            return usersDataBase;

        }catch (SQLException e){
            e.printStackTrace();
        }

        return usersDataBase;
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        validator.validate(entity);

        int rez = -1;

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Users (firstname, lastname, password) VALUES (?, ?, ?)");
        ){

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getPassword());
            rez = statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }

        if (rez > 0) {
            users.put(entity.getId(), entity);
            loadData();
            return Optional.empty();
        }
        else
            return Optional.of(entity);

    }

    @Override
    public Optional<Utilizator> delete(Long id) {

        Optional<Utilizator> user = Optional.ofNullable(users.get(id));
        int rez = -1;

        if (user.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM Users WHERE id = ?")) {

                statement.setLong(1, id);
                rez = statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (rez > 0) {
                users.remove(id);
                loadData();
                return user;
            }
        }

        return Optional.empty();
    }


    @Override
    public Optional<Utilizator> update(Utilizator entity) {

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        validator.validate(entity);

        Optional<Utilizator> existingUser = Optional.ofNullable(users.get(entity.getId()));
        int rez = -1;

        if (existingUser.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("UPDATE Users SET firstname = ?, lastname = ? WHERE id = ?")) {

                statement.setString(1, entity.getFirstName());
                statement.setString(2, entity.getLastName());
                statement.setLong(3, entity.getId());

                rez = statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (rez > 0) {
                users.put(entity.getId(), entity);
                loadData();
                return Optional.empty();
            }
        }

        return Optional.of(entity);
    }


    private void loadData(){
        findAll().forEach(user -> {
            users.put(user.getId(), user);
        });
    }

}


