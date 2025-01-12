package org.gittorr.ccerial.records;

import org.gittorr.ccerial.Ccerial;
import org.gittorr.ccerial.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class RecordSerializationTest {

    private static final String ENCODED_DATA = "CzExLjIyLjMzLjQ0kD8CCzIyLjMzLjQ0LjU1CzExLjMzLjIyLjQ0";

    @Test
    public void testRecordSerialization() throws IOException {
        ConfigData configData = new ConfigData("11.22.33.44", 8080, new String[]{"22.33.44.55", "11.33.22.44"});
        Serializer<ConfigData> serializer = Ccerial.getSerializer(ConfigData.class);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.serialize(bos, configData);
        byte[] bytes = bos.toByteArray();

        String encodedData = Base64.getEncoder().encodeToString(bytes);
        Assert.assertEquals(39, bytes.length);
        Assert.assertEquals(ENCODED_DATA, encodedData);
    }

}
