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
public class HouseItems {

    @CcArray(count = 10, componentCount = 20)
    private String[] itemNames;

    @CcArray(count = 10, componentCount = 0)
    private double[] prices;

    private HouseType type;

}
