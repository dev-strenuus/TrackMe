package se2.trackMe.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class IndividualRequest {

    @Id
    @ManyToOne
    private Individual individual;

    @Id
    @ManyToOne
    private ThirdParty thirdParty;

    @Column
    private Boolean accepted;

    public IndividualRequest(ThirdParty thirdParty, Individual individual){

        this.thirdParty = thirdParty;
        this.individual = individual;
        this.accepted = false;
    }


    public Individual getIndividual() {
        return individual;
    }

    public ThirdParty getThirdParty() {
        return thirdParty;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
