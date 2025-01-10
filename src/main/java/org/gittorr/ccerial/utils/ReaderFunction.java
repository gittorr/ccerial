package org.gittorr.ccerial.utils;

import java.io.IOException;
import java.io.InputStream;

public interface ReaderFunction<T> {

    T read(InputStream in) throws IOException;

}
