package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.AnonymousRequest;
import se2.trackMe.model.ThirdParty;

import java.util.List;

public interface AnonymousRequestRepository extends CrudRepository<AnonymousRequest, Long> {
    List<AnonymousRequest> findAllByThirdParty(ThirdParty thirdParty);
}
