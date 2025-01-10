# Ccerial

**Ccerial** is a high-performance serialization library for Java, inspired by the compactness and efficiency of C-style binary serialization. Designed for developers who need fine-grained control over serialization, Ccerial provides flexibility, speed, and customization for serializing Java objects.

---

## Features

- **High Performance:** Reflection-free serialization using generated code.
- **Compact Binary Format:** Optimized binary output with optional headers.
- **Customizable Serialization:** Control over field sizes (fixed or variable), null handling, and optional headers.
- **Versioning Support:** Includes metadata for object versioning to enable backward and forward compatibility.
- **Lightweight:** Minimal dependencies and straightforward integration.
- **Annotation-Driven:** Configure serialization with intuitive annotations.

---

## Installation

Add Ccerial to your Maven project:

```xml
<dependency>
    <groupId>org.gittorr</groupId>
    <artifactId>ccerial</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Quick Start

### 1. Annotate Your Class

Mark your class with `@CcSerializable` to make it serializable:

```java
import org.gittorr.ccerial.CcSerializable;

@CcSerializable
public class User {
    private String name;
    private int age;

    // Getters and setters
}
```

### 2. Serialize and Deserialize

Use `Ccerial` to obtain the serializer and perform serialization/deserialization:

```java
import org.gittorr.ccerial.Ccerial;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        User user = new User();
        user.setName("John");
        user.setAge(30);

        // Serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Ccerial.getSerializer(User.class).serialize(user, out);

        byte[] data = out.toByteArray();

        // Deserialize
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        User deserializedUser = Ccerial.getSerializer(User.class).deserialize(in);

        System.out.println(deserializedUser.getName()); // Output: John
        System.out.println(deserializedUser.getAge());  // Output: 30
    }
}
```

---

## Annotations Overview

### `@CcSerializable`
Marks a class as serializable and allows customization.

| Property           | Description                                                                                | Default        |
|--------------------|--------------------------------------------------------------------------------------------|----------------|
| `accessorType`     | Specifies how fields are accessed (`SETTER`, `FIELD`, `CONSTRUCTOR`).                      | Constructor    |
| `properties`       | Defines the fields to serialize and their order. Use `*` for all or a comma-separated list. | `*`            |
| `classIdentifier`  | Custom identifier for the class (used for complex objects).                                | `0`            |
| `variableSize`     | Allows fields to have variable sizes.                                                      | `true`         |
| `nullIsZeroOrEmpty`| Treats `null` values as zero (numeric) or empty (strings/arrays).                          | `true`         |
| `includeHeader`    | Includes metadata headers like id and version.                                             | `false`        |

### `@CcArray`
Customizes array, collection, or string serialization.

| Property             | Description                              | Default  |
|----------------------|------------------------------------------|----------|
| `count`              | Fixed size for an array, collection, or string. | `-1`     |
| `nullIsEmpty`        | Treats `null` arrays or collections as empty.   | `true`   |
| `stringCharsetName`  | Charset for string serialization.              | `UTF-8`  |
| `stringAsCharArray`  | Serializes strings as wide char arrays.         | `false`  |

### `@CcValue`
Customizes individual field serialization.

| Property             | Description                              | Default |
|----------------------|------------------------------------------|---------|
| `variableSize`       | Allows the field to have variable size.  | `false` |
| `nullIsZeroOrEmpty`  | Treats `null` values as zero or empty.   | `true`  |

---

## Advanced Features

### Headers
With `includeHeader=true`, the serialized output includes:
- **Class Identifier**: Defined by the `classIdentifier` or automatically generated.
- **Version**: Adds the version, but currently it will be zero.

---

## Limitations
- Currently optimized for Java environments only.
- No built-in support for schema export/import across languages (e.g., like Protobuf).

---

## Contributing
We welcome contributions! Please visit the [GitHub repository](https://github.com/gittorr/ccerial) to open issues, suggest features, or submit pull requests.

---

## License
Ccerial is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

