package org.gittorr.ccerial.variable.pojos;

import org.gittorr.ccerial.Ccerial;
import org.gittorr.ccerial.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class VariablePojoTest {

    private static final String ROBINSON = "Robinson";
    private static final String CRUSOE = "Crusoe";
    private static final int AGE = 27;
    private static final double BALANCE = 2700.15;
    private static final String ENCODED_DATA = "Rf/jsgAAAAAIUm9iaW5zb24GQ3J1c29lG82Zs+bMicbSQA==";

    @Test
    public void testVariablePojo() throws IOException {
        Serializer<Client> serializer = Ccerial.getSerializer(Client.class);
        Client client = new Client(ROBINSON, CRUSOE, AGE, BALANCE);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.serialize(bos, client);
        byte[] data = bos.toByteArray();
        String encodedData = Base64.getEncoder().encodeToString(data);
        Assert.assertEquals(34, data.length);
        Assert.assertEquals(ENCODED_DATA, encodedData);
        Client backClient = serializer.deserialize(new ByteArrayInputStream(data));
        Assert.assertEquals(ROBINSON, backClient.getName());
        Assert.assertEquals(CRUSOE, backClient.getLastName());
        Assert.assertEquals(AGE, backClient.getAge());
        Assert.assertEquals(BALANCE, backClient.getBalance(), 0d);
    }

}
