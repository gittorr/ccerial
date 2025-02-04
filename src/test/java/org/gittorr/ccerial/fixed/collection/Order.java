package org.gittorr.ccerial.fixed.collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gittorr.ccerial.CcArray;
import org.gittorr.ccerial.CcSerializable;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@CcSerializable(variableSize = false)
public class Order {

    private double total;

    @CcArray(count = 10, componentCount = 0)
    private List<Double> itemValues;

    @CcArray(count = 10, componentCount = 30)
    private Set<String> itemNames;

}
