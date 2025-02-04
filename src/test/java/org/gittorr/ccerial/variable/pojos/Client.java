package org.gittorr.ccerial.variable.pojos;

import org.gittorr.ccerial.AccessorType;
import org.gittorr.ccerial.CcEnum;
import org.gittorr.ccerial.CcSerializable;
import org.gittorr.ccerial.EnumType;
import org.gittorr.ccerial.fixed.arrays.HouseType;

@CcSerializable(accessorType = AccessorType.SETTER, includeHeader = true)
public class Client {

    private String name;
    private String lastName;
    private int age;
    private double balance;
    @CcEnum(EnumType.STRING)
    private HouseType houseType;

    public Client() {
    }

    public Client(String name, String lastName, int age, double balance, HouseType houseType) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.balance = balance;
        this.houseType = houseType;
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

    public HouseType getHouseType() {
        return houseType;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }
}
