package org.gittorr.ccerial.nested.objects;

import org.gittorr.ccerial.Ccerial;
import org.gittorr.ccerial.Serializer;
import org.gittorr.ccerial.fixed.pojos.Customer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class NestedObjectsTest {

    private static final String ENCODED_DATA = "s+bMmbPm7JtAAgVCcmVhZM2Zs+bMmfOIQICAgICAgICKQARNaWxrs+bMmbPmzPs/gICAgICAgPg/4oK9TgAAAABKb2UAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABNYWx0c29uAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAXI/C9SicWUA=";

    @Test
    public void testNestedObject() throws IOException {
        CustomerOrder co = new CustomerOrder(23.7, List.of(
                new OrderItem("Bread", 4.45, 5),
                new OrderItem("Milk", 1.45, 1)
        ), new Customer("Joe", "Maltson", 32, 102.44));

        Serializer<CustomerOrder> serializer = Ccerial.getSerializer(CustomerOrder.class);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.serialize(bos, co);
        byte[] data = bos.toByteArray();
        String encodedData = Base64.getEncoder().encodeToString(data);
        Assert.assertEquals(137, data.length);
        Assert.assertEquals(ENCODED_DATA, encodedData);

        CustomerOrder customerOrderDs = serializer.deserialize(new ByteArrayInputStream(data));

        Assert.assertEquals(23.7, customerOrderDs.getTotal(), 0.01);
        Assert.assertEquals(2, customerOrderDs.getItems().size());
        Assert.assertEquals("Bread", customerOrderDs.getItems().get(0).getDescription());
        Assert.assertEquals("Milk", customerOrderDs.getItems().get(1).getDescription());
        Assert.assertEquals("Joe", customerOrderDs.getCustomer().getName());
        Assert.assertEquals("Maltson", customerOrderDs.getCustomer().getLastName());
        Assert.assertEquals(32, customerOrderDs.getCustomer().getAge());
        Assert.assertEquals(102.44, customerOrderDs.getCustomer().getBalance(), 0.001);
    }

}
