package Console;

import Domain.Utilizator;
import Service.Service;


import java.util.Optional;
import java.util.Scanner;

public class Console {

    private final Service service;
    private final Scanner scanner = new Scanner(System.in);

    public Console(Service service) {
        this.service = service;
    }

    public void print_menu(){
        System.out.println("MENU");
        System.out.println("1. Add User");
        System.out.println("2. Remove User");
        System.out.println("3. Print Users");
        System.out.println("4. Add Friend");
        System.out.println("5. Remove Friend");
        System.out.println("6. Print number of communities");
        System.out.println("7. Print the biggest community");
        System.out.println("8. Find user by ID");
        System.out.println("9. Exit");
    }

    public void run() {

        while (true) {

            print_menu();
            int option = scanner.nextInt();
            switch (option) {

                case 1: // Add User

                    System.out.println("Enter user's first name:");
                    String firstName = scanner.next();

                    System.out.println("Enter user's last name:");
                    String lastName = scanner.next();

                    Long id = 0L;
                    for (Utilizator u : service.findAll_user())
                        id = u.getId() + 1;

                    Utilizator user = new Utilizator(firstName, lastName);
                    user.setId(id);
                    service.add_user(user);
                    break;

                case 2: // Remove User

                    System.out.println("Enter user's First Name:");
                    String firstname = scanner.next();
                    Long id_utilizator = service.get_user_id_by_name(firstname);
                    Optional<Utilizator> utilizator = service.find_user(id_utilizator);

                    utilizator.ifPresent(service::remove_user);
                    break;

                case 3: // Print Users

                    Iterable<Utilizator> users = service.findAll_user();
                    for (Utilizator u : users) {
                        System.out.println(u);
                    }
                    break;

                case 4: // Add Friend

                    System.out.println("Enter first user's First Name:");
                    String firstname1 = scanner.next();
                    Long id_user_1 = service.get_user_id_by_name(firstname1);
                    System.out.println("Enter second user's First Name:");
                    String firstname2 = scanner.next();
                    Long id_user_2 = service.get_user_id_by_name(firstname2);

                    service.add_friendship(id_user_1, id_user_2);

                    break;

                case 5: // Remove Friend

                    System.out.println("Enter first user's First Name:");
                    String firstname_1 = scanner.next();
                    Long id_user1 = service.get_user_id_by_name(firstname_1);
                    System.out.println("Enter second user's First Name:");
                    String firstname_2 = scanner.next();
                    Long id_user2 = service.get_user_id_by_name(firstname_2);

                    service.remove_friendship(id_user1, id_user2);

                    break;

                case 6: // Number of communities

                    int nr_communities = service.number_of_communities();
                    System.out.println("Number of communities: " + nr_communities);

                    break;

                case 7: // The biggest community

                    System.out.println("The biggest community is: ");
                    for (Utilizator u : service.biggest_community())
                        System.out.println(u);
                    break;

                case 8:

                    System.out.println("User's ID: ");
                    Long id_user = scanner.nextLong();
                    System.out.println("User: " + service.find_user(id_user));

                case 9: // Exit
                    break;
            }
            if (option == 9)
                break;
        }

    }

}



