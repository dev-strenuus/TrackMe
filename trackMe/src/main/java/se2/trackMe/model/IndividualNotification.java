package se2.trackMe.model;

import javax.persistence.*;

@Entity
public class IndividualNotification {

    @Id
    private String id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Individual individual;

    @ManyToOne(cascade = CascadeType.ALL)
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
