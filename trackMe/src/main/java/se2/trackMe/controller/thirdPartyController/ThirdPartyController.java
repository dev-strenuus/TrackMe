package se2.trackMe.controller.thirdPartyController;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualRequest;
import se2.trackMe.model.ThirdParty;
import se2.trackMe.model.ThirdPartyNotification;

import java.util.List;

@Controller
@ResponseStatus(HttpStatus.OK)
public class ThirdPartyController {

    @Autowired
    private ThirdPartyService thirdPartyService;

    @RequestMapping(method = RequestMethod.POST, value = "/thirdParty/signin")
    public void addPlayer(@RequestBody ThirdParty thirdParty){
        thirdPartyService.getThirdParty(thirdParty.getVat()).ifPresent(t -> {throw new ResponseStatusException(HttpStatus.CONFLICT, "This third party already exists");});
        thirdPartyService.addThirdParty(thirdParty);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/thirdParty/individualRequest")
    public void addPlayer(@RequestBody IndividualRequest individualRequest){
        ThirdParty thirdParty = thirdPartyService.getThirdParty(individualRequest.getThirdParty().getVat()).orElse(null);//.orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "ThirdParty Not Found"));
        Individual individual = thirdPartyService.getIndividual(individualRequest.getIndividual().getFiscalCode()).orElse(null);//.orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Individual Not Found"));
        if(individual == null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Individual Not Found");
        thirdPartyService.getIndividualRequest(thirdParty, individual).ifPresent(iR -> {throw new ResponseStatusException(HttpStatus.CONFLICT, "This request has been already done");});
        thirdPartyService.addIndividualRequest(thirdParty, individual);
    }

    @RequestMapping("/thirdParty/{thirdParty}/notifications")
    public @ResponseBody List<ThirdPartyNotification> getThirdPartyNotificationList(@PathVariable("thirdParty") String id){
        ThirdParty thirdParty = thirdPartyService.getThirdParty(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty Not Found"));
        List<ThirdPartyNotification> thirdPartyNotificationList = thirdPartyService.getThirdPartyNotificationList(thirdParty);
        return thirdPartyNotificationList;
    }

}
