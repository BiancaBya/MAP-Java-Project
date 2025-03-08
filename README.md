# 🌍💻 Social Network Project
This is a Java-based social networking application designed to connect users through an intuitive and modern graphical interface. Built with JavaFX and powered by PostgreSQL, this project demonstrates a full-stack approach to creating a dynamic, user-friendly platform where individuals can interact, build connections, and communicate in real time.

## 📌 Key Features
- User Authentication:
Secure account creation (sign-up) and login processes ensure that every user’s profile is protected and personalized.

- Friend Request System:
Users can send, receive, and manage friend requests, facilitating a growing network of connections.

- Real-Time Messaging:
An integrated messaging feature allows users to communicate directly with their friends, making interaction quick and convenient.

- Modern Graphical Interface:
Developed using JavaFX, the interface is both responsive and visually appealing, providing a smooth user experience.

## ⚙️ Technologies Used

- Java:
Core programming language used to build the application logic and overall structure.

- JavaFX:
Framework used to design and implement a dynamic and user-friendly graphical interface.

- PostgreSQL:
A powerful open-source relational database system that securely manages all user data and interactions.

- Build Tools:
Gradle is used to manage dependencies and build the project efficiently.

## 🌐 Architecture 

- **Domain (Model):**
  - Encapsulates the core business logic and entities (e.g., User, Message, FriendRequest).
  - Represents the real-world data and behavior crucial for the social network.

- **Repository Layer:**
  - Abstracts data persistence and interacts directly with the PostgreSQL database.
  - Ensures that data access is decoupled from business logic, supporting maintainability and scalability.

- **Service Layer:**
  - Acts as the intermediary, orchestrating complex operations and integrating various domain functionalities.
  - Handles business rules, input validation, and transactional processes.

 **Presentation Layer (GUI & Controller):**
  - **Controller :**
    - Serves as the bridge between the view and the model.
    - Manages user inputs, communicates with the service layer, and updates the domain accordingly.
      
  - **GUI (View):**
    - Built using JavaFX, it offers a responsive and user-friendly interface.
    - Delivers a smooth and engaging user experience by cleanly separating visual elements from business logic.

## 📷 Photos      

![signup](https://github.com/user-attachments/assets/acb63467-18f9-40e7-962f-e56be5a7d314)


![login](https://github.com/user-attachments/assets/077770fe-c945-4dee-b673-a3d0d6c062a5)


![main](https://github.com/user-attachments/assets/e4be39ea-64cd-4457-90e0-49b7e17cee87)


![request](https://github.com/user-attachments/assets/79f5dcbb-9b2b-4b8d-b7a6-bf463315317a)


![chat](https://github.com/user-attachments/assets/5dc9b367-c95b-4046-8944-2705f258994a)




