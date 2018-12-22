package se2.trackMe.model;

import com.fasterxml.jackson.annotation.JsonView;
import se2.trackMe.model.profileJSON.Profile;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class ThirdPartyNotification {

    @Id
    private String id;

    @ManyToOne
    private ThirdParty thirdParty;

    @JsonView(Profile.ThirdPartyPublicView.class)
    @ManyToOne
    private IndividualRequest individualRequest;

    @JsonView(Profile.ThirdPartyPublicView.class)
    @ManyToMany
    private List<IndividualData> individualDataList;

    public ThirdPartyNotification(){}

    public ThirdPartyNotification(List<IndividualData> individualDataList, ThirdParty thirdParty){
        this.individualDataList = individualDataList;
        this.thirdParty = thirdParty;
    }

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

    public List<IndividualData> getIndividualDataList() {
        return individualDataList;
    }
}
