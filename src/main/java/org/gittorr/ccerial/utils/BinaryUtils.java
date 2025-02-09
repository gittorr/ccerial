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
package org.gittorr.ccerial.utils;

import org.gittorr.ccerial.Ccerial;
import org.gittorr.ccerial.Serializer;
import org.gittorr.ccerial.SerializerFeature;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Binary utilities class.
 */
public final class BinaryUtils {

    public static Integer zeroIfNull(Integer i) {
        return i == null ? 0 : i;
    }

    public static Long zeroIfNull(Long i) {
        return i == null ? 0L : i;
    }

    public static Byte zeroIfNull(Byte i) {
        return i == null ? 0 : i;
    }

    public static Short zeroIfNull(Short i) {
        return i == null ? 0 : i;
    }

    public static Float zeroIfNull(Float i) {
        return i == null ? 0 : i;
    }

    public static Double zeroIfNull(Double i) {
        return i == null ? 0 : i;
    }

    public static boolean isNullOrEmpty(byte[] ar) {
        return ar == null || ar.length == 0;
    }

    public static boolean isNullOrEmpty(char[] ar) {
        return ar == null || ar.length == 0;
    }

    public static boolean isNullOrEmpty(int[] ar) {
        return ar == null || ar.length == 0;
    }

    public static boolean isNullOrEmpty(short[] ar) {
        return ar == null || ar.length == 0;
    }

    public static boolean isNullOrEmpty(long[] ar) {
        return ar == null || ar.length == 0;
    }

    public static boolean isNullOrEmpty(float[] ar) {
        return ar == null || ar.length == 0;
    }

    public static boolean isNullOrEmpty(double[] ar) {
        return ar == null || ar.length == 0;
    }

    public static <T> boolean isNullOrEmpty(T[] ar) {
        return ar == null || ar.length == 0;
    }

    public static <T> boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static <T> boolean isNullOrEmpty(Collection<T> ar) {
        return ar == null || ar.isEmpty();
    }

    public static <K,V> boolean isNullOrEmpty(Map<K,V> map) {
        return map == null || map.isEmpty();
    }

    public static <T, V> V getFieldIfNotNull(T obj, Function<T, V> getter) {
        if (obj == null) {
            return null;
        }
        return getter.apply(obj);
    }

    public static <T, V> V getFieldOrDefault(T obj, Function<T, V> getter, V defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        V value = getter.apply(obj);
        return (value == null) ? defaultValue : value;
    }

    public static <K, V> void writeMap(OutputStream out, Map<K, V> map, int count, WriterFunction<K> keyWriter, WriterFunction<V> valueWriter) throws IOException {
        int size = map != null ? map.size() : 0;
        if (count == -1) {
            writeVarInt(out, size);
            count = size;
        }
        Iterator<Map.Entry<K, V>> it = map != null ? map.entrySet().iterator() : null;
        for (int i = 0; i < count; i++) {
            if (map != null && i < map.size()) {
                Map.Entry<K, V> entry = it.next();
                keyWriter.write(out, entry.getKey());
                valueWriter.write(out, entry.getValue());
            } else {
                keyWriter.write(out, null);
                valueWriter.write(out, null);
            }
        }
    }

