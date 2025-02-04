package org.gittorr.ccerial.fixed.collection;

import org.gittorr.ccerial.Ccerial;
import org.gittorr.ccerial.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;

public class FixedCollectionTest {

    private static final String EXPECTED_DATA = "AAAAAAAAWUAAAAAAAMBQQAAAAAAAgEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEJyZWFkcwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFNhdXNhZ2VzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";

    @Test
    public void testFixedCollection() throws IOException {

        Order order = new Order(100.0, List.of(67.0, 33.0), new LinkedHashSet<>(List.of("Breads", "Sausages")));
        Serializer<Order> serializer = Ccerial.getSerializer(Order.class);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.serialize(bos, order);
        byte[] bytes = bos.toByteArray();

        String encodedData = Base64.getEncoder().encodeToString(bytes);

        Assert.assertEquals(388, bytes.length);
        Assert.assertEquals(EXPECTED_DATA, encodedData);
    }

}
