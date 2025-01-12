/*
 * Copyright 2025 GitTorr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For inquiries, visit https://gittorr.org
 */
package org.gittorr.ccerial.processor;

import org.gittorr.ccerial.AccessorType;
import org.gittorr.ccerial.CcArray;
import org.gittorr.ccerial.CcValue;
import org.gittorr.ccerial.CcSerializable;
import org.gittorr.ccerial.utils.ClassIdentifierGenerator;
import org.gittorr.ccerial.utils.FieldAccessorWriter;
import org.gittorr.ccerial.utils.FieldAccessorWriterManager;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedAnnotationTypes("org.gittorr.ccerial.CcSerializable")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class CcerialProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        FieldAccessorWriterManager.setTypeUtils(processingEnv.getTypeUtils());
        // Iterates over each element annotated with @Ccerializable
        for (Element el : roundEnv.getElementsAnnotatedWith(CcSerializable.class)) {
            // The full name of the original class
            TypeElement element = (TypeElement) el;
            String className = element.getQualifiedName().toString();
            String packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
            String generatedClassName = "ccerial." + className + "_CcerialSerializer";
            CcSerializable annotation = element.getAnnotation(CcSerializable.class);
            boolean isRecord = !element.getRecordComponents().isEmpty();
            int objId = annotation.classIdentifier() != 0 ? annotation.classIdentifier() : ClassIdentifierGenerator.INSTANCE.generateIdentifier(className);
            try {
                // Create the output file
                JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(generatedClassName);

                // Write the code of the generated class
                try (Writer writer = sourceFile.openWriter()) {
                    writer.write("package ccerial." + packageName + ";\n\n");
                    writer.write("import org.gittorr.ccerial.Serializer;\n");
                    writer.write("import org.gittorr.ccerial.utils.BinaryUtils;\n\n");
                    writer.write("public class " + generatedClassName.substring(packageName.length() + 9) + " implements Serializer<"+className+"> {\n");

                    // Generate serialization method
                    writer.write("    public void serialize(java.io.OutputStream out, " + className + " obj) throws java.io.IOException {\n");
                    if (annotation.includeHeader()) {
                        writer.write("\t\tBinaryUtils.writeInt(out, " + objId + ");\n");
                        writer.write("\t\tBinaryUtils.writeInt(out, 0);\n"); // reserved for version
                    }
                    writeSerialization(writer, annotation, element, false, isRecord);
                    writer.write("    }\n");

                    // Generate deserialization method
                    writer.write("    public " + className + " deserialize(java.io.InputStream in) throws java.io.IOException {\n");
                    if (annotation.includeHeader()) {
                        writer.write("\t\tif (BinaryUtils.readInt(in) != " + objId + ") \n");
                        writer.write("\t\t\tthrow new java.io.IOException(\"Invalid object identifier\"); \n");
                        writer.write("\t\tif (BinaryUtils.readInt(in) != 0)\n"); // version must be 0
                        writer.write("\t\t\tthrow new java.io.IOException(\"Invalid object version\"); \n");
                    }
                    if (annotation.accessorType() != AccessorType.CONSTRUCTOR) {
                        writer.write("\t\t" + className + " obj = new " + className + "();\n");
                    }
                    writeSerialization(writer, annotation, element, true, isRecord);
                    if (annotation.accessorType() != AccessorType.CONSTRUCTOR) {
                        writer.write("\t\treturn obj;\n");
                    } else {
                        writer.write("\t\treturn new " + className + "(" + getCtorArgs(annotation, element) + ");\n");
                    }
                    writer.write("    }\n");

                    writer.write("}\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private String getCtorArgs(CcSerializable annotation, Element element) {
        String properties = annotation.properties();
        List<String> allProperties = Stream.of(properties.split("\\*")).map(String::trim).collect(Collectors.toList());
        if (properties.equals("*")) {
            allProperties = element.getEnclosedElements().stream().filter(field -> field.getKind().equals(ElementKind.FIELD)
                    && !field.getModifiers().contains(Modifier.STATIC)).map(Object::toString).collect(Collectors.toList());
        }
        if (allProperties.isEmpty())
            throw new IllegalStateException("No properties to serialize at " + element.getSimpleName());
        return allProperties.stream().map(fieldName -> "arg" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).reduce((a,b) -> a + ", " + b).orElse("");
    }

    private void writeSerialization(Writer writer, CcSerializable annotation, Element element, boolean deserialize, boolean isRecord) {
        String properties = annotation.properties();
        List<String> allProperties = Stream.of(properties.split("\\*")).map(String::trim).collect(Collectors.toList());
        Map<String, ? extends Element> fields = element.getEnclosedElements().stream().filter(field -> field.getKind().equals(ElementKind.FIELD)
                && !field.getModifiers().contains(Modifier.STATIC)).collect(Collectors.toMap(Object::toString, field -> field));
        Map<String, ? extends Element> methods = element.getEnclosedElements().stream().filter(field -> field.getKind().equals(ElementKind.METHOD)
                && !field.getModifiers().contains(Modifier.STATIC)).collect(Collectors.toMap(Object::toString, field -> field));
        if (properties.equals("*")) {
            allProperties = element.getEnclosedElements().stream().filter(field -> field.getKind().equals(ElementKind.FIELD)
                    && !field.getModifiers().contains(Modifier.STATIC)).map(Object::toString).collect(Collectors.toList());
        }
        if (allProperties.isEmpty())
            throw new IllegalStateException("No properties to serialize at " + element.getSimpleName());
        allProperties.forEach(fieldName ->
            writeField(writer, fields, methods, fieldName, annotation, deserialize, isRecord));
    }

    private void writeField(Writer writer, Map<String, ? extends Element> fields, Map<String, ? extends Element> methods,
                                   String fieldName, CcSerializable annotation, boolean deserialize, boolean isRecord) {
        Element fieldEl = fields.get(fieldName);
        AccessorType accessorType = annotation.accessorType();
        boolean variableSize = annotation.variableSize();
        if (fieldEl.asType().getKind() == TypeKind.ARRAY || fieldEl.asType().toString().equals("java.lang.String")) {
            CcArray ccArray = fieldEl.getAnnotation(CcArray.class);
            if (ccArray != null) {
                variableSize = ccArray.count() == -1;
            }
        } else {
            CcValue ccValue = fieldEl.getAnnotation(CcValue.class);
            if (ccValue != null) {
                variableSize = ccValue.variableSize();
            }
        }
        try {
            String accessorGetter = getAccessorGetter(fieldName, fieldEl, accessorType, methods, isRecord);
            FieldAccessorWriter fieldAccessorWriter = FieldAccessorWriterManager.getFieldAccessorWriter(fieldEl.asType(), variableSize);
            if (fieldAccessorWriter == null) {
                throw new IllegalStateException("Can't find a field accessor writer for type " + fieldEl.asType() + " and variableSize=" + variableSize);
            }
            if (deserialize)
                fieldAccessorWriter.writeReader(writer, accessorGetter, fieldEl, annotation, isRecord);
            else
                fieldAccessorWriter.writeWriter(writer, accessorGetter, fieldEl, annotation, isRecord);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getAccessorGetter(String fieldName, Element fieldEl, AccessorType accessorType, Map<String, ? extends Element> methods, boolean isRecord) {
        if (isRecord) {
            return fieldName + "()";
        } else {
            String getterName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1) + "()";
            Element element = methods.get(getterName);
            if (element != null)
                return getterName;
            if (fieldEl.getModifiers().contains(Modifier.PUBLIC))
                return fieldName;
            throw new IllegalStateException("Field " + fieldName + " has no accessor.");
        }
    }
}
