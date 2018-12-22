package se2.trackMe.controller.individualController;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualNotification;
import se2.trackMe.model.IndividualRequest;
import se2.trackMe.model.ThirdPartyNotification;
import se2.trackMe.model.security.Authority;
import se2.trackMe.model.security.AuthorityName;
import se2.trackMe.model.security.User;
import se2.trackMe.storageController.*;

import java.util.*;

@Service
public class IndividualService {

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private IndividualNotificationRepository individualNotificationRepository;

    @Autowired
    private IndividualRequestRepository individualRequestRepository;

    @Autowired
    private ThirdPartyNotificationRepository thirdPartyNotificationRepository;


    public void addIndividual(Individual individual) throws DataAccessException {
        individualRepository.save(individual);
    }

    public Optional<Individual> getIndividual(String id){
        return individualRepository.findById(id);
    }

    public List<IndividualNotification> getIndvidualNotificationList(Individual individual){
        return individualNotificationRepository.findAllByIndividual(individual);
    }

    public Optional<IndividualRequest> getIndividualRequest(IndividualRequest individualRequest){
        return individualRequestRepository.findByThirdPartyAndIndividual(individualRequest.getThirdParty(), individualRequest.getIndividual());
    }

    public void setIndividualRequestAnswer(IndividualRequest individualRequest){
        individualRequestRepository.save(individualRequest);
        thirdPartyNotificationRepository.save(new ThirdPartyNotification(individualRequest));
    }


}
