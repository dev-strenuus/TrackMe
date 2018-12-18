package se2.trackMe.controller.thirdPartyController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualNotification;
import se2.trackMe.model.IndividualRequest;
import se2.trackMe.model.ThirdParty;
import se2.trackMe.storageController.IndividualNotificationRepository;
import se2.trackMe.storageController.IndividualRequestRepository;
import se2.trackMe.storageController.ThirdPartyRepository;

@Service
public class ThirdPartyService {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private IndividualRequestRepository individualRequestRepository;

    @Autowired
    private IndividualNotificationRepository individualNotificationRepository;

    public void addThirdParty(ThirdParty thirdParty){
        thirdPartyRepository.save(thirdParty);
    }

    public void addIndividualRequest(ThirdParty thirdParty, Individual individual) throws DataAccessException {

        IndividualRequest individualRequest = new IndividualRequest(thirdParty, individual);
        individualRequestRepository.save(individualRequest);
        individualNotificationRepository.save(new IndividualNotification(individualRequest));
    }
}
