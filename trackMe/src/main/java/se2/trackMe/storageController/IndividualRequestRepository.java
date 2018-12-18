package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualRequest;
import se2.trackMe.model.ThirdParty;

import java.util.Optional;

public interface IndividualRequestRepository extends CrudRepository<IndividualRequest, String> {
    Optional<IndividualRequest> findByIndividualAndThirdParty(Individual individual, ThirdParty thirdParty);
}
