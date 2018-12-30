package se2.trackMe.controller.thirdPartyController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se2.trackMe.model.*;
import se2.trackMe.storageController.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.REQUIRED)
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

    @Autowired
    private IndividualDataRepository individualDataRepository;

    @Autowired
    private AnonymousRequestRepository anonymousRequestRepository;

    @Autowired
    private AnonymousAnswerRepository anonymousAnswerRepository;

    @Autowired
    private AnonymousRequestBuilder anonymousRequestBuilder;

    public List<Individual> getAllIndividuals() {
        List<Individual> people = new ArrayList<>();
        individualRepository.findAll().forEach(people::add);
        return people;
    }

    public void addThirdParty(ThirdParty thirdParty) {
        thirdPartyRepository.save(thirdParty);
    }


    public void addIndividualRequest(ThirdParty thirdParty, Individual individual, Boolean subscribedToNewData) throws DataAccessException {

        IndividualRequest individualRequest = new IndividualRequest(thirdParty, individual, subscribedToNewData);
        individualRequestRepository.save(individualRequest);
        individualNotificationRepository.save(new IndividualNotification(individualRequest));
    }

    public Optional<IndividualRequest> getIndividualRequest(ThirdParty thirdParty, Individual individual) {
        return individualRequestRepository.findByThirdPartyAndIndividual(thirdParty, individual);
    }

    public Optional<ThirdParty> getThirdParty(String id) {
        return thirdPartyRepository.findById(id);
    }

    public List<ThirdPartyNotification> getThirdPartyNotificationList(ThirdParty thirdParty) {
        return thirdPartyNotificationRepository.findAllByThirdParty(thirdParty);
    }

    public List<IndividualData> getIndividualData(ThirdParty thirdParty, Individual individual) {
        thirdPartyNotificationRepository.deleteAllByThirdPartyAndIndividual(thirdParty, individual);
        return individualDataRepository.findAllByIndividual(individual);
    }

    public List<IndividualData> getIndividualDataInATimeRange(Individual individual, Date start, Date end){
        return individualDataRepository.findAllByIndividualAndTimestampBetween(individual, start, end);
    }

    public List<IndividualData> getIndividualDataBeforeTimestamp(Individual individual, Date date){
        return individualDataRepository.findAllByIndividualAndTimestampIsBefore(individual, date);
    }

    public Optional<Individual> getIndividual(String id) {
        return individualRepository.findById(id);
    }

    public void addAnonymousRequest(ThirdParty thirdParty, AnonymousRequest anonymousRequest) {
        anonymousRequest.setThirdParty(thirdParty);
        anonymousRequestRepository.save(anonymousRequest);
        new Thread(()-> {anonymousRequestBuilder.calculate(anonymousRequest);}).start();

    }

    public List<IndividualData> getNewDataNotificationList(ThirdParty thirdParty, Individual individual){
        List<ThirdPartyNotification> thirdPartyNotificationList = thirdPartyNotificationRepository.findAllByThirdPartyAndIndividual(thirdParty, individual);
        List<IndividualData> individualDataList = new ArrayList<>();
        thirdPartyNotificationList.forEach(notification -> notification.getIndividualDataList().forEach(individualData -> individualDataList.add(individualData)));
        deleteNotifications(thirdPartyNotificationList);
        return individualDataList;
    }

    public List<IndividualRequest> getIndividualRequestNotificationList(ThirdParty thirdParty){
        List<ThirdPartyNotification> thirdPartyNotificationList  = thirdPartyNotificationRepository.findAllByThirdPartyAndIndividualRequestIsNotNull(thirdParty);
        List<IndividualRequest> individualRequestList = new ArrayList<>();
        thirdPartyNotificationList.forEach(thirdPartyNotification -> individualRequestList.add(thirdPartyNotification.getIndividualRequest()));
        deleteNotifications(thirdPartyNotificationList);
        return individualRequestList;
    }

    public Integer countIndividualRequestNotifications(ThirdParty thirdParty){
        return thirdPartyNotificationRepository.countAllByThirdPartyAndIndividualRequestIsNotNull(thirdParty);
    }

    public void deleteAllNotificationsByThirdPartyAndIndividualRequestIsNotNull(ThirdParty thirdParty){
        thirdPartyNotificationRepository.deleteAllByThirdPartyAndIndividualRequestIsNotNull(thirdParty);
    }
    public void deleteNotifications(List<ThirdPartyNotification> thirdPartyNotificationList){
        thirdPartyNotificationList.forEach(notification -> {thirdPartyNotificationRepository.deleteById(notification.getId());});
    }

    public List<IndividualRequest> getAllIndividualRequestsByThirdParty(ThirdParty thirdParty){
        deleteAllNotificationsByThirdPartyAndIndividualRequestIsNotNull(thirdParty);
        return individualRequestRepository.findAllByThirdParty(thirdParty);

    }

    public List<AnonymousRequest> getAllAnonymousRequests(ThirdParty thirdParty){
        return anonymousRequestRepository.findAllByThirdParty(thirdParty);
    }

    public List<AnonymousAnswer> getAllAnonymousAnswers(AnonymousRequest anonymousRequest){
        thirdPartyNotificationRepository.deleteAllByAnonymousAnswer_AnonymousRequest(anonymousRequest);
        return anonymousAnswerRepository.findAllByAnonymousRequest(anonymousRequest);
    }

    public Optional<AnonymousRequest> getAnonymousRequest(Long id){
        return anonymousRequestRepository.findById(id);
    }

    public List<AnonymousAnswer> getNewAnswersNotificationList(AnonymousRequest anonymousRequest){
        List<ThirdPartyNotification> thirdPartyNotificationList = thirdPartyNotificationRepository.findAllByAnonymousAnswer_AnonymousRequest(anonymousRequest);
        List<AnonymousAnswer> anonymousAnswerList = new ArrayList<>();
        thirdPartyNotificationList.forEach(notification -> anonymousAnswerList.add(notification.getAnonymousAnswer()));
        deleteNotifications(thirdPartyNotificationList);
        return anonymousAnswerList;
    }
}
