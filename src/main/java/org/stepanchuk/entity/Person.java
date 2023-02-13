package org.stepanchuk.entity;

import lombok.Data;
import org.stepanchuk.orm.annotation.Column;
import org.stepanchuk.orm.annotation.Entity;
import org.stepanchuk.orm.annotation.Id;
import org.stepanchuk.orm.annotation.Table;
@Data
@Entity
@Table("persons")
public class Person {

    @Id("id")
    private long id;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String LastName;
}
