package ru.job4j.auth.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private int taxNumber;
    private Timestamp hireDate;
    @OneToMany
    private List<Person> persons;

    public static Employee of(
            int id, String firstName, String lastName, int taxNumber, Timestamp hireDate, List<Person> persons) {
        var employee = new Employee();
        employee.id = id;
        employee.firstName = firstName;
        employee.lastName = lastName;
        employee.taxNumber = taxNumber;
        employee.persons = persons;
        return employee;
    }
}
