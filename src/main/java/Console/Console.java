package Console;

import Domain.User;
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

                    System.out.println("Enter user's first name: ");
                    String firstName = scanner.next();

                    System.out.println("Enter user's last name: ");
                    String lastName = scanner.next();

                    System.out.println("Enter user's password: ");
                    String password = scanner.next();

                    System.out.println("Enter user's email: ");
                    String email = scanner.next();

                    Long id = 0L;
                    for (User u : service.findAllUsers())
                        id = u.getId() + 1;

                    User user = new User(firstName, lastName, password, email);
                    user.setId(id);
                    service.addUser(user);
                    break;

                case 2: // Remove User

                    System.out.println("Enter user's First Name:");
                    String firstname = scanner.next();
                    Long id_utilizator = service.getUserIdByName(firstname);
                    Optional<User> utilizator = service.findUser(id_utilizator);

                    utilizator.ifPresent(service::removeUser);
                    break;

                case 3: // Print Users

                    Iterable<User> users = service.findAllUsers();
                    for (User u : users) {
                        System.out.println(u);
                    }
                    break;

                case 4: // Add Friend

                    System.out.println("Enter first user's First Name:");
                    String firstname1 = scanner.next();
                    Long id_user_1 = service.getUserIdByName(firstname1);
                    System.out.println("Enter second user's First Name:");
                    String firstname2 = scanner.next();
                    Long id_user_2 = service.getUserIdByName(firstname2);

                    service.addFriendship(id_user_1, id_user_2, id_user_1);

                    break;

                case 5: // Remove Friend

                    System.out.println("Enter first user's First Name:");
                    String firstname_1 = scanner.next();
                    Long id_user1 = service.getUserIdByName(firstname_1);
                    System.out.println("Enter second user's First Name:");
                    String firstname_2 = scanner.next();
                    Long id_user2 = service.getUserIdByName(firstname_2);

                    service.removeFriendship(id_user1, id_user2);

                    break;

                case 6: // Number of communities

                    int nr_communities = service.numberOfCommunities();
                    System.out.println("Number of communities: " + nr_communities);

                    break;

                case 7: // The biggest community

                    System.out.println("The biggest community is: ");
                    for (User u : service.biggestCommunity())
                        System.out.println(u);
                    break;

                case 8:

                    System.out.println("User's ID: ");
                    Long id_user = scanner.nextLong();
                    System.out.println("User: " + service.findUser(id_user));

                case 9: // Exit
                    break;
            }
            if (option == 9)
                break;
        }

    }

}



