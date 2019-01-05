package se2.trackMe.model;


import com.fasterxml.jackson.annotation.JsonView;
import se2.trackMe.model.profileJSON.Profile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ThirdParty {

    @JsonView(Profile.IndividualPublicView.class)
    @Id
    private String vat;

    @Column
    private String name;

    @Column
    private String password;

    public ThirdParty() {
    }

    public ThirdParty(String vat) {
        this.vat = vat;
    }

    public ThirdParty(String vat, String name, String password) {
        this.vat = vat;
        this.name = name;
        this.password = password;
    }

    public String getVat() {
        return vat;
    }

    public String getName(){
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
