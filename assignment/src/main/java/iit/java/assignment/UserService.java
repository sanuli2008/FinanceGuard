package iit.java.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    public Long addUser(User user) {
        return userRepo.save(user).getId();
    }

    public boolean authenticateUser(String username, String password) {
        List<User> users = userRepo.findByUsername(username);
        if (users.size() == 1) {
            User user = users.get(0);
            return user.getPassword().equals(password); // Check if passwords match
        }
        // Return false if no user found or if there are multiple users with the same username
        return false;
    }
}
