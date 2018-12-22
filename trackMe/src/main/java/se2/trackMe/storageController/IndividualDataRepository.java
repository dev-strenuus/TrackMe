package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.IndividualData;

public interface IndividualDataRepository extends CrudRepository<IndividualData, Long> {
}
