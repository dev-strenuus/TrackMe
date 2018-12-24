package se2.trackMe.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
public class AnonymousAnswer {

    @Id
    @GeneratedValue
    private Long id;


    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date start;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date end;

    @ManyToMany
    private List<IndividualData> individualDataList;

    @ManyToOne
    @NotNull
    private AnonymousRequest anonymousRequest;

    public AnonymousAnswer(@NotNull Date start, @NotNull Date end, List<IndividualData> individualDataList, @NotNull AnonymousRequest anonymousRequest) {
        this.start = start;
        this.end = end;
        this.individualDataList = individualDataList;
        this.anonymousRequest = anonymousRequest;
    }

    public Long getId() {
        return id;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public List<IndividualData> getIndividualDataList() {
        return individualDataList;
    }

    public AnonymousRequest getAnonymousRequest() {
        return anonymousRequest;
    }
}
