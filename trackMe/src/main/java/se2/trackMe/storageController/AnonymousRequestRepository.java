package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.AnonymousRequest;

public interface AnonymousRequestRepository extends CrudRepository<AnonymousRequest, Long> {
}
