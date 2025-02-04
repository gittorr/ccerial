package org.gittorr.ccerial.nested.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.gittorr.ccerial.CcSerializable;
import org.gittorr.ccerial.fixed.pojos.Customer;

import java.util.List;
import java.util.Set;

@CcSerializable
@Getter
@Setter
@AllArgsConstructor
public class CustomerOrder {

    private double total;

    private List<OrderItem> items;

    private Customer customer;
}
