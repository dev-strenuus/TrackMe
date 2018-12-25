package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualData;

import java.util.List;

public interface IndividualDataRepository extends CrudRepository<IndividualData, Long> {
    List<IndividualData> findAllByIndividual(Individual individual);
}
