package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualNotification;

import javax.management.Notification;
import java.util.List;

public interface IndividualNotificationRepository extends CrudRepository<IndividualNotification, String> {

    Integer countAllByIndividual(Individual individual);
    void deleteAllByIndividual(Individual individual);
    List<IndividualNotification> findAllByIndividualAndIndividualRequest_Accepted(Individual individual, Boolean value);
}
