package se2.trackMe.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ThirdPartyNotification {

    @Id
    private String id;

    @ManyToOne
    private ThirdParty thirdParty;

    @ManyToOne
    private IndividualRequest individualRequest;

    public ThirdPartyNotification(){}

    public ThirdPartyNotification(IndividualRequest individualRequest){
        this.individualRequest = individualRequest;
        this.thirdParty = individualRequest.getThirdParty();
        this.id = (new Integer(individualRequest.hashCode())).toString();
    }

    public ThirdParty getThirdParty() {
        return thirdParty;
    }

    public IndividualRequest getIndividualRequest() {
        return individualRequest;
    }

    public String getId() {
        return id;
    }
}
