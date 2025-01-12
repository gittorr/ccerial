package org.gittorr.ccerial.fixed.pojos;

import org.gittorr.ccerial.Ccerial;
import org.gittorr.ccerial.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class FixedPojoTest {

    private static final String ENCODED_DATA = "Um9iaW5zb24AAAAAAAAAAAAAAAAAAAAAAAAAAAAAQ3J1c29lAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGwAAAM3MzMxMGKVA";
    private static final String ROBINSON = "Robinson";
    private static final String CRUSOE = "Crusoe";
    private static final int AGE = 27;
    private static final double BALANCE = 2700.15;

    @Test
    public void testFixedPojo() throws IOException {
        Serializer<Customer> serializer = Ccerial.getSerializer(Customer.class);
        Customer customer = new Customer(ROBINSON, CRUSOE, AGE, BALANCE);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.serialize(bos, customer);
        byte[] data = bos.toByteArray();
        String encodedData = Base64.getEncoder().encodeToString(data);
        Assert.assertEquals(72, data.length);
        Assert.assertEquals(ENCODED_DATA, encodedData);
        Customer backCustomer = serializer.deserialize(new ByteArrayInputStream(data));
        Assert.assertEquals(ROBINSON, backCustomer.getName());
        Assert.assertEquals(CRUSOE, backCustomer.getLastName());
        Assert.assertEquals(AGE, backCustomer.getAge());
        Assert.assertEquals(BALANCE, backCustomer.getBalance(), 0d);
    }

}
