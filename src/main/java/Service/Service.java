package Service;

import Domain.Friendship;
import Domain.Message;
import Domain.Tuple;
import Domain.Utilizator;
import Domain.Validators.ValidationException;

import Repository.Repository;
import Repository.FriendshipPagingRepository;

import Utils.Observer.Observer;
import Utils.Observer.Observable;
import Utils.Events.EntityChangeEvent;
import Utils.Events.ChangeEventType;
import Utils.Paging.Page;
import Utils.Paging.Pageable;

import java.util.*;
import java.util.stream.Collectors;


public class Service implements Observable<EntityChangeEvent>{

    private final Repository<Long, Utilizator> repositoryUsers;
    private final FriendshipPagingRepository<Tuple<Long, Long>, Friendship> repositoryFriendships;
    private final Repository<Long, Message> repositoryMessages;

    private final List<Observer<EntityChangeEvent>> observers = new ArrayList<>();

    public Service(Repository<Long, Utilizator> repositoryUsers, FriendshipPagingRepository<Tuple<Long, Long>, Friendship> repositoryFriendships, Repository<Long, Message> repositoryMessages) {
        this.repositoryUsers = repositoryUsers;
        this.repositoryFriendships = repositoryFriendships;
        this.repositoryMessages = repositoryMessages;
    }

    public Optional<Utilizator> addUser(Utilizator user) {

        Iterable<Utilizator> utilizatori = repositoryUsers.findAll();
        for (Utilizator u : utilizatori) {
            if(u.getFirstName().equals(user.getFirstName()) && u.getLastName().equals(user.getLastName())) {
                return Optional.of(u);
            }
        }

        Optional<Utilizator> savedUser = repositoryUsers.save(user);
        if (savedUser.isEmpty()) {
            for (Utilizator u : utilizatori) {
                if(u.getFirstName().equals(user.getFirstName()) && u.getLastName().equals(user.getLastName())) {

                    EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.ADD_USER, u);
                    notifyObservers(event);
                    return Optional.of(u);

                }
            }
        }

