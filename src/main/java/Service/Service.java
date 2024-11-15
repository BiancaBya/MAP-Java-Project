package Service;

import Domain.Friendship;
import Domain.Tuple;
import Domain.Utilizator;
import Repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Service {

    private final Repository<Long, Utilizator> repository_users;
    private final Repository<Tuple<Long, Long>, Friendship> repository_friendships;

    public Service(Repository<Long, Utilizator> repository, Repository<Tuple<Long, Long>, Friendship> repositoryFriendships) {
        this.repository_users = repository;
        this.repository_friendships = repositoryFriendships;
    }

    public Optional<Utilizator> add_user(Utilizator user) {
        return repository_users.save(user);
    }

    public Optional<Utilizator> remove_user(Utilizator user) {

        List<Tuple<Long, Long>> lista_ind = new ArrayList<>();
        List<Utilizator> friends = get_users_friends(user);

        friends.forEach(friend -> lista_ind.add(new Tuple<>(friend.getId(), user.getId())));
        lista_ind.forEach(tuple -> remove_friendship(tuple.getLeft(), tuple.getRight()));

        return repository_users.delete(user.getId());
    }

    public Optional<Utilizator> update_user(Utilizator user) {
        return repository_users.update(user);
    }

    public Optional<Utilizator> find_user(Long id) {
        return repository_users.findOne(id);
    }

    public Optional<Friendship> find_friendship(Tuple<Long, Long> id) {
        return repository_friendships.findOne(id);
    }

    public Iterable<Utilizator> findAll_user() {

        Iterable<Utilizator> users =  repository_users.findAll();

        repository_friendships.findAll().forEach(f -> {
            Long id1 = f.getId_user_1();
            Long id2 = f.getId_user_2();
            for (Utilizator u : users){

                if (u.getId().equals(id1)){
                    Optional<Utilizator> friend = repository_users.findOne(id2);
                    friend.ifPresent(u::addFriend);
                }

                else if (u.getId().equals(id2)){
                    Optional<Utilizator> friend = repository_users.findOne(id1);
                    friend.ifPresent(u::addFriend);
                }
            }
        });

        return users;
    }

    public Iterable<Friendship> findAll_friendships() {

        return repository_friendships.findAll();

    }


    public void add_friendship(Long id1, Long id2, Long id_request) {

        Optional<Utilizator> u1 = repository_users.findOne(id1);
        Optional<Utilizator> u2 = repository_users.findOne(id2);

        u1.ifPresent(user1 -> u2.ifPresent(user2 -> {
            user1.addFriend(user2);
            user2.addFriend(user1);

            Friendship f = new Friendship(id1, id2, id_request);
            f.setId(new Tuple<>(id1, id2));
            repository_friendships.save(f);
        }));

    }

    public void remove_friendship(Long id1, Long id2) {

        Optional<Utilizator> u1 = repository_users.findOne(id1);
        Optional<Utilizator> u2 = repository_users.findOne(id2);

        u1.ifPresent(user1 -> u2.ifPresent(user2 -> {
            user2.removeFriend(user1);
            user1.removeFriend(user2);

            Optional<Friendship> removed_friendship = repository_friendships.delete(new Tuple<>(id1,id2));
            if(removed_friendship.isEmpty())
                repository_friendships.delete(new Tuple<>(id2,id1));
        }));

    }

    public void update_friendship(Friendship friendship){

        if(repository_friendships.findOne(friendship.getId()).isPresent())
            repository_friendships.update(friendship);
    }


    public void DFS(Utilizator user, boolean[] visited){

        visited[Integer.parseInt(user.getId().toString())] = true;

        List<Utilizator> friends = get_users_friends(user);
        friends.forEach(u -> {
            if (!visited[Integer.parseInt(u.getId().toString())]) {
                DFS(u, visited);
            }
        });

    }

    public int number_of_communities(){

        Iterable<Utilizator> users = findAll_user();
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

    public void DFS_nr(Utilizator user, boolean[] visited, List<Utilizator> users){

        visited[Integer.parseInt(user.getId().toString())] = true;

        List<Utilizator> friends = get_users_friends(user);
        friends.forEach(u -> {
            if (!visited[Integer.parseInt(u.getId().toString())]) {
                users.add(u);
                DFS_nr(u, visited, users);
            }
        });

        if (!visited[Integer.parseInt(user.getId().toString())])
            users.add(user);

    }

    public List<Utilizator> biggest_community(){

        List<Utilizator> users = new ArrayList<>();

        Iterable<Utilizator> users_iterator = findAll_user();
        int nr_users = 0;
        for (Utilizator u : users_iterator)
            nr_users = Integer.parseInt(u.getId().toString());

        boolean[] visited = new boolean[nr_users + 1];


        for (Utilizator u : users_iterator)
            if (!visited[Integer.parseInt(u.getId().toString())]){
                List <Utilizator> communitiy = new ArrayList<>();
                communitiy.add(u);
                DFS_nr(u, visited, communitiy);

                if (communitiy.size() > users.size()){
                    users = communitiy;
                }
            }

        return users;
    }

    public List<Utilizator> get_users_friends(Utilizator user){

        List<Utilizator> friends = new ArrayList<>();

        for (Friendship f : repository_friendships.findAll()) {
            if(f.getId_user_1().equals(user.getId())){
                friends.add(repository_users.findOne(f.getId_user_2()).get());
                Utilizator utilizator = repository_users.findOne(f.getId_user_1()).get();
                repository_users.findOne(f.getId_user_2()).get().addFriend(utilizator);
            }
            else if(f.getId_user_2().equals(user.getId())){
                friends.add(repository_users.findOne(f.getId_user_1()).get());
                Utilizator utilizator = repository_users.findOne(f.getId_user_2()).get();
                repository_users.findOne(f.getId_user_1()).get().addFriend(utilizator);
            }
        }

        return friends;

    }

    public Long get_user_id_by_name(String firstName){

        for (Utilizator u : findAll_user()){
            if (u.getFirstName().equals(firstName))
                return u.getId();
        }
        return -1L;
    }

}




