package se2.trackMe.controller.individualController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualNotification;
import se2.trackMe.model.IndividualRequest;

import java.util.List;

@Controller
public class IndividualController {

    @Autowired
    private IndividualService individualService;

    @RequestMapping(method = RequestMethod.POST, value = "/individual/signin")
    public void addIndividual(@RequestBody Individual individual){
        try {
            individualService.addIndividual(individual);
        }catch (DataAccessException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This individual already exists");
        }
    }

    @RequestMapping("/individual/{individual}/notifications")
    public List<IndividualNotification> getIndividualNotificationList(@PathVariable("individual") String id){
        Individual individual = individualService.getIndividual(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual Not Found"));
        List<IndividualNotification> individualNotificationList = individualService.getIndvidualNotificationList(individual);
        return individualNotificationList;
    }

    @RequestMapping("/individual/individualRequest/answer") //POST
    public void answerToIndividualRequest(@RequestBody IndividualRequest individualRequest, @RequestBody Boolean answer){
        Individual individual = individualService.getIndividual(individualRequest.getIndividual().getFiscalCode()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual Not Found"));
       // individualRequest = individualService.getIndividualRequest(individualRequest).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual Request Not Found"));

    }

}
