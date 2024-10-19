
import Domain.Utilizator;
import Domain.Validators.UtilizatorValidator;
import Domain.Validators.ValidationException;
import Domain.Validators.Validator;
import Repository.Repository;
import Repository.File.UtilizatorRepository;
import Repository.Memory.InMemoryRepository;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        Repository<Long, Utilizator> repo = new InMemoryRepository<Long, Utilizator>(new UtilizatorValidator());
        Repository<Long, Utilizator> repoFile = new UtilizatorRepository(new UtilizatorValidator(), "./data/utilizatori.txt");

        for(Utilizator u: repoFile.findAll())
            System.out.println(u);

        Utilizator u1 = new Utilizator("IONUT2", "a");
        Utilizator u2 = new Utilizator("Mihai2", "b");
        Utilizator u3 = null;
        u1.setId(4L);
        u2.setId(5L);
        try {
            repoFile.save(u1);
            repoFile.save(u2);
            repoFile.save(u3);
        }catch(IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
        }catch(ValidationException e)
        {
            System.out.println(e.getMessage());
        }
        System.out.println();

    }
}

