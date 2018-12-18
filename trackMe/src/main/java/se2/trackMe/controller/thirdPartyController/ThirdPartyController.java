package se2.trackMe.controller.thirdPartyController;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import se2.trackMe.model.Individual;
import se2.trackMe.model.ThirdParty;

@Controller
public class ThirdPartyController {

    @Autowired
    private ThirdPartyService thirdPartyService;

    @RequestMapping(method = RequestMethod.POST, value = "/thirdParty/signin")
    public void addPlayer(@RequestBody ThirdParty thirdParty){
        thirdPartyService.addThirdParty(thirdParty);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/thirdParty/individualRequest")
    public void addPlayer(@RequestBody ThirdParty thirdParty, @RequestBody Individual individual){
        thirdPartyService.addIndividualRequest(thirdParty, individual);
    }

}
