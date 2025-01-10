package org.gittorr.ccerial;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class CcerialProcessorTest {


    @Test
    public void testGeneratedCodeFor() throws Exception {
        // Path to the generated file
        Path generatedFilePath = Path.of("target/generated-test-sources/test-annotations/ccerial/org/gittorr/ccerial/TestClass_CcerialSerializer.java");

        testGeneratedCodeFor(generatedFilePath, "public org.gittorr.ccerial.TestClass deserialize");
    }

    @Test
    public void testGeneratedCodeRecord() throws Exception {
        // Path to the generated file
        Path generatedFilePath = Path.of("target/generated-test-sources/test-annotations/ccerial/org/gittorr/ccerial/TestClassRecord_CcerialSerializer.java");

        testGeneratedCodeFor(generatedFilePath, "public org.gittorr.ccerial.TestClassRecord deserialize");
    }

    private static void testGeneratedCodeFor(Path generatedFilePath, String deserializeMethodSignature) throws IOException {
        // Check if the generated file exists
        assertTrue("The generated file was not found!", Files.exists(generatedFilePath));

        // Reads the contents of the generated file
        String generatedCode = Files.readString(generatedFilePath, StandardCharsets.UTF_8);

        // Checks if the generated code contains the expected methods
        assertTrue("Generated code does not contain the expected serialize method!", generatedCode.contains("public void serialize"));
        assertTrue("Generated code does not contain the expected deserialize method!", generatedCode.contains(deserializeMethodSignature));
    }
}
