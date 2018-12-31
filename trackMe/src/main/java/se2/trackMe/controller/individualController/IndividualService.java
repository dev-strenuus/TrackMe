package se2.trackMe.controller.individualController;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se2.trackMe.model.*;
import se2.trackMe.storageController.*;


import java.time.Period;
import java.time.ZoneId;
import java.util.*;

@Transactional
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

    public List<IndividualRequest> getIndvidualPendingRequestList(Individual individual){
        return individualRequestRepository.findAllByIndividualAndAccepted(individual, null);
    }

    public List<IndividualRequest> getIndividualAcceptedRequestList(Individual individual){
        return individualRequestRepository.findAllByIndividualAndAccepted(individual, true);
    }

    public Optional<IndividualRequest> getIndividualRequest(IndividualRequest individualRequest){
        return individualRequestRepository.findByThirdPartyAndIndividual(individualRequest.getThirdParty(), individualRequest.getIndividual());
    }

    public void setIndividualRequestAnswer(IndividualRequest individualRequest){
        individualRequestRepository.save(individualRequest);
        thirdPartyNotificationRepository.save(new ThirdPartyNotification(individualRequest));
    }

    public void saveData(List<IndividualData> individualDataList){
        individualDataList.forEach(data -> {data.setAge(Period.between(data.getIndividual().getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),data.getTimestamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).getYears());
                                            data.setLatitude(data.getIndividual().getLatitude());
                                            data.setLongitude(data.getIndividual().getLongitude());
                                            });
        individualDataList.forEach(data -> individualDataRepository.save(data));
        List<IndividualRequest> individualRequestList = new ArrayList<>();
        individualRequestRepository.findAllByIndividual(individualDataList.get(0).getIndividual()).forEach(individualRequestList::add);
        individualRequestList.forEach(individualRequest -> {if(individualRequest.getSubscribedToNewData() != null && individualRequest.getSubscribedToNewData()) thirdPartyNotificationRepository.save(new ThirdPartyNotification(individualDataList, individualRequest.getThirdParty()));});
    }

    public Integer countNotifications(Individual individual){
        return individualNotificationRepository.countAllByIndividual(individual);
    }

    public void deleteNotifications(List<IndividualNotification> individualNotificationList){
        individualNotificationList.forEach(individualNotification -> individualNotificationRepository.deleteById(individualNotification.getId()));
    }

    public void deleteAllNotifications(Individual individual){
        individualNotificationRepository.deleteAllByIndividual(individual);
    }

    public List<IndividualRequest> getIndvidualPendingNotificationList(Individual individual){
        List<IndividualNotification> individualNotificationList = individualNotificationRepository.findAllByIndividualAndIndividualRequest_Accepted(individual, null);
        List<IndividualRequest> individualRequestList = new ArrayList<>();
        individualNotificationList.forEach(individualNotification -> individualRequestList.add(individualNotification.getIndividualRequest()));
        deleteNotifications(individualNotificationList);
        return individualRequestList;
    }

    public void updateIndividual(Individual individual){
        individualRepository.save(individual);
    }

}
