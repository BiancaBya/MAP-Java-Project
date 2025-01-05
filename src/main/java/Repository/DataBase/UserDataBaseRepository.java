package Repository.DataBase;

import Domain.Validators.Validator;
import Domain.User;
import Repository.PagingRepository;
import Utils.Paging.Page;
import Utils.Paging.Pageable;

import java.sql.*;
import java.util.*;

public class UserDataBaseRepository implements PagingRepository<Long, User> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;
    Map<Long, User> users = new HashMap<>();


    public UserDataBaseRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.password = password;
        this.username = username;
        this.validator = validator;
        loadData();
    }


    @Override
    public Optional<User> findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("id cannot be null");
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Iterable<User> findAll() {

        Set<User> usersDataBase = new HashSet<>();

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){

                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");

                User utilizator = new User(firstName, lastName, password, email);
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
    public Optional<User> save(User entity) {

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        validator.validate(entity);

        int rez = -1;

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Users (firstname, lastname, password, email) VALUES (?, ?, ?, ?)");
        ){

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getPassword());
            statement.setString(4, entity.getEmail());
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
    public Optional<User> delete(Long id) {

        Optional<User> user = Optional.ofNullable(users.get(id));
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
    public Optional<User> update(User entity) {

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        validator.validate(entity);

        Optional<User> existingUser = Optional.ofNullable(users.get(entity.getId()));
        int rez = -1;

        if (existingUser.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("UPDATE Users SET firstname = ?, lastname = ?, email = ? WHERE id = ?")) {

                statement.setString(1, entity.getFirstName());
                statement.setString(2, entity.getLastName());
                statement.setString(3, entity.getEmail());
                statement.setLong(4, entity.getId());

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

    @Override
    public Page<User> findAllOnPage(Pageable pageable) {

        List<User> usersList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement pageStatement = connection.prepareStatement("SELECT * FROM Users " + "LIMIT ? OFFSET ?");
        PreparedStatement countStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM Users")
        ){

            pageStatement.setInt(1, pageable.getPageSize());
            pageStatement.setInt(2, pageable.getPageNumber() * pageable.getPageSize());
            try (ResultSet pageResultSet = pageStatement.executeQuery();
                 ResultSet countResultSet = countStatement.executeQuery()){

                while(pageResultSet.next()){

                    Long id = pageResultSet.getLong("id");
                    String firstName = pageResultSet.getString("firstName");
                    String lastName = pageResultSet.getString("lastName");
                    String password = pageResultSet.getString("password");
                    String email = pageResultSet.getString("email");

                    User user = new User(firstName, lastName, password, email);
                    user.setId(id);
                    usersList.add(user);

                }

                int count = 0;
                if(countResultSet.next()){
                    count = countResultSet.getInt("count");
                }

                return new Page<>(usersList, count);

            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return null;
    }
}


