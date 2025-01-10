package org.gittorr.ccerial.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface WriterFunction<T> {

    void write(OutputStream out, T value) throws IOException;

}
