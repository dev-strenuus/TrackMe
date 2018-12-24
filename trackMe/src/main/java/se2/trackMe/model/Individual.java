package se2.trackMe.model;

import com.fasterxml.jackson.annotation.JsonView;
import se2.trackMe.model.profileJSON.Profile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Individual {

    @JsonView(Profile.ThirdPartyPublicView.class)
    @Id
    private String fiscalCode;

    @Column
    @NotNull
    private String name;

    @Column
    @NotNull
    private  String surname;

    @Column
    @NotNull
    private String password;

    @Column
    @NotNull
    private Date birthDate;

    @Column
    @NotNull
    private Float latitude;

    @Column
    @NotNull
    private Float longitude;

    public Individual(){}

    public Individual(@NotNull String fiscalCode, @NotNull String name, @NotNull String surname, @NotNull String password, @NotNull Date birthDate, @NotNull Float latitude, @NotNull Float longitude) {
        this.fiscalCode = fiscalCode;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.birthDate = birthDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }
}
