package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.ThirdParty;
import se2.trackMe.model.ThirdPartyNotification;

import java.util.List;

public interface ThirdPartyNotificationRepository extends CrudRepository<ThirdPartyNotification, String> {
    List<ThirdPartyNotification> findAllByThirdParty(ThirdParty thirdParty);
}
