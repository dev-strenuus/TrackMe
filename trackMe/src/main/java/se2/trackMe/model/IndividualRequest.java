package se2.trackMe.model;

import com.fasterxml.jackson.annotation.JsonView;
import se2.trackMe.model.profileJSON.Profile;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class IndividualRequest{

    @Id
    @GeneratedValue
    private Long id;

    @JsonView(Profile.ThirdPartyPublicView.class)
    @ManyToOne
    private Individual individual;

    @JsonView(Profile.IndividualPublicView.class)
    @ManyToOne
    private ThirdParty thirdParty;

    @JsonView(Profile.ThirdPartyPublicView.class)
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