    public static <K, V> Map<K, V> readMap(InputStream in, int count, ReaderFunction<K> keyReader, ReaderFunction<V> valueReader, Function<Integer, Map<K,V>> creator) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        Map<K, V> map = creator.apply(count);
        for (int i = 0; i < count; i++) {
            map.put(keyReader.read(in), valueReader.read(in));
        }
        return map;
    }

    public static <T> void writeCollection(OutputStream out, Collection<T> ar, int count, WriterFunction<T> componentWriter) throws IOException {
        int size = ar != null ? ar.size() : 0;
        if (count == -1) {
            writeVarInt(out, size);
            count = size;
        }
        Iterator<T> it = ar != null ? ar.iterator() : null;
        for (int i = 0; i < count; i++) {
            componentWriter.write(out, ar != null && i < ar.size() ? it.next() : null);
        }
    }

    public static <T> Collection readCollection(InputStream in, int count, ReaderFunction<T> componentReader, Function<Integer, Collection<T>> creator) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        Collection<T> ar = creator.apply(count);
        for (int i = 0; i < count; i++) {
            ar.add(componentReader.read(in));
        }
        return ar;
    }

    public static <T> void writeGenericArray(OutputStream out, T[] ar, int count, WriterFunction<T> componentWriter) throws IOException {
        int length = ar != null ? ar.length : 0;
        if (count == -1) {
            writeVarInt(out, length);
            count = length;
        }
        for (int i = 0; i < count; i++) {
            componentWriter.write(out, ar != null && i < ar.length ? ar[i] : null);
        }
    }

    public static <T> T[] readGenericArray(InputStream in, int count, ReaderFunction<T> componentReader, Function<Integer, T[]> creator) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        T[] ar = creator.apply(count);
        for (int i = 0; i < count; i++) {
            ar[i] = componentReader.read(in);
        }
        return ar;
    }

    public static <U, V> void writeObject(OutputStream out, V obj, Class<V> type, Serializer<U> father) throws IOException {
        if (obj == null) {
            out.write(0);
        } else {
            @SuppressWarnings({"unchecked"})
            Class<V> c = type != null ? type : (Class<V>) obj.getClass();
            Serializer<V> serializer = Ccerial.getSerializer(c);
            if (father != null)
                copyFeatures(father, serializer);
            if (type == null)
                serializer.setFeatureEnabled(SerializerFeature.FORCE_HEADERS, true);
            serializer.serialize(out, obj);
        }
    }

    public static <T, U> T readObject(InputStream in, Class<T> type, Serializer<U> father) throws IOException {
        Serializer<T> serializer;
        if (type == null) {
            BufferedInputStream bis = new BufferedInputStream(in);
            bis.mark(8);
            int oid = readInt(bis);
            int version = readInt(bis);
            if (version != 0) throw new IllegalStateException("Versioning is not yet supported!");
            bis.reset();
            in = bis;
            serializer = Ccerial.getSerializer(oid);
            if (father != null)
                copyFeatures(father, serializer);
            serializer.setFeatureEnabled(SerializerFeature.FORCE_HEADERS, true);
        } else {
            serializer = Ccerial.getSerializer(type);
            if (father != null)
                copyFeatures(father, serializer);
        }
        return serializer.deserialize(in);
    }

    private static <T, U> void copyFeatures(Serializer<U> father, Serializer<T> serializer) {
        Stream.of(SerializerFeature.values()).forEach(feature -> serializer.setFeatureEnabled(feature, father.isFeatureEnabled(feature)));
    }

    public static void writeFloats(OutputStream out, float[] fs, int count) throws IOException {
        int length = fs != null ? fs.length : 0;
        if (count == -1) {
            writeVarInt(out, length);
            count = length;
        }
        for (int i = 0; i < count; i++) {
            writeFloat(out, fs != null && i < length ? fs[i] : 0);
        }
    }

    public static void writeVarFloats(OutputStream out, float[] fs, int count) throws IOException {
        int length = fs != null ? fs.length : 0;
        if (count == -1) {
            writeVarInt(out, length);
            count = length;
        }
        for (int i = 0; i < count; i++) {
            writeVarFloat(out, fs != null && i < length ? fs[i] : 0);
        }
    }

    public static float[] readFloats(InputStream in, int count) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        float[] fs = new float[count];
        for (int i = 0; i < count; i++) {
            fs[i] = readFloat(in);
        }
        return fs;
    }

    public static float[] readVarFloats(InputStream in, int count) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        float[] fs = new float[count];
        for (int i = 0; i < count; i++) {
            fs[i] = readVarFloat(in);
        }
        return fs;
    }

    public static void writeDoubles(OutputStream out, double[] ds, int count) throws IOException {
        int length = ds != null ? ds.length : 0;
        if (count == -1) {
            writeVarInt(out, length);
            count = length;
        }
        for (int i = 0; i < count; i++) {
            writeDouble(out, ds != null && i < ds.length ? ds[i] : 0d);
        }
    }

    public static void writeVarDoubles(OutputStream out, double[] ds, int count) throws IOException {
        int length = ds != null ? ds.length : 0;
        if (count == -1) {
            writeVarInt(out, length);
            count = length;
        }
        for (int i = 0; i < count; i++) {
            writeVarDouble(out, ds != null && i < ds.length ? ds[i] : 0d);
        }
    }

    public static double[] readDoubles(InputStream in, int count) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        double[] ds = new double[count];
        for (int i = 0; i < count; i++) {
            ds[i] = readDouble(in);
        }
        return ds;
    }

    public static double[] readVarDoubles(InputStream in, int count) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        double[] ds = new double[count];
        for (int i = 0; i < count; i++) {
            ds[i] = readVarDouble(in);
        }
        return ds;
    }

    public static void writeChars(OutputStream out, char[] chars, int count) throws IOException {
        int length = chars != null ? chars.length : 0;
        if (count == -1) {
            writeVarInt(out, length);
            count = length;
        }
        for (int i = 0; i < count; i++) {
            if (chars != null && i < length)
                writeChar(out, chars[i]);
            else
                writeChar(out, (char) 0);
        }
    }

    public static char[] readChars(InputStream in, int count) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        char[] chs = new char[count];
        for ( int i = 0; i < count; i++) {
            chs[i] = readChar(in);
        }
        return chs;
    }

    public static void writeInts(OutputStream out, int[] is, int count) throws IOException {
        int length = is != null ? is.length : 0;
        if (count == -1) {
            writeVarInt(out, length);
            count = length;
        }
        for (int i = 0; i < count; i++) {
            if (is != null && i < length)
                writeInt(out, is[i]);
            else
                writeInt(out, 0);
        }
    }

    public static void writeVarInts(OutputStream out, int[] is, int count) throws IOException {
        int length = is != null ? is.length : 0;
        if (count == -1) {
            writeVarInt(out, length);
            count = length;
        }
        for (int i = 0; i < count; i++) {
            if (is != null && i < length)
                writeVarInt(out, is[i]);
            else
                writeVarInt(out, 0);
        }
    }

    public static int[] readInts(InputStream in, int count) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        int[] is = new int[count];
        for (int i = 0; i < count; i++) {
            is[i] = readInt(in);
        }
        return is;
    }

    public static int[] readVarInts(InputStream in, int count) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        int[] is = new int[count];
        for (int i = 0; i < count; i++) {
            is[i] = readVarInt(in);
        }
        return is;
    }

    public static void writeLongs(OutputStream out, long[] ls, int count) throws IOException {
        int length = ls != null ? ls.length : 0;
        if (count == -1) {
            writeVarInt(out, length);
            count = length;
        }
        for (int i = 0; i < count; i++) {
            if (ls != null && i < length)
                writeLong(out, ls[i]);
            else
                writeLong(out, 0L);
        }
    }

    public static void writeVarLongs(OutputStream out, long[] ls, int count) throws IOException {
        int length = ls != null ? ls.length : 0;
        if (count == -1) {
            writeVarInt(out, length);
            count = length;
        }
        for (int i = 0; i < count; i++) {
            if (ls != null && i < length)
                writeVarLong(out, ls[i]);
            else
                writeVarLong(out, 0L);
        }
    }

    public static long[] readLongs(InputStream in, int count) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        long[] ls = new long[count];
        for (int i = 0; i < count; i++) {
            ls[i] = readLong(in);
        }
        return ls;
    }

    public static long[] readVarLongs(InputStream in, int count) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        long[] ls = new long[count];
        for (int i = 0; i < count; i++) {
            ls[i] = readVarLong(in);
        }
        return ls;
    }

    public static void writeString(OutputStream out, String s, int count, String charsetName) throws IOException {
        writeBytes(out, s != null ? s.getBytes(charsetName) : null, count);
    }

    public static WriterFunction<String> createStringWriter(final int count, final String charset) {
        return (out, s) -> writeString(out, s, count, charset);
    }

    public static String readString(InputStream in, int count, String charsetName) throws IOException {
        byte[] bytes = readBytes(in, count);
        int i = 0; for (; i < bytes.length && bytes[i] != 0; i++) { }
        return new String(bytes, 0, i, charsetName);
    }

    public static void writeBytes(OutputStream out, byte[] bytes, int count) throws IOException {
        int length = bytes != null ? bytes.length : 0;
        if (count == -1) {
            writeVarInt(out, length);
            count = length;
        }
        for (int i = 0; i < count; i++) {
            if (bytes != null && i < length)
                out.write(bytes[i]);
            else
                out.write(0);
        }
    }

    public static byte[] readBytes(InputStream in, int count) throws IOException {
        if (count == -1) {
            count = readVarInt(in);
        }
        byte[] result = new byte[count];
        if (in.read(result) != count) throw newEndOfStreamException();
        return result;
    }

    public static void writeByte(OutputStream out, Byte b) throws IOException {
        writeByte(out, b == null ? 0 : b);
    }

    public static void writeByte(OutputStream out, byte b) throws IOException {
        out.write(b & 0xff);
    }

    public static byte readByte(InputStream in) throws IOException {
        int b = in.read();
        if (b == -1) throw newEndOfStreamException();
        return (byte) (b & 0xff);
    }

    private static IOException newEndOfStreamException() {
        return new EOFException("Unexpected end of stream.");
    }

    public static void writeBoolean(OutputStream out, Boolean b) throws IOException {
        writeBoolean(out, b != null && b);
    }

    public static void writeBoolean(OutputStream out, boolean b) throws IOException {
        out.write(b ? 1 : 0);
    }

    public static boolean readBoolean(InputStream in) throws IOException {
        return readByte(in) == 1;
    }

    public static void writeShort(OutputStream out, Character s) throws IOException {
        writeShort(out, s == null ? 0 : s);
    }

    public static void writeShort(OutputStream out, short s) throws IOException {
        out.write(s & 0xff);
        out.write((s >> 8) & 0xff);
    }

    public static short readShort(InputStream in) throws IOException {
        return (short) ((readByte(in) & 0xff) | ((readByte(in) & 0xff) << 8));
    }

    public static void writeChar(OutputStream out, Character s) throws IOException {
        writeChar(out, s == null ? 0 : s);
    }

    public static void writeChar(OutputStream out, char s) throws IOException {
        out.write(s & 0xff);
        out.write((s >> 8) & 0xff);
    }

    public static char readChar(InputStream in) throws IOException {
        return (char) ((readByte(in) & 0xff) | ((readByte(in) & 0xff) << 8));
    }

    public static void writeVarFloat(OutputStream out, Float value) throws IOException {
        writeVarFloat(out, value == null ? 0f : value);
    }

    public static void writeVarFloat(OutputStream out, float value) throws IOException {
        writeVarInt(out, Float.floatToRawIntBits(value));
    }

    public static void writeFloat(OutputStream out, Float value) throws IOException {
        writeFloat(out, value == null ? 0f : value);
    }

    public static void writeFloat(OutputStream out, float value) throws IOException {
        writeInt(out, Float.floatToRawIntBits(value));
    }

    public static void writeVarInt(OutputStream out, Integer i) throws IOException {
        writeVarInt(out, i == null ? 0 : i);
    }

    public static void writeVarInt(OutputStream out, int value) throws IOException {
        while ((value & ~0x7F) != 0) {
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.write(value & 0x7F);
    }

    public static void writeInt(OutputStream out, Integer i) throws IOException {
        writeInt(out, i == null ? 0 : i);
    }

    public static void writeInt(OutputStream out, int i) throws IOException {
        out.write(i & 0xff);
        out.write((i >> 8) & 0xff);
        out.write((i >> 16) & 0xff);
        out.write((i >> 24) & 0xff);
    }

    public static void writeVarDouble(OutputStream out, Double value) throws IOException {
        writeVarDouble(out, value == null ? 0d : value);
    }

    public static void writeVarDouble(OutputStream out, double value) throws IOException {
        writeVarLong(out, Double.doubleToLongBits(value));
    }

    public static void writeDouble(OutputStream out, Double value) throws IOException {
        writeDouble(out, value == null ? 0d : value);
    }

    public static void writeDouble(OutputStream out, double value) throws IOException {
        writeLong(out, Double.doubleToLongBits(value));
    }

    public static void writeVarLong(OutputStream out, Long value) throws IOException {
        writeVarLong(out, value == null ? 0L : value);
    }

    public static void writeVarLong(OutputStream out, long value) throws IOException {
        while ((value & ~0x7F) != 0) {
            out.write((int) ((value & 0x7F) | 0x80));
            value >>>= 7;
        }
        out.write((int) (value & 0x7F));
    }

    public static void writeLong(OutputStream out, Long value) throws IOException {
        writeLong(out, value == null ? 0L : value);
    }

    public static void writeLong(OutputStream out, long l) throws IOException {
        out.write((int) (l & 0xff));
        out.write((int) ((l >> 8) & 0xff));
        out.write((int) ((l >> 16) & 0xff));
        out.write((int) ((l >> 24) & 0xff));
        out.write((int) ((l >> 32) & 0xff));
        out.write((int) ((l >> 40) & 0xff));
        out.write((int) ((l >> 48) & 0xff));
        out.write((int) ((l >> 56) & 0xff));
    }

    public static int readVarInt(InputStream in) throws IOException {
        int value = 0;
        int position = 0;
        int currentByte;

        while (true) {
            currentByte = readByte(in);
            if (currentByte == -1) {
                throw new EOFException("Unexpected end of stream while reading VarInt");
            }
            value |= (currentByte & 0x7F) << position;
            if ((currentByte & 0x80) == 0) {
                break;
            }
            position += 7;
            if (position >= 32) {
                throw new IOException("VarInt is too big");
            }
        }

        return value;
    }

    public static int readInt(InputStream in) throws IOException {
        return (readByte(in) & 0xFF) |
                ((readByte(in) & 0xFF) << 8) |
                ((readByte(in) & 0xFF) << 16) |
                ((readByte(in) & 0xFF) << 24);
    }

    public static float readVarFloat(InputStream in) throws IOException {
        return Float.intBitsToFloat(readVarInt(in));
    }

    public static float readFloat(InputStream in) throws IOException {
        return Float.intBitsToFloat(readInt(in));
    }

    public static long readVarLong(InputStream in) throws IOException {
        long value = 0;
        int position = 0;
        long currentByte;

        while (true) {
            currentByte = readByte(in);
            if (currentByte == -1) {
                throw new EOFException("Unexpected end of stream while reading VarLong");
            }
            value |= (currentByte & 0x7F) << position;
            if ((currentByte & 0x80) == 0) {
                break;
            }
            position += 7;
            if (position >= 64) {
                throw new IOException("VarLong is too big");
            }
        }

        return value;
    }

    public static long readLong(InputStream in) throws IOException {
        return (readByte(in) & 0xFFL) |
                ((readByte(in) & 0xFFL) << 8) |
                ((readByte(in) & 0xFFL) << 16) |
                ((readByte(in) & 0xFFL) << 24) |
                ((readByte(in) & 0xFFL) << 32) |
                ((readByte(in) & 0xFFL) << 40) |
                ((readByte(in) & 0xFFL) << 48) |
                ((readByte(in) & 0xFFL) << 56);
    }

    public static double readVarDouble(InputStream in) throws IOException {
        return Double.longBitsToDouble(readVarLong(in));
    }

    public static double readDouble(InputStream in) throws IOException {
        return Double.longBitsToDouble(readLong(in));
    }

}
