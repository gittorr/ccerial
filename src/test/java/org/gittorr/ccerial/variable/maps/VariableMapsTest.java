package org.gittorr.ccerial.variable.maps;

import org.gittorr.ccerial.Ccerial;
import org.gittorr.ccerial.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class VariableMapsTest {

    private static final String ENCODED_DATA = "AwpKb2huIFNtaXRoVQtEb3UgR2xhc3Nlc2QISmFuZSBEb2Vf";

    @Test
    public void testVariableMaps() throws IOException {
        // Create the object to be serialized
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Dou Glasses", 100);
        scores.put("Jane Doe", 95);
        scores.put("John Smith", 85);

        StatisticsMapData data = new StatisticsMapData(scores);

        // Retrieve the serializer
        Serializer<StatisticsMapData> serializer = Ccerial.getSerializer(StatisticsMapData.class);

        // Serialize it
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.serialize(bos, data);
        byte[] dataBytes = bos.toByteArray();
        String encodedData = Base64.getEncoder().encodeToString(dataBytes);

        Assert.assertEquals(36, dataBytes.length); // Map size verification
        Assert.assertEquals(ENCODED_DATA, encodedData);
        Assert.assertEquals(3, dataBytes[0]); // Map size verification
    }

    @Test
    public void testDeserialization() throws IOException {
        // Retrieve the serializer
        Serializer<StatisticsMapData> serializer = Ccerial.getSerializer(StatisticsMapData.class);

        // Deserialize it
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(ENCODED_DATA));
        StatisticsMapData data = serializer.deserialize(bis);

        Assert.assertEquals(3, data.getScores().size()); // Map size verification
        Assert.assertEquals(Integer.valueOf(100), data.getScores().get("Dou Glasses"));
        Assert.assertEquals(Integer.valueOf(95), data.getScores().get("Jane Doe"));
        Assert.assertEquals(Integer.valueOf(85), data.getScores().get("John Smith"));
    }
}
