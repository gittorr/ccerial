package org.gittorr.ccerial.variable.pojos;

import org.gittorr.ccerial.AccessorType;
import org.gittorr.ccerial.CcSerializable;

@CcSerializable(accessorType = AccessorType.SETTER, includeHeader = true)
public class Client {

    private String name;
    private String lastName;
    private int age;
    private double balance;

    public Client() {
    }

    public Client(String name, String lastName, int age, double balance) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
