package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.ThirdParty;

public interface ThirdPartyRepository extends CrudRepository<ThirdParty, String> {

}