        return Optional.empty();

    }

    public Optional<Utilizator> removeUser(Utilizator user) {

        Optional<Utilizator> userDB = repositoryUsers.findOne(user.getId());

        if (userDB.isPresent()) {

            List<Tuple<Long, Long>> lista_ind = new ArrayList<>();
            List<Utilizator> friends = getUsersFriends(user);

            friends.forEach(friend -> lista_ind.add(new Tuple<>(friend.getId(), user.getId())));
            lista_ind.forEach(tuple -> removeFriendship(tuple.getLeft(), tuple.getRight()));

            EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.DELETE_USER, userDB.get());
            notifyObservers(event);

            return repositoryUsers.delete(user.getId());
        }

        return Optional.empty();
    }

    public Optional<Utilizator> updateUser(Utilizator user) {

        Optional<Utilizator> oldUser = repositoryUsers.findOne(user.getId());
        if (oldUser.isPresent()) {

            Optional<Utilizator> newUser = repositoryUsers.update(user);
            if (newUser.isEmpty()) {
                EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.MODIFY_USER, user, oldUser.get());
                notifyObservers(event);
                return newUser;
            }

        }

        return oldUser;
    }

    public Optional<Utilizator> findUser(Long id) {
        return repositoryUsers.findOne(id);
    }

    public Optional<Friendship> findFriendship(Tuple<Long, Long> id) {
        return repositoryFriendships.findOne(id);
    }

    public Iterable<Utilizator> findAllUsers() {

        Iterable<Utilizator> users =  repositoryUsers.findAll();

        repositoryFriendships.findAll().forEach(f -> {
            Long id1 = f.getId_user_1();
            Long id2 = f.getId_user_2();
            for (Utilizator u : users){

                if (u.getId().equals(id1)){
                    Optional<Utilizator> friend = repositoryUsers.findOne(id2);
                    friend.ifPresent(u::addFriend);
                }

                else if (u.getId().equals(id2)){
                    Optional<Utilizator> friend = repositoryUsers.findOne(id1);
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

        Optional<Utilizator> u1 = repositoryUsers.findOne(id1);
        Optional<Utilizator> u2 = repositoryUsers.findOne(id2);

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

        Optional<Utilizator> u1 = repositoryUsers.findOne(id1);
        Optional<Utilizator> u2 = repositoryUsers.findOne(id2);

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


    public void DFS(Utilizator user, boolean[] visited){

        visited[Integer.parseInt(user.getId().toString())] = true;

        List<Utilizator> friends = getUsersFriends(user);
        friends.forEach(u -> {
            if (!visited[Integer.parseInt(u.getId().toString())]) {
                DFS(u, visited);
            }
        });

    }

    public int numberOfCommunities(){

        Iterable<Utilizator> users = findAllUsers();
        int nr_users = 0;
        for (Utilizator u : users)
            nr_users = Integer.parseInt(u.getId().toString());

        int communities = 0;

        boolean[] visited = new boolean[nr_users + 1];
        for (Utilizator u : users)
            if (!visited[Integer.parseInt(u.getId().toString())]){
                communities++;
                DFS(u, visited);
            }

        return communities;
    }

    public void DFSNr(Utilizator user, boolean[] visited, List<Utilizator> users){

        visited[Integer.parseInt(user.getId().toString())] = true;

        List<Utilizator> friends = getUsersFriends(user);
        friends.forEach(u -> {
            if (!visited[Integer.parseInt(u.getId().toString())]) {
                users.add(u);
                DFSNr(u, visited, users);
            }
        });

        if (!visited[Integer.parseInt(user.getId().toString())])
            users.add(user);

    }

    public List<Utilizator> biggestCommunity(){

        List<Utilizator> users = new ArrayList<>();

        Iterable<Utilizator> users_iterator = findAllUsers();
        int nr_users = 0;
        for (Utilizator u : users_iterator)
            nr_users = Integer.parseInt(u.getId().toString());

        boolean[] visited = new boolean[nr_users + 1];


        for (Utilizator u : users_iterator)
            if (!visited[Integer.parseInt(u.getId().toString())]){
                List <Utilizator> communitiy = new ArrayList<>();
                communitiy.add(u);
                DFSNr(u, visited, communitiy);

                if (communitiy.size() > users.size()){
                    users = communitiy;
                }
            }

        return users;
    }

    public List<Utilizator> getUsersFriends(Utilizator user){

        List<Utilizator> friends = new ArrayList<>();

        for (Friendship f : repositoryFriendships.findAll()) {
            if(f.getId_user_1().equals(user.getId())){
                friends.add(repositoryUsers.findOne(f.getId_user_2()).get());
                Utilizator utilizator = repositoryUsers.findOne(f.getId_user_1()).get();
                repositoryUsers.findOne(f.getId_user_2()).get().addFriend(utilizator);
            }
            else if(f.getId_user_2().equals(user.getId())){
                friends.add(repositoryUsers.findOne(f.getId_user_1()).get());
                Utilizator utilizator = repositoryUsers.findOne(f.getId_user_2()).get();
                repositoryUsers.findOne(f.getId_user_1()).get().addFriend(utilizator);
            }
        }

        return friends;

    }

    public Long getUserIdByName(String firstName){

        for (Utilizator u : findAllUsers()){
            if (u.getFirstName().equals(firstName))
                return u.getId();
        }
        return -1L;
    }

    public Long getUserIdByEmail(String email){
        for (Utilizator u : findAllUsers()){
            if(u.getEmail().equals(email))
                return u.getId();
        }
        return -1L;
    }


    public List<Message> getMessagesBetween(Utilizator user, Utilizator friend){

        Collection<Message> messages = (Collection<Message>) repositoryMessages.findAll();

        return messages.stream()
                .filter( m -> (m.getFrom().equals(user) && m.getTo().contains(repositoryUsers.findOne(friend.getId()).get()))
                        || (m.getFrom().equals(friend) && m.getTo().contains(repositoryUsers.findOne(user.getId()).get())))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toCollection(ArrayList::new));

    }


    public boolean addMessage(Utilizator from, Utilizator to, String msg){

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


    public boolean addReply(Utilizator from, Utilizator to, String msg, Message reply){

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


    public Page<Friendship> findUsersFriends(Pageable pageable, Utilizator user){

        return repositoryFriendships.getUsersFriends(pageable, user);

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




