package se2.trackMe.storageController;

import org.springframework.data.jpa.repository.JpaRepository;
import se2.trackMe.model.security.User;

/**
 * Created by stephan on 20.03.16.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
