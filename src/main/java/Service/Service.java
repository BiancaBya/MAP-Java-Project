package Service;

import Domain.Friendship;
import Domain.Message;
import Domain.Tuple;
import Domain.User;
import Domain.Validators.ValidationException;

import Repository.Repository;
import Repository.FriendshipPagingRepository;

import Utils.Observer.Observer;
import Utils.Observer.Observable;
import Utils.Events.EntityChangeEvent;
import Utils.Events.ChangeEventType;
import Utils.Paging.Page;
import Utils.Paging.Pageable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;


public class Service implements Observable<EntityChangeEvent>{

    private final Repository<Long, User> repositoryUsers;
    private final FriendshipPagingRepository<Tuple<Long, Long>, Friendship> repositoryFriendships;
    private final Repository<Long, Message> repositoryMessages;

    private final List<Observer<EntityChangeEvent>> observers = new ArrayList<>();

    public Service(Repository<Long, User> repositoryUsers, FriendshipPagingRepository<Tuple<Long, Long>, Friendship> repositoryFriendships, Repository<Long, Message> repositoryMessages) {
        this.repositoryUsers = repositoryUsers;
        this.repositoryFriendships = repositoryFriendships;
        this.repositoryMessages = repositoryMessages;
    }

    public Optional<User> addUser(User user) {

        Iterable<User> users = repositoryUsers.findAll();
        for (User u : users) {
            if(u.getFirstName().equals(user.getFirstName()) && u.getLastName().equals(user.getLastName())) {
                return Optional.of(u);
            }
        }

        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        Optional<User> savedUser = repositoryUsers.save(user);

        if (savedUser.isEmpty()) {
            for (User u : users) {
                if(u.getFirstName().equals(user.getFirstName()) && u.getLastName().equals(user.getLastName())) {

                    EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.ADD_USER, u);
                    notifyObservers(event);
                    return Optional.of(u);

                }
            }
        }

