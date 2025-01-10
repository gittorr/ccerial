package org.gittorr.ccerial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serializer<E> {

    void serialize(OutputStream out, E entity) throws IOException;

    E deserialize(InputStream in) throws IOException;

}
