package se2.trackMe.model;

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

    public Individual(){}

    public Individual(String fiscalCode, String name, String surname) {
        this.fiscalCode = fiscalCode;
        this.name = name;
        this.surname = surname;
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
