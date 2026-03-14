package controller;

import model.entity.User;
import view.IView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// SRP: Only handles user registration and authentication
public class UserController {

    private IView view;
    private List<User> users = new ArrayList<>();
    private User loggedInUser = null;

    public UserController(IView view) {
        // DIP: Depends on IView abstraction
        this.view = view;
        seedSampleUser();
    }

    private void seedSampleUser() {
        users.add(new User("U001", "Arjun Sharma", "arjun@example.com", "pass123", "9876543210"));
    }

    public void register() {
        String name = view.getInput("Enter your name");
        String email = view.getInput("Enter email");
        String password = view.getInput("Enter password");
        String phone = view.getInput("Enter phone number");

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                view.showError("Email already registered.");
                return;
            }
        }

        String id = "U" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        User newUser = new User(id, name, email, password, phone);
        users.add(newUser);
        view.showMessage("Registration successful! Welcome, " + name);
    }

    public boolean login() {
        String email = view.getInput("Email");
        String password = view.getInput("Password");

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                loggedInUser = u;
                view.showMessage("Login successful! Welcome back, " + u.getName());
                return true;
            }
        }
        view.showError("Invalid credentials.");
        return false;
    }

    public void logout() {
        loggedInUser = null;
        view.showMessage("Logged out successfully.");
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
}
