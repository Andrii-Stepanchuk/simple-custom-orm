package org.stepanchuk;

import org.stepanchuk.entity.Person;
import org.stepanchuk.orm.Orm;

public class App {
    public static void main(String[] args) {
        Orm customOrm = new Orm();
        Person person = customOrm.find(Person.class, 1L);
        System.out.println(person);
    }
}
