package se2.trackMe.storageController;

import org.springframework.data.jpa.repository.JpaRepository;
import se2.trackMe.model.security.Authority;
import se2.trackMe.model.security.AuthorityName;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findByName(AuthorityName authorityName);
}
