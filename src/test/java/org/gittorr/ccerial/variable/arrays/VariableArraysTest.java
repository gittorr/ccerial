package org.gittorr.ccerial.variable.arrays;

import org.gittorr.ccerial.Ccerial;
import org.gittorr.ccerial.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class VariableArraysTest {

    private static final String ENCODED_DATA = "BA1OYW5uaWUgTGFyc2VuDEt1cnRpcyBNZWppYQtOaXRhIEhhbnNlbhFHd2VuZG9seW4gV2hlZWxlcgRxjgGFAY0B";

    @Test
    public void testVariableArrays() throws IOException {
        // create the object to be serialized
        String[] names = {"Nannie Larsen", "Kurtis Mejia", "Nita Hansen", "Gwendolyn Wheeler"};
        int[] scores = {113, 142, 133, 141};
        StatisticsData data = new StatisticsData(names, scores);
        // retrieve the serializer
        Serializer<StatisticsData> serializer = Ccerial.getSerializer(StatisticsData.class);
        // serialize it
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.serialize(bos, data);
        byte[] dataBytes = bos.toByteArray();
        String encodedData = Base64.getEncoder().encodeToString(dataBytes);

        Assert.assertEquals(66, dataBytes.length);
        Assert.assertEquals(ENCODED_DATA, encodedData);

        Assert.assertEquals(4, dataBytes[0]);
        Assert.assertEquals(13, dataBytes[1]);
    }

}
