package Repository.File;

import Domain.Validators.Validator;
import Domain.Friendship;
import Domain.Tuple;


public class FriendRepository extends AbstractFileRepository <Tuple<Long, Long>, Friendship> {

    public FriendRepository(Validator<Friendship> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public Friendship createEntity(String line) {

        Long id1 = Long.parseLong(line.split(" ")[0]);
        Long id2 = Long.parseLong(line.split(" ")[1]);


        Friendship friendship = new Friendship(id1, id2, id1);
        friendship.setId(new Tuple<>(id1,id2));

        return friendship;
    }

    @Override
    public String saveEntity(Friendship entity) {

        Long user1_id = entity.getId_user_1();
        Long user2_id = entity.getId_user_2();

        return user1_id.toString() + " " + user2_id.toString();
    }

}


