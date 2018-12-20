package se2.trackMe.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class IndividualRequest{

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Individual individual;

    @ManyToOne
    private ThirdParty thirdParty;

    @Column
    private Boolean accepted;

    public IndividualRequest(){}

    public IndividualRequest(ThirdParty thirdParty, Individual individual){

        this.thirdParty = thirdParty;
        this.individual = individual;
        this.accepted = null;
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
