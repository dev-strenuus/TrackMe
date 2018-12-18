package se2.trackMe.controller.individualController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualNotification;
import se2.trackMe.model.IndividualRequest;
import se2.trackMe.storageController.IndividualNotificationRepository;
import se2.trackMe.storageController.IndividualRepository;
import se2.trackMe.storageController.IndividualRequestRepository;

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

    public void addIndividual(Individual individual) throws DataAccessException {
        individualRepository.save(individual);
    }

    public Optional<Individual> getIndividual(String id){
        return individualRepository.findById(id);
    }

    public List<IndividualNotification> getIndvidualNotificationList(Individual individual){
        return individualNotificationRepository.findAllByIndividual(individual);
    }

    public Optional<IndividualRequest> setIndividualRequestAnswer(IndividualRequest individualRequest){
        //TODO forse è meglio mettere la risposta
        return individualRequestRepository.findByIndividualAndThirdParty(individualRequest.getIndividual(), individualRequest.getThirdParty());
    }


}
