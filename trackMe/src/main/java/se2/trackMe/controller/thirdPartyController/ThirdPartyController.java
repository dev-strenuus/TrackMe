package se2.trackMe.controller.thirdPartyController;


import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se2.trackMe.model.*;
import se2.trackMe.model.profileJSON.Profile;

import java.util.List;

@Controller
@PreAuthorize("hasRole('THIRDPARTY')")
@ResponseStatus(HttpStatus.OK)
public class ThirdPartyController {

    @Autowired
    private ThirdPartyService thirdPartyService;

    @RequestMapping("/people")
    public @ResponseBody
    List<Individual> getPeople() {
        return thirdPartyService.getAllIndividuals();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/thirdParty/individualRequest")
    public void addIndividualRequest(@RequestBody IndividualRequest individualRequest) {
        ThirdParty thirdParty = thirdPartyService.getThirdParty(individualRequest.getThirdParty().getVat()).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "ThirdParty Not Found"));
        Individual individual = thirdPartyService.getIndividual(individualRequest.getIndividual().getFiscalCode()).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Individual Not Found"));
        thirdPartyService.getIndividualRequest(thirdParty, individual).ifPresent(iR -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This request has been already done");
        });
        thirdPartyService.addIndividualRequest(thirdParty, individual, individualRequest.getSubscribedToNewData());
    }

    @JsonView(Profile.ThirdPartyPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/notifications")
    public @ResponseBody
    List<ThirdPartyNotification> getThirdPartyNotificationList(@PathVariable("thirdParty") String id) {
        ThirdParty thirdParty = thirdPartyService.getThirdParty(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty Not Found"));
        List<ThirdPartyNotification> thirdPartyNotificationList = thirdPartyService.getThirdPartyNotificationList(thirdParty);
        return thirdPartyNotificationList;
    }

    @JsonView(Profile.ThirdPartyPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/{individual}/data")
    public @ResponseBody
    List<IndividualData> getIndividualData(@PathVariable("thirdParty") String tPId, @PathVariable("individual") String iId) {
        ThirdParty thirdParty = thirdPartyService.getThirdParty(tPId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty not found"));
        Individual individual = thirdPartyService.getIndividual(iId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty not found"));

        // check if the thirdParty has an active subscription to receive the data of the individual
        IndividualRequest individualRequest = thirdPartyService.getIndividualRequest(thirdParty, individual).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The thirdParty has not the right to receive data from the individual"));

        List<IndividualData> listOfData = thirdPartyService.getIndividualData(individual);
        return listOfData;
    }

    @JsonView(Profile.AnonymousRequestPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/anonymousRequest")
    public void addAnonymousRequest(@RequestBody AnonymousRequest anonymousRequest) {
        ThirdParty thirdParty = thirdPartyService.getThirdParty(anonymousRequest.getThirdParty().getVat()).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "ThirdParty Not Found"));
    }
}
