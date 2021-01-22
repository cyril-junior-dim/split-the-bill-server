package com.splitthebill.server.model.user;

import com.splitthebill.server.model.Group;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class PersonGroup {

    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal personGroupBalance;

    @CreationTimestamp
    private Date joined;

    @ManyToOne
    private Person person;

    @ManyToOne
    private Group group;

    public PersonGroup(Person person, Group group) {
        this.person = person;
        this.group = group;
        personGroupBalance = BigDecimal.ZERO;
    }

    public void addToBalance(BigDecimal amount){
        personGroupBalance = personGroupBalance.add(amount);
    }

    public void subtractFromBalance(BigDecimal amount){
        personGroupBalance = personGroupBalance.subtract(amount);
    }

}
