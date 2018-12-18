package se2.trackMe.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class IndividualNotification {

    @Id
    String id;

    //TODO
    @Column
    Individual individual;

    //TODO
    @Column
    IndividualRequest individualRequest;

    public IndividualNotification(IndividualRequest individualRequest){
        this.individual = individualRequest.getIndividual();
        this.individualRequest = individualRequest;
        this.id = (new Integer(individualRequest.hashCode())).toString();
    }
}
