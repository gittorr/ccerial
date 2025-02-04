package org.gittorr.ccerial.nested.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.gittorr.ccerial.CcSerializable;

@CcSerializable
@Getter
@Setter
@AllArgsConstructor
public class OrderItem {

    private String description;

    private double unitValue;

    private double amount;

}
