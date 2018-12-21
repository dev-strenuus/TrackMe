package se2.trackMe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Individual {

    @Id
    private String fiscalCode;

    @Column
    private String name;

    @Column
    private  String surname;

    @Column
    private String password;

    public Individual(){}

    public Individual(String fiscalCode, String name, String surname, String password) {
        this.fiscalCode = fiscalCode;
        this.name = name;
        this.surname = surname;
        this.password = password;
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
}
