package se2.trackMe.controller.thirdPartyController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import se2.trackMe.model.*;
import se2.trackMe.storageController.*;

import java.util.List;
import java.util.Optional;

@Service
public class ThirdPartyService {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private IndividualRequestRepository individualRequestRepository;

    @Autowired
    private IndividualNotificationRepository individualNotificationRepository;

    @Autowired
    private ThirdPartyNotificationRepository thirdPartyNotificationRepository;

    @Autowired
    private IndividualRepository individualRepository;

    public void addThirdParty(ThirdParty thirdParty){
        thirdPartyRepository.save(thirdParty);
    }


    public void addIndividualRequest(ThirdParty thirdParty, Individual individual) throws DataAccessException {

        IndividualRequest individualRequest = new IndividualRequest(thirdParty, individual);
        individualRequestRepository.save(individualRequest);
        individualNotificationRepository.save(new IndividualNotification(individualRequest));
    }

    public Optional<IndividualRequest> getIndividualRequest(ThirdParty thirdParty, Individual individual){
        return individualRequestRepository.findByThirdPartyAndIndividual(thirdParty, individual);
    }

    public Optional<ThirdParty> getThirdParty(String id){
        return thirdPartyRepository.findById(id);
    }

    public List<ThirdPartyNotification> getThirdPartyNotificationList(ThirdParty thirdParty){
        return thirdPartyNotificationRepository.findAllByThirdParty(thirdParty);
    }

    public Optional<Individual> getIndividual(String id){
        return individualRepository.findById(id);
    }
}
