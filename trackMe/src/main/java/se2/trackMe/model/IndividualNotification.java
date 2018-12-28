package se2.trackMe.model;

import com.fasterxml.jackson.annotation.JsonView;
import se2.trackMe.model.profileJSON.Profile;

import javax.persistence.*;

@Entity
public class IndividualNotification {

    @JsonView(Profile.IndividualPublicView.class)
    @Id
    private String id;

    @ManyToOne()
    private Individual individual;

    @JsonView(Profile.IndividualPublicView.class)
    @ManyToOne()
    private IndividualRequest individualRequest;

    public IndividualNotification(){}

    public IndividualNotification(IndividualRequest individualRequest){
        this.individual = individualRequest.getIndividual();
        this.individualRequest = individualRequest;
        this.id = (new Integer(individualRequest.hashCode())).toString();
    }

    public IndividualRequest getIndividualRequest() {
        return individualRequest;
    }

    public Individual getIndividual() {
        return individual;
    }

    public String getId() {
        return id;
    }
}
