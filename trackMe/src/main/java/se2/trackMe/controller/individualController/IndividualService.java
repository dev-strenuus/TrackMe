package se2.trackMe.controller.individualController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualNotification;
import se2.trackMe.model.IndividualRequest;
import se2.trackMe.model.ThirdPartyNotification;
import se2.trackMe.storageController.IndividualNotificationRepository;
import se2.trackMe.storageController.IndividualRepository;
import se2.trackMe.storageController.IndividualRequestRepository;
import se2.trackMe.storageController.ThirdPartyNotificationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    public List<Individual> getAllIndividuals(){
        List<Individual> people = new ArrayList<>();
        individualRepository.findAll().forEach(people::add);
        return people;
    }

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
