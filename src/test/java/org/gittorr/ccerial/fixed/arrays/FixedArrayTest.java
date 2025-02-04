package org.gittorr.ccerial.fixed.arrays;

import org.gittorr.ccerial.Ccerial;
import org.gittorr.ccerial.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class FixedArrayTest {

    private static final String EXPECTED_DATA = "RG9vcgAAAAAAAAAAAAAAAAAAAABXaW5kb3cAAAAAAAAAAAAAAAAAAFRhYmxlAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB7FK5H4XokQJqZmZmZGSFAPQrXo3B9PkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
    private static final String EXPECTED_DATA2 = "AkRvb3IAAAAAAAAAAAAAAAAAAAAAV2luZG93AAAAAAAAAAAAAAAAAABUYWJsZQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAexSuR+F6JECamZmZmRkhQD0K16NwfT5AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAV2luZG93AAAAAAAAAAAAAAAAAABXYXJkcm9iZQAAAAAAAAAAAAAAAEJlZAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACamZmZmRkhQOxRuB6FK0lAw/UoXI/CNUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADwPwAAAAAAAABAAAAAAAAACEAAAAAAAAAIQAAAAAAAAABAAAAAAAAACEAAAAAAAAAQQAAAAAAAAABAAAAAAAAACEA=";

    @Test
    public void testFixedArray() throws IOException {
        // Testing fixed array serialization
        String[] names = {"Door","Window","Table"};
        double[] prices = {10.24,8.55,30.49};
        HouseItems houseItems = new HouseItems(names, prices, HouseType.HOUSE);
        Serializer<HouseItems> serializer = Ccerial.getSerializer(HouseItems.class);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.serialize(bos, houseItems);
        byte[] bytes = bos.toByteArray();
        String encodedData = Base64.getEncoder().encodeToString(bytes);

        Assert.assertEquals(284, bytes.length);
        Assert.assertEquals(EXPECTED_DATA, encodedData);
    }

    @Test
    public void test2dArray() throws IOException {

        String[] names = {"Door","Window","Table"};
        double[] prices = {10.24,8.55,30.49};
        HouseItems houseItems1 = new HouseItems(names, prices, HouseType.HOUSE);
        String[] names2 = {"Window","Wardrobe","Bed"};
        double[] prices2 = {8.55,50.34,21.76};
        HouseItems houseItems2 = new HouseItems(names2, prices2, HouseType.HOUSE);

        House house = new House(new HouseItems[]{houseItems1, houseItems2}, new double[][]{{1,2,3},{3,2,3},{4,2,3}});

        Serializer<House> serializer = Ccerial.getSerializer(House.class);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.serialize(bos, house);
        byte[] bytes = bos.toByteArray();

        String encodedData = Base64.getEncoder().encodeToString(bytes);

        Assert.assertEquals(641, bytes.length);
        Assert.assertEquals(EXPECTED_DATA2, encodedData);
    }

}
