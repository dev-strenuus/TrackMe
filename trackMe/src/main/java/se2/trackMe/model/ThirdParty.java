package se2.trackMe.model;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ThirdParty {

    @Id
    private String vat;

    @Column
    private String password;

    public ThirdParty(){}

    public ThirdParty(String vat) {
        this.vat = vat;
    }

    public String getVat() {
        return vat;
    }

    public String getPassword() {
        return password;
    }
}
