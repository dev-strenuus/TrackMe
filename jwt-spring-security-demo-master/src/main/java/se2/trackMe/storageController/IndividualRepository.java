package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.Individual;

public interface IndividualRepository extends CrudRepository<Individual, String> {
}
