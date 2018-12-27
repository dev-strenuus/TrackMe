package se2.trackMe.controller.individualController;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se2.trackMe.controller.authenticationController.UserService;
import se2.trackMe.model.*;
import se2.trackMe.model.profileJSON.Profile;

import java.util.Date;
import java.util.List;

@Controller
@PreAuthorize("hasRole('INDIVIDUAL')")
@ResponseStatus(HttpStatus.OK)
public class IndividualController {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private UserService userService;

    public void checkUsername(String username, String token){
        if(!userService.checkUsername(username, token.substring(7)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Trying to be another user");
    }

    @JsonView(Profile.IndividualPublicView.class)
    @RequestMapping("/individual/{individual}/notifications")
    public @ResponseBody List<IndividualNotification> getIndividualNotificationList(@PathVariable("individual") String id){
        //if(!userService.checkUsername())
        Individual individual = individualService.getIndividual(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual Not Found"));
        List<IndividualNotification> individualNotificationList = individualService.getIndvidualPendingNotificationList(individual, null);
        return individualNotificationList;
    }

    @JsonView(Profile.IndividualPublicView.class)
    @RequestMapping("/individual/{individual}/acceptedRequests")
    public @ResponseBody List<IndividualNotification> getIndividualAcceptedNotificationList(@PathVariable("individual") String id){
        Individual individual = individualService.getIndividual(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual Not Found"));
        List<IndividualNotification> individualAcceptedNotificationList = individualService.getIndividualAcceptedNotificationList(individual, true);
        return individualAcceptedNotificationList;
    }

    //better with third party as parameter and individual in the url
    @RequestMapping(method = RequestMethod.POST, value = "/individual/individualRequest/answer")
    public void answerToIndividualRequest(@RequestBody IndividualRequest individualRequest){
        Individual individual = individualService.getIndividual(individualRequest.getIndividual().getFiscalCode()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual Not Found"));
        IndividualRequest individualRequest1 = individualService.getIndividualRequest(individualRequest).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual Request Not Found"));
        individualRequest1.setAccepted(individualRequest.getAccepted());
        if(individualRequest.getAccepted()){
            individualRequest1.setBeginningOfSubscription(new Date());
        } else {
            individualRequest1.setEndOfSubscription(new Date());
        }
        individualService.setIndividualRequestAnswer(individualRequest1);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/individual/{individual}/data")
    public void addData(@PathVariable("individual") String id, @RequestBody List<IndividualData> individualDataList){
        Individual individual = individualService.getIndividual(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual Not Found"));
        individualDataList.forEach(data -> data.setIndividual(individual));
        individualService.saveData(individualDataList);
    }

}
