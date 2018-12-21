package se2.trackMe.controller.individualController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualNotification;
import se2.trackMe.model.IndividualRequest;
import se2.trackMe.model.ThirdParty;

import java.util.List;

@Controller
@PreAuthorize("hasRole('INDIVIDUAL')")
@ResponseStatus(HttpStatus.OK)
public class IndividualController {

    @Autowired
    private IndividualService individualService;

    @RequestMapping("/people")
    public @ResponseBody List<Individual> getPeople(){
        return individualService.getAllIndividuals();
    }

    @RequestMapping("/individual/{individual}/notifications")
    public @ResponseBody List<IndividualNotification> getIndividualNotificationList(@PathVariable("individual") String id){
        Individual individual = individualService.getIndividual(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual Not Found"));
        List<IndividualNotification> individualNotificationList = individualService.getIndvidualNotificationList(individual);
        return individualNotificationList;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/individual/individualRequest/answer")
    public void answerToIndividualRequest(@RequestBody IndividualRequest individualRequest){
        Individual individual = individualService.getIndividual(individualRequest.getIndividual().getFiscalCode()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual Not Found"));
        IndividualRequest individualRequest1 = individualService.getIndividualRequest(individualRequest).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual Request Not Found"));
        individualRequest1.setAccepted(individualRequest.getAccepted());
        individualService.setIndividualRequestAnswer(individualRequest1);
    }

}
