package se2.trackMe.model;

import com.fasterxml.jackson.annotation.JsonView;
import se2.trackMe.model.profileJSON.Profile;

import javax.persistence.*;
import java.util.List;

@Entity
public class ThirdPartyNotification {

    @JsonView(Profile.ThirdPartyPublicView.class)
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ThirdParty thirdParty;

    @JsonView(Profile.ThirdPartyPublicView.class)
    @ManyToOne
    private IndividualRequest individualRequest;

    @JsonView(Profile.ThirdPartyPublicView.class)
    @ManyToMany
    private List<IndividualData> individualDataList;

    @JsonView(Profile.AnonymousRequestPublicView.class)
    @ManyToOne
    private AnonymousAnswer anonymousAnswer;

    @ManyToOne
    private Individual individual;

    public ThirdPartyNotification(){}

    public ThirdPartyNotification(AnonymousAnswer anonymousAnswer, ThirdParty thirdParty){
        this.anonymousAnswer = anonymousAnswer;
        this.thirdParty = thirdParty;
    }

    public ThirdPartyNotification(List<IndividualData> individualDataList, ThirdParty thirdParty){
        this.individualDataList = individualDataList;
        this.thirdParty = thirdParty;
        this.individual = individualDataList.get(0).getIndividual();
    }

    public ThirdPartyNotification(IndividualRequest individualRequest){
        this.individualRequest = individualRequest;
        this.thirdParty = individualRequest.getThirdParty();
    }

    public ThirdParty getThirdParty() {
        return thirdParty;
    }

    public IndividualRequest getIndividualRequest() {
        return individualRequest;
    }

    public Long getId() {
        return id;
    }

    public List<IndividualData> getIndividualDataList() {
        return individualDataList;
    }

    public AnonymousAnswer getAnonymousAnswer() {
        return anonymousAnswer;
    }

    public Individual getIndividual() {
        return individual;
    }
}
