package org.gittorr.ccerial.fixed.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gittorr.ccerial.AccessorType;
import org.gittorr.ccerial.CcArray;
import org.gittorr.ccerial.CcSerializable;

@CcSerializable(accessorType = AccessorType.SETTER, variableSize = false)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @CcArray(count = 30)
    private String name;
    @CcArray(count = 30)
    private String lastName;
    private int age;
    private double balance;

}
