package se2.trackMe.controller.thirdPartyController;


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
import java.util.Optional;

@Controller
@PreAuthorize("hasRole('THIRDPARTY')")
@ResponseStatus(HttpStatus.OK)
public class ThirdPartyController {

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private UserService userService;

    public void checkUsername(String username, String token) {
        if (!userService.checkUsername(username, token.substring(7)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Trying to be another user");
    }

    @RequestMapping("/people")
    public @ResponseBody
    List<Individual> getPeople() {
        return thirdPartyService.getAllIndividuals();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/thirdParty/individualRequest")
    public void addIndividualRequest(@RequestBody IndividualRequest individualRequest, @RequestHeader("Authorization") String token) {
        ThirdParty thirdParty = thirdPartyService.getThirdParty(individualRequest.getThirdParty().getVat()).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "ThirdParty Not Found"));
        checkUsername(thirdParty.getVat(), token);
        Individual individual = thirdPartyService.getIndividual(individualRequest.getIndividual().getFiscalCode()).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Individual Not Found"));
        thirdPartyService.getIndividualRequest(thirdParty, individual).ifPresent(iR -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This request has been already done");
        });
        thirdPartyService.addIndividualRequest(thirdParty, individual, individualRequest.getSubscribedToNewData());
    }

    @JsonView(Profile.ThirdPartyPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/notifications")
    public @ResponseBody
    List<ThirdPartyNotification> getThirdPartyNotificationList(@PathVariable("thirdParty") String id, @RequestHeader("Authorization") String token) {
        checkUsername(id, token);
        ThirdParty thirdParty = thirdPartyService.getThirdParty(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty Not Found"));
        List<ThirdPartyNotification> thirdPartyNotificationList = thirdPartyService.getThirdPartyNotificationList(thirdParty);
        return thirdPartyNotificationList;
    }

    @JsonView(Profile.ThirdPartyPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/individualRequests")
    public @ResponseBody
    List<IndividualRequest> getThirdPartyIndividualRequest(@PathVariable("thirdParty") String id, @RequestHeader("Authorization") String token) {
        checkUsername(id, token);
        ThirdParty thirdParty = thirdPartyService.getThirdParty(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty Not Found"));
        List<IndividualRequest> individualRequestList = thirdPartyService.getAllIndividualRequestsByThirdParty(thirdParty);
        return individualRequestList;
    }

    @JsonView(Profile.ThirdPartyPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/notifications/individualRequests")
    public @ResponseBody
    List<IndividualRequest> getThirdPartyIndividualRequestNotifications(@PathVariable("thirdParty") String id, @RequestHeader("Authorization") String token) {
        checkUsername(id, token);
        ThirdParty thirdParty = thirdPartyService.getThirdParty(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty Not Found"));
        List<IndividualRequest> thirdPartyNotificationList = thirdPartyService.getIndividualRequestNotificationList(thirdParty);
        return thirdPartyNotificationList;
    }

    @RequestMapping("/thirdParty/{thirdParty}/notifications/countIndividualRequests")
    public @ResponseBody
    Integer getThirdPartyCountIndividualRequestNotifications(@PathVariable("thirdParty") String id, @RequestHeader("Authorization") String token) {
        checkUsername(id, token);
        ThirdParty thirdParty = thirdPartyService.getThirdParty(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty Not Found"));
        return thirdPartyService.countIndividualRequestNotifications(thirdParty);
    }

    @JsonView(Profile.ThirdPartyPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/notifications/{individual}")
    public @ResponseBody
    List<IndividualData> getThirdPartyNewDataNotificationList(@PathVariable("thirdParty") String tPId, @PathVariable("individual") String iId, @RequestHeader("Authorization") String token) {
        checkUsername(tPId, token);
        ThirdParty thirdParty = thirdPartyService.getThirdParty(tPId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty not found"));
        Individual individual = thirdPartyService.getIndividual(iId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual not found"));
        List<IndividualData> thirdPartyIndividualDataList = thirdPartyService.getNewDataNotificationList(thirdParty, individual);
        IndividualRequest individualRequest = thirdPartyService.getIndividualRequest(thirdParty, individual).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The thirdParty has not the right to receive data from the individual"));

        if(individualRequest.getAccepted() == false)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't access this data");

        return thirdPartyIndividualDataList;
    }
    
    @JsonView(Profile.ThirdPartyPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/{individual}/data")
    public @ResponseBody
    List<IndividualData> getData(@PathVariable("thirdParty") String tPId, @PathVariable("individual") String iId, @RequestHeader("Authorization") String token) {
        checkUsername(tPId, token);
        ThirdParty thirdParty = thirdPartyService.getThirdParty(tPId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty not found"));
        Individual individual = thirdPartyService.getIndividual(iId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual not found"));

        // check if the thirdParty has an active subscription to receive the data of the individual
        IndividualRequest individualRequest = thirdPartyService.getIndividualRequest(thirdParty, individual).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The thirdParty has not the right to receive data from the individual"));

        if(individualRequest.getAccepted() == false)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't access this data");

        return thirdPartyService.getIndividualData(thirdParty, individual);
    }

   /* @JsonView(Profile.ThirdPartyPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/{individual}/{startTime}/{endTime}/data")
    public @ResponseBody
    List<IndividualData> getIndividualDataByTimeRange(@PathVariable("thirdParty") String tPId, @PathVariable("individual") String iId, @PathVariable("startTime") Date start,  @PathVariable("endTime") Date end) {
        ThirdParty thirdParty = thirdPartyService.getThirdParty(tPId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty not found"));
        Individual individual = thirdPartyService.getIndividual(iId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty not found"));

        // check if the thirdParty has an active subscription to receive the data of the individual
        IndividualRequest individualRequest = thirdPartyService.getIndividualRequest(thirdParty, individual).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The thirdParty has not the right to receive data from the individual"));

        // check time range
        if(start.after(end)){
            Boolean subscribed = individualRequest.getSubscribedToNewData();
            Date subscriptionDate = individualRequest.getBeginningOfSubscription();

            // if the third party didn't ask for new data, limits the time range
            if( !subscribed && end.after(subscriptionDate)) {
                return thirdPartyService.getIndividualDataInATimeRange(individual, start, subscriptionDate);
            } else {
                return thirdPartyService.getIndividualDataInATimeRange(individual, start, end);
            }
        } else {
            throw(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad time range provided"));
        }
    }*/

    /*@JsonView(Profile.ThirdPartyPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/{individual}/{time}/data")
    public @ResponseBody
    List<IndividualData> getIndividualDataBeforeTimestamp(@PathVariable("thirdParty") String tPId, @PathVariable("individual") String iId,  @PathVariable("time") Date date) {
        ThirdParty thirdParty = thirdPartyService.getThirdParty(tPId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty not found"));
        Individual individual = thirdPartyService.getIndividual(iId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty not found"));

        // check if the thirdParty has an active subscription to receive the data of the individual
        IndividualRequest individualRequest = thirdPartyService.getIndividualRequest(thirdParty, individual).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The thirdParty has not the right to receive data from the individual"));

        return thirdPartyService.getIndividualDataBeforeTimestamp(individual, date);
    }*/

    @RequestMapping(method = RequestMethod.POST, value ="/thirdParty/anonymousRequest")
    public void addAnonymousRequest(@RequestBody AnonymousRequest anonymousRequest, @RequestHeader("Authorization") String token) {
        ThirdParty thirdParty = thirdPartyService.getThirdParty(anonymousRequest.getThirdParty().getVat()).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "ThirdParty Not Found"));
        checkUsername(thirdParty.getVat(), token);
        thirdPartyService.addAnonymousRequest(thirdParty, anonymousRequest);
    }

    @RequestMapping("/thirdParty/{thirdParty}/anonymousRequests")
    public @ResponseBody List<AnonymousRequest> getAllAnonymousRequets(@PathVariable("thirdParty") String id, @RequestHeader("Authorization") String token) {
        checkUsername(id, token);
        ThirdParty thirdParty = thirdPartyService.getThirdParty(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty Not Found"));
        return thirdPartyService.getAllAnonymousRequests(thirdParty);
    }

    @JsonView(Profile.AnonymousRequestPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/anonymousAnswer/{anonymousRequest}")
    public @ResponseBody
    List<AnonymousAnswer> getAnonymousData(@PathVariable("thirdParty") String tPId, @PathVariable("anonymousRequest") Long aRId, @RequestHeader("Authorization") String token) {
        checkUsername(tPId, token);
        ThirdParty thirdParty = thirdPartyService.getThirdParty(tPId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty not found"));
        AnonymousRequest anonymousRequest = thirdPartyService.getAnonymousRequest(aRId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anonymous Request not found"));
        if(!anonymousRequest.getThirdParty().getVat().equals(anonymousRequest.getThirdParty().getVat()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not your request");
        return thirdPartyService.getAllAnonymousAnswers(anonymousRequest);
    }

    @JsonView(Profile.AnonymousRequestPublicView.class)
    @RequestMapping("/thirdParty/{thirdParty}/anonymousAnswer/notifications/{anonymousRequest}")
    public @ResponseBody
    List<AnonymousAnswer> getAnonymousDataNotifications(@PathVariable("thirdParty") String tPId, @PathVariable("anonymousRequest") Long aRId, @RequestHeader("Authorization") String token) {
        checkUsername(tPId, token);
        ThirdParty thirdParty = thirdPartyService.getThirdParty(tPId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ThirdParty not found"));
        AnonymousRequest anonymousRequest = thirdPartyService.getAnonymousRequest(aRId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anonymous Request not found"));
        if(!anonymousRequest.getThirdParty().getVat().equals(anonymousRequest.getThirdParty().getVat()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not your request");
        return thirdPartyService.getNewAnswersNotificationList(anonymousRequest);
    }

    @JsonView(Profile.ThirdPartyPublicView.class)
    @RequestMapping(method = RequestMethod.PUT, value = "/thirdParty/{username}/changePassword")
    public void updatePassword(@PathVariable("username") String id, @RequestBody List<String> passwords, @RequestHeader("Authorization") String token) {
        checkUsername(id, token);
        ThirdParty thirdParty = thirdPartyService.getThirdParty(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Third party Not Found"));

        String oldPassword = passwords.get(0);
        String newPassword = passwords.get(1);

        if (oldPassword != null && newPassword != null) {
            try {
                userService.updatePassword(id, newPassword, oldPassword);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Bad Credentials");
            }
        }
        else{
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Data are not well formed");
        }
    }
}
