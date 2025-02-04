package org.gittorr.ccerial.fixed.arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.gittorr.ccerial.AccessorType;
import org.gittorr.ccerial.CcArray;
import org.gittorr.ccerial.CcSerializable;

@CcSerializable(accessorType = AccessorType.CONSTRUCTOR, variableSize = false)
@Getter
@Setter
@AllArgsConstructor
public class House {

    private HouseItems[] items;

    @CcArray(count = 3, componentCount = 3)
    private double[][] metrics;

}
