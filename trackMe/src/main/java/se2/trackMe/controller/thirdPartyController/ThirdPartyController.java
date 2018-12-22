package se2.trackMe.controller.thirdPartyController;


import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualRequest;
import se2.trackMe.model.ThirdParty;
import se2.trackMe.model.ThirdPartyNotification;
import se2.trackMe.model.profileJSON.Profile;

import java.util.List;

@Controller
@PreAuthorize("hasRole('THIRDPARTY')")
@ResponseStatus(HttpStatus.OK)
public class ThirdPartyController {

    @Autowired
    private ThirdPartyService thirdPartyService;

    @RequestMapping("/people")
    public @ResponseBody List<Individual> getPeople(){
        return thirdPartyService.getAllIndividuals();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/thirdParty/individualRequest")
    public void addIndividualRequest(@RequestBody IndividualRequest individualRequest){
        ThirdParty thirdParty = thirdPartyService.getThirdParty(individualRequest.getThirdParty().getVat()).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "ThirdParty Not Found"));
        Individual individual = thirdPartyService.getIndividual(individualRequest.getIndividual().getFiscalCode()).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Individual Not Found"));
        thirdPartyService.getIndividualRequest(thirdParty, individual).ifPresent(iR -> {throw new ResponseStatusException(HttpStatus.CONFLICT, "This request has been already done");});
        thirdPartyService.addIndividualRequest(thirdParty, individual, individualRequest.getSubscribedToNewData());
    }

    @JsonView(Profile.ThirdPartyPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/notifications")
    public @ResponseBody List<ThirdPartyNotification> getThirdPartyNotificationList(@PathVariable("thirdParty") String id){
        ThirdParty thirdParty = thirdPartyService.getThirdParty(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty Not Found"));
        List<ThirdPartyNotification> thirdPartyNotificationList = thirdPartyService.getThirdPartyNotificationList(thirdParty);
        return thirdPartyNotificationList;
    }

}