        return Optional.empty();

    }

    public Optional<User> removeUser(User user) {

        Optional<User> userDB = repositoryUsers.findOne(user.getId());

        if (userDB.isPresent()) {

            List<Tuple<Long, Long>> lista_ind = new ArrayList<>();
            List<User> friends = getUsersFriends(user);

            friends.forEach(friend -> lista_ind.add(new Tuple<>(friend.getId(), user.getId())));
            lista_ind.forEach(tuple -> removeFriendship(tuple.getLeft(), tuple.getRight()));

            EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.DELETE_USER, userDB.get());
            notifyObservers(event);

            return repositoryUsers.delete(user.getId());
        }

        return Optional.empty();
    }

    public Optional<User> updateUser(User user) {

        Optional<User> oldUser = repositoryUsers.findOne(user.getId());
        if (oldUser.isPresent()) {

            Optional<User> newUser = repositoryUsers.update(user);
            if (newUser.isEmpty()) {
                EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.MODIFY_USER, user, oldUser.get());
                notifyObservers(event);
                return newUser;
            }

        }

        return oldUser;
    }

    public Optional<User> findUser(Long id) {
        return repositoryUsers.findOne(id);
    }

    public Optional<Friendship> findFriendship(Tuple<Long, Long> id) {
        return repositoryFriendships.findOne(id);
    }

    public Iterable<User> findAllUsers() {

        Iterable<User> users =  repositoryUsers.findAll();

        repositoryFriendships.findAll().forEach(f -> {
            Long id1 = f.getId_user_1();
            Long id2 = f.getId_user_2();
            for (User u : users){

                if (u.getId().equals(id1)){
                    Optional<User> friend = repositoryUsers.findOne(id2);
                    friend.ifPresent(u::addFriend);
                }

                else if (u.getId().equals(id2)){
                    Optional<User> friend = repositoryUsers.findOne(id1);
                    friend.ifPresent(u::addFriend);
                }
            }
        });

        return users;
    }

    public Iterable<Friendship> findAllFriendships() {

        return repositoryFriendships.findAll();

    }


    public void addFriendship(Long id1, Long id2, Long id_request) {

        Optional<User> u1 = repositoryUsers.findOne(id1);
        Optional<User> u2 = repositoryUsers.findOne(id2);

        u1.ifPresent(user1 -> u2.ifPresent(user2 -> {
            user1.addFriend(user2);
            user2.addFriend(user1);

            Friendship f = new Friendship(id1, id2, id_request);
            f.setId(new Tuple<>(id1, id2));
            repositoryFriendships.save(f);

            EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.ADD_FRIENDSHIP, f);
            notifyObservers(event);

        }));

    }

    public void removeFriendship(Long id1, Long id2) {

        Optional<User> u1 = repositoryUsers.findOne(id1);
        Optional<User> u2 = repositoryUsers.findOne(id2);

        u1.ifPresent(user1 -> u2.ifPresent(user2 -> {
            user2.removeFriend(user1);
            user1.removeFriend(user2);

            Optional<Friendship> removed_friendship = repositoryFriendships.delete(new Tuple<>(id1,id2));
            if(removed_friendship.isEmpty())
                repositoryFriendships.delete(new Tuple<>(id2,id1));

            EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.DELETE_FRIENDSHIP, removed_friendship.get());
            notifyObservers(event);

        }));

    }

    public void updateFriendship(Friendship friendship){

        Optional<Friendship> oldFriendship = repositoryFriendships.findOne(friendship.getId());
        if(oldFriendship.isPresent()){

            Optional<Friendship> newFriendship = repositoryFriendships.update(friendship);
            if(newFriendship.isEmpty()){
                EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.MODIFY_FRIENDSHIP, friendship, oldFriendship.get());
                notifyObservers(event);
            }

        }

    }


    public void DFS(User user, boolean[] visited){

        visited[Integer.parseInt(user.getId().toString())] = true;

        List<User> friends = getUsersFriends(user);
        friends.forEach(u -> {
            if (!visited[Integer.parseInt(u.getId().toString())]) {
                DFS(u, visited);
            }
        });

    }

    public int numberOfCommunities(){

        Iterable<User> users = findAllUsers();
        int nr_users = 0;
        for (User u : users)
            nr_users = Integer.parseInt(u.getId().toString());

        int communities = 0;

        boolean[] visited = new boolean[nr_users + 1];
        for (User u : users)
            if (!visited[Integer.parseInt(u.getId().toString())]){
                communities++;
                DFS(u, visited);
            }

        return communities;
    }

    public void DFSNr(User user, boolean[] visited, List<User> users){

        visited[Integer.parseInt(user.getId().toString())] = true;

        List<User> friends = getUsersFriends(user);
        friends.forEach(u -> {
            if (!visited[Integer.parseInt(u.getId().toString())]) {
                users.add(u);
                DFSNr(u, visited, users);
            }
        });

        if (!visited[Integer.parseInt(user.getId().toString())])
            users.add(user);

    }

    public List<User> biggestCommunity(){

        List<User> users = new ArrayList<>();

        Iterable<User> users_iterator = findAllUsers();
        int nr_users = 0;
        for (User u : users_iterator)
            nr_users = Integer.parseInt(u.getId().toString());

        boolean[] visited = new boolean[nr_users + 1];


        for (User u : users_iterator)
            if (!visited[Integer.parseInt(u.getId().toString())]){
                List <User> communitiy = new ArrayList<>();
                communitiy.add(u);
                DFSNr(u, visited, communitiy);

                if (communitiy.size() > users.size()){
                    users = communitiy;
                }
            }

        return users;
    }

    public List<User> getUsersFriends(User user){

        List<User> friends = new ArrayList<>();

        for (Friendship f : repositoryFriendships.findAll()) {
            if(f.getId_user_1().equals(user.getId())){
                friends.add(repositoryUsers.findOne(f.getId_user_2()).get());
                User friend = repositoryUsers.findOne(f.getId_user_1()).get();
                repositoryUsers.findOne(f.getId_user_2()).get().addFriend(friend);
            }
            else if(f.getId_user_2().equals(user.getId())){
                friends.add(repositoryUsers.findOne(f.getId_user_1()).get());
                User friend = repositoryUsers.findOne(f.getId_user_2()).get();
                repositoryUsers.findOne(f.getId_user_1()).get().addFriend(friend);
            }
        }

        return friends;

    }

    public Long getUserIdByName(String firstName){

        for (User u : findAllUsers()){
            if (u.getFirstName().equals(firstName))
                return u.getId();
        }
        return -1L;
    }

    public Long getUserIdByEmail(String email){
        for (User u : findAllUsers()){
            if(u.getEmail().equals(email))
                return u.getId();
        }
        return -1L;
    }


    public List<Message> getMessagesBetween(User user, User friend){

        Collection<Message> messages = (Collection<Message>) repositoryMessages.findAll();

        return messages.stream()
                .filter( m -> (m.getFrom().equals(user) && m.getTo().contains(repositoryUsers.findOne(friend.getId()).get()))
                        || (m.getFrom().equals(friend) && m.getTo().contains(repositoryUsers.findOne(user.getId()).get())))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toCollection(ArrayList::new));

    }


    public boolean addMessage(User from, User to, String msg){

        try{

            Message message = new Message(from, Collections.singletonList(to), msg);
            repositoryMessages.save(message);

            return true;

        }catch (ValidationException ve){
            System.out.println("User error");
        } catch (Exception ex){
            System.out.println("Message error");
        }

        return false;

    }


    public boolean addReply(User from, User to, String msg, Message reply){

        try{

            Message message = new Message(from, Collections.singletonList(to), msg, reply);
            repositoryMessages.save(message);

            return true;

        }catch (ValidationException ve){
            System.out.println("User error");
        } catch (Exception ex){
            System.out.println("Message error");
        }

        return false;

    }


    public Page<Friendship> getAllFriendships(Pageable pageable){
        return repositoryFriendships.findAllOnPage(pageable);
    }


    public Page<Friendship> findUsersFriends(Pageable pageable, User user){

        return repositoryFriendships.getUsersFriends(pageable, user);

    }

    public String hashPassword(String password){

        try{

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException ex){
            throw new RuntimeException("Hashing error", ex);
        }
    }

    public User login(String email, String password){

        String hashedPassword = hashPassword(password);
        for (User user : findAllUsers()) {
            if (user.getEmail().equals(email) && hashedPassword.equals(user.getPassword())){
                return user;
            }
        }
        return null;
    }


    @Override
    public void addObserver(Observer<EntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(EntityChangeEvent t) {
        observers.stream().forEach(x -> x.update(t));
    }

}




