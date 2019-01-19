package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.*;

import java.util.List;

public interface ThirdPartyNotificationRepository extends CrudRepository<ThirdPartyNotification, Long> {
    List<ThirdPartyNotification> findAllByThirdParty(ThirdParty thirdParty);
    List<ThirdPartyNotification> findAllByThirdPartyAndIndividual(ThirdParty thirdParty, Individual individual);
    List<ThirdPartyNotification> findAllByThirdPartyAndIndividualRequestIsNotNull(ThirdParty thirdParty);
    Integer countAllByThirdPartyAndIndividualRequestIsNotNull(ThirdParty thirdParty);
    void deleteAllByThirdPartyAndIndividualRequestIsNotNull(ThirdParty thirdParty);
    void deleteAllByThirdPartyAndIndividual(ThirdParty thirdParty, Individual individual);
    void deleteAllByAnonymousAnswer_AnonymousRequest(AnonymousRequest anonymousRequest);
    List<ThirdPartyNotification> findAllByAnonymousAnswer_AnonymousRequest(AnonymousRequest anonymousRequest);
}
