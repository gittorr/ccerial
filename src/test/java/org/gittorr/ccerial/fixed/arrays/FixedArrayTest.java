package org.gittorr.ccerial.fixed.arrays;

import org.gittorr.ccerial.Ccerial;
import org.gittorr.ccerial.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class FixedArrayTest {

    private static final String EXPECTED_DATA = "BERvb3IGV2luZG93BVRhYmxlexSuR+F6JECamZmZmRkhQD0K16NwfT5A";

    @Test
    public void testFixedArray() throws IOException {
        // Testing fixed array serialization
        String[] names = {"Door","Window","Table"};
        double[] prices = {10.24,8.55,30.49};
        HouseItems houseItems = new HouseItems(names, prices);
        Serializer<HouseItems> serializer = Ccerial.getSerializer(HouseItems.class);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.serialize(bos, houseItems);
        byte[] bytes = bos.toByteArray();
        String encodedData = Base64.getEncoder().encodeToString(bytes);

        Assert.assertEquals(42, bytes.length);
        Assert.assertEquals(EXPECTED_DATA, encodedData);
    }

}
