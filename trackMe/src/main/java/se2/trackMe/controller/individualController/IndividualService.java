package se2.trackMe.controller.individualController;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se2.trackMe.model.*;
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

    @Autowired
    private IndividualDataRepository individualDataRepository;


    public void addIndividual(Individual individual) throws DataAccessException {
        individualRepository.save(individual);
    }

    public Optional<Individual> getIndividual(String id){
        return individualRepository.findById(id);
    }

    public List<IndividualNotification> getIndvidualPendingNotificationList(Individual individual, Boolean value){
        return individualNotificationRepository.findAllByIndividualAndIndividualRequest_Accepted(individual, value);
    }

    public List<IndividualNotification> getIndividualAcceptedNotificationList(Individual individual, Boolean value){
        return individualNotificationRepository.findAllByIndividualAndIndividualRequest_Accepted(individual, value);
    }

    public Optional<IndividualRequest> getIndividualRequest(IndividualRequest individualRequest){
        return individualRequestRepository.findByThirdPartyAndIndividual(individualRequest.getThirdParty(), individualRequest.getIndividual());
    }

    public void setIndividualRequestAnswer(IndividualRequest individualRequest){
        individualRequestRepository.save(individualRequest);
        thirdPartyNotificationRepository.save(new ThirdPartyNotification(individualRequest));
    }

    public void saveData(List<IndividualData> individualDataList){
        individualDataList.forEach(data -> individualDataRepository.save(data));
        List<IndividualRequest> individualRequestList = new ArrayList<>();
        individualRequestRepository.findAllByIndividual(individualDataList.get(0).getIndividual()).forEach(individualRequestList::add);
        individualRequestList.forEach(individualRequest -> {if(individualRequest.getSubscribedToNewData()) thirdPartyNotificationRepository.save(new ThirdPartyNotification(individualDataList, individualRequest.getThirdParty()));});
    }


}
