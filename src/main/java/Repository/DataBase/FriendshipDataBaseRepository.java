package Repository.DataBase;

import Domain.Validators.Validator;
import Domain.Tuple;
import Repository.Repository;
import Domain.Friendship;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipDataBaseRepository implements Repository<Tuple<Long, Long>, Friendship>{

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;
    Map<Tuple<Long, Long>, Friendship> friendships = new HashMap<>();


    public FriendshipDataBaseRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        loadData();
    }


    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        if (id == null)
            throw new IllegalArgumentException("id cannot be null");
        return Optional.ofNullable(friendships.get(id));
    }

    @Override
    public Iterable<Friendship> findAll() {

        Set<Friendship> friendsDataBase = new HashSet<>();

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Friendships");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){

                Long id_user_1 = resultSet.getLong("id_user_1");
                Long id_user_2 = resultSet.getLong("id_user_2");
                LocalDateTime dateTime = resultSet.getTimestamp("friendship_date").toLocalDateTime();
                String status = resultSet.getString("friendship_status");
                Long id_request = resultSet.getLong("id_request");

                Friendship friendship = new Friendship(id_user_1, id_user_2, id_request);
                friendship.setId(new Tuple<>(id_user_1, id_user_2));
                friendship.setStatus(status);
                friendship.setDate(dateTime);
                friendsDataBase.add(friendship);
            }

            return friendsDataBase;

        }catch (SQLException e){
            e.printStackTrace();
        }

        return friendsDataBase;

    }

    @Override
    public Optional<Friendship> save(Friendship friendship) {

        if (friendship == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        validator.validate(friendship);

        int rez = -1;

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Friendships (id_user_1, id_user_2, friendship_date, friendship_status, id_request) VALUES (?, ?, ?, ?, ?)")
        ){

            statement.setLong(1, friendship.getId_user_1());
            statement.setLong(2, friendship.getId_user_2());
            statement.setTimestamp(3, Timestamp.valueOf(friendship.getDate()));
            statement.setString(4, friendship.getStatus());
            statement.setLong(5, friendship.getId_request());
            rez = statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }

        if (rez > 0) {
            friendships.put(friendship.getId(), friendship);
            return Optional.empty();
        }
        else
            return Optional.of(friendship);

    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {

        Optional<Friendship> friendship = Optional.ofNullable(friendships.get(id));
        int rez = -1;

        if (friendship.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM Friendships WHERE id_user_1 = ? and id_user_2 = ?")) {

                statement.setLong(1, friendship.get().getId_user_1());
                statement.setLong(2, friendship.get().getId_user_2());
                rez = statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (rez > 0) {
                friendships.remove(id);
                return friendship;
            }
        }

        return Optional.empty();

    }

    @Override
    public Optional<Friendship> update(Friendship friendship) {

        if (friendship == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        validator.validate(friendship);

        Optional<Friendship> existingFriendship = Optional.ofNullable(friendships.get(friendship.getId()));
        int rez = -1;

        if (existingFriendship.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("UPDATE Friendships SET friendship_date = ?, friendship_status = ? WHERE id_user_1 = ? and id_user_2 = ?")) {

                statement.setTimestamp(1, Timestamp.valueOf(friendship.getDate()));
                statement.setString(2, friendship.getStatus());
                statement.setLong(3, friendship.getId_user_1());
                statement.setLong(4, friendship.getId_user_2());

                rez = statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (rez > 0) {
                friendships.put(friendship.getId(), friendship);
                return Optional.empty();
            }
        }

        return Optional.of(friendship);

    }

    private void loadData(){
        findAll().forEach(friendship -> {
            friendships.put(friendship.getId(), friendship);
        });
    }

}




