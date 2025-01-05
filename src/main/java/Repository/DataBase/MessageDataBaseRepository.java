package Repository.DataBase;

import Domain.Message;
import Domain.User;
import Repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDataBaseRepository implements Repository<Long, Message> {

    private final String url;
    private final String username;
    private final String password;
    private final Repository<Long, User> userRepository;

    public MessageDataBaseRepository(Repository<Long, User> userRepository, String url, String username, String password) {
        this.userRepository = userRepository;
        this.url = url;
        this.username = username;
        this.password = password;
    }


    public Optional<Message> findOneNoReply(Long id) {

        String query = "SELECT * FROM Messages WHERE id_message = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {

                Long id_to = resultSet.getLong("id_to");
                Long id_from = resultSet.getLong("id_from");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Message messageDB = new Message(userRepository.findOne(id_from).get(),
                        Collections.singletonList(userRepository.findOne(id_to).get()),
                        message,
                        date);

                messageDB.setId(id);
                return Optional.of(messageDB);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.empty();
    }


    @Override
    public Optional<Message> findOne(Long id) {

        String query = "SELECT * FROM Messages WHERE id_message = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {

                Long id_to = resultSet.getLong("id_to");
                Long id_from = resultSet.getLong("id_from");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long reply_id = resultSet.getLong("reply_id");

                if (resultSet.wasNull()) {
                    reply_id = null;
                }

                Message messageDB = new Message(userRepository.findOne(id_from).get(),
                        Collections.singletonList(userRepository.findOne(id_to).get()),
                        message,
                        date);

                messageDB.setId(id);

                if (reply_id != null) {
                    findOneNoReply(reply_id).ifPresent(messageDB::setReply);
                }

                return Optional.of(messageDB);

            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.empty();

    }


    @Override
    public Iterable<Message> findAll() {

        List<Message> messages = new ArrayList<>();

        String query = "SELECT * FROM Messages";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                Long id_message = resultSet.getLong("id_message");
                Long id_to = resultSet.getLong("id_to");
                Long id_from = resultSet.getLong("id_from");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long reply_id = resultSet.getLong("reply_id");

                if (resultSet.wasNull()) {
                    reply_id = null;
                }

                User from = userRepository.findOne(id_from).get();
                List<User> to = Collections.singletonList(userRepository.findOne(id_to).get());

                Message messageDB = new Message(from, to, message, date);
                messageDB.setId(id_message);

                if(reply_id != null) {
                    Message reply = findOne(reply_id).orElse(null);
                    messageDB.setReply(reply);
                }

                messages.add(messageDB);

            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return messages;
    }


    @Override
    public Optional<Message> save(Message entity) {

        String query = "INSERT INTO Messages(id_to, id_from, message, date, reply_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setLong(1, entity.getTo().get(0).getId());
            statement.setLong(2, entity.getFrom().getId());
            statement.setString(3, entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));

            if(entity.getReply() == null){
                statement.setNull(5, Types.NULL);
            } else{
                statement.setLong(5, entity.getReply().getId());
            }

            statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.of(entity);

    }


    @Override
    public Optional<Message> delete(Long id) {

        String query = "DELETE FROM Messages WHERE id_message = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setLong(1, id);
            statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.empty();
    }


    @Override
    public Optional<Message> update(Message entity) {

        String query = "UPDATE Messages SET id_to = ?, id_from = ?, message = ?, date = ?, reply_id = ? WHERE id_message = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setLong(1, entity.getTo().get(0).getId());
            statement.setLong(2, entity.getFrom().getId());
            statement.setString(3, entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));

            if(entity.getReply() == null){
                statement.setNull(5, Types.NULL);
            }else{
                statement.setLong(5, entity.getReply().getId());
            }

            statement.setLong(6, entity.getId());

            statement.executeUpdate();
            return Optional.empty();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.of(entity);
    }

}


