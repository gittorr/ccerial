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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

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

    public static <T> void writeCollection(OutputStream out, Collection<T> ar, WriterFunction<T> componentWriter) throws IOException {
        if (ar == null || ar.isEmpty()) {
            out.write(0);
        } else {
            writeVarInt(out, ar.size());
            for (T t : ar) componentWriter.write(out, t);
        }
    }

    public static <T> void writeCollection(OutputStream out, Collection<T> ar, int count, WriterFunction<T> componentWriter) throws IOException {
        Iterator<T> it = ar != null ? ar.iterator() : null;
        for (int i = 0; i < count; i++) {
            componentWriter.write(out, ar != null && i < ar.size() ? it.next() : null);
        }
    }

    public static <T> Collection<T> readCollection(InputStream in, ReaderFunction<T> componentReader, Function<Integer, Collection<T>> creator) throws IOException {
        int count = readVarInt(in);
        return readCollection(in, count, componentReader, creator);
    }

    public static <T> Collection<T> readCollection(InputStream in, int count, ReaderFunction<T> componentReader, Function<Integer, Collection<T>> creator) throws IOException {
        Collection<T> ar = creator.apply(count);
        for (int i = 0; i < count; i++) {
            ar.add(componentReader.read(in));
        }
        return ar;
    }

    public static <T> void writeGenericArray(OutputStream out, T[] ar, WriterFunction<T> componentWriter) throws IOException {
        if (ar == null || ar.length == 0) {
            out.write(0);
        } else {
            writeVarInt(out, ar.length);
            for (T t : ar) componentWriter.write(out, t);
        }
    }

    public static <T> T[] readGenericArray(InputStream in, ReaderFunction<T> componentReader, Function<Integer, T[]> creator) throws IOException {
        int count = readVarInt(in);
        return readGenericArray(in, count, componentReader, creator);
    }

    public static <T> void writeGenericArray(OutputStream out, T[] ar, int count, WriterFunction<T> componentWriter) throws IOException {
        for (int i = 0; i < count; i++) {
            componentWriter.write(out, ar != null && i < ar.length ? ar[i] : null);
        }
    }

    public static <T> T[] readGenericArray(InputStream in, int count, ReaderFunction<T> componentReader, Function<Integer, T[]> creator) throws IOException {
        T[] ar = creator.apply(count);
        for (int i = 0; i < count; i++) {
            ar[i] = componentReader.read(in);
        }
        return ar;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void writeObject(OutputStream out, Object obj) throws IOException {
        if (obj == null) {
            out.write(0);
        } else {
            Serializer serializer = Ccerial.getSerializer(obj.getClass());
            if (serializer != null)
                serializer.serialize(out, obj);
            else
                throw new IOException("Can't serialize an unknown object type: " + obj.getClass().getName());
        }
    }

    public static <T> T readObject(InputStream in, Class<T> type) throws IOException {
        Serializer<T> serializer = Ccerial.getSerializer(type);
        if (serializer != null)
            return serializer.deserialize(in);
        else
            throw new IOException("Can't serialize an unknown object type: " + type.getName());
    }

    public static void writeFloats(OutputStream out, float[] fs) throws IOException {
        writeVarInt(out, fs.length);
        for (float f : fs) {
            writeVarInt(out, Float.floatToRawIntBits(f));
        }
    }

    public static void writeFloats(OutputStream out, float[] fs, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            writeInt(out, Float.floatToRawIntBits(fs != null && i < fs.length ? fs[i] : 0));
        }
    }

    public static float[] readFloats(InputStream in) throws IOException {
        int size = readVarInt(in);
        float[] fs = new float[size];
        for (int i = 0; i < size; i++) {
            fs[i] = Float.intBitsToFloat(readVarInt(in));
        }
        return fs;
    }

    public static float[] readFloats(InputStream in, int count) throws IOException {
        float[] fs = new float[count];
        for (int i = 0; i < count; i++) {
            fs[i] = Float.intBitsToFloat(readInt(in));
        }
        return fs;
    }

    public static void writeDoubles(OutputStream out, double[] ds) throws IOException {
        writeVarInt(out, ds.length);
        for (double d : ds) {
            writeVarLong(out, Double.doubleToLongBits(d));
        }
    }

    public static void writeDoubles(OutputStream out, double[] ds, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            writeLong(out, Double.doubleToLongBits(ds != null && i < ds.length ? ds[i] : 0d));
        }
    }

    public static double[] readDoubles(InputStream in) throws IOException {
        int size = readVarInt(in);
        double[] ds = new double[size];
        for (int i = 0; i < size; i++) {
            ds[i] = Double.longBitsToDouble(readVarLong(in));
        }
        return ds;
    }

    public static double[] readDoubles(InputStream in, int count) throws IOException {
        double[] ds = new double[count];
        for (int i = 0; i < count; i++) {
            ds[i] = Double.longBitsToDouble(readLong(in));
        }
        return ds;
    }

    public static void writeChars(OutputStream out, char[] chars) throws IOException {
        writeVarInt(out, chars.length);
        for (char ch : chars) {
            writeChar(out, ch);
        }
    }

    public static void writeChars(OutputStream out, char[] chars, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            if (chars != null && i < chars.length)
                writeChar(out, chars[i]);
            else
                writeChar(out, (char) 0);
        }
    }

    public static char[] readChars(InputStream in) throws IOException {
        int count = readVarInt(in);
        char[] chs = new char[count];
        for ( int i = 0; i < count; i++) {
            chs[i] = readChar(in);
        }
        return chs;
    }

    public static char[] readChars(InputStream in, int count) throws IOException {
        char[] chs = new char[count];
        for ( int i = 0; i < count; i++) {
            chs[i] = readChar(in);
        }
        return chs;
    }

    public static void writeInts(OutputStream out, int[] is, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            if (is != null && i < is.length)
                writeInt(out, is[i]);
            else
                writeInt(out, 0);
        }
    }

    public static void writeInts(OutputStream out, int[] is) throws IOException {
        writeVarInt(out, is.length);
        for (int i : is) {
            writeVarInt(out, i);
        }
    }

    public static int[] readInts(InputStream in) throws IOException {
        int size = readVarInt(in);
        int[] is = new int[size];
        for (int i = 0; i < size; i++) {
            is[i] = readVarInt(in);
        }
        return is;
    }

    public static int[] readInts(InputStream in, int count) throws IOException {
        int[] is = new int[count];
        for (int i = 0; i < count; i++) {
            is[i] = readInt(in);
        }
        return is;
    }

    public static void writeLongs(OutputStream out, long[] ls) throws IOException {
        writeVarInt(out, ls.length);
        for (long l : ls) {
            writeVarLong(out, l);
        }
    }

    public static void writeLongs(OutputStream out, long[] ls, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            if (ls != null && i < ls.length)
                writeLong(out, ls[i]);
            else
                writeLong(out, 0L);
        }
    }

    public static long[] readLongs(InputStream in) throws IOException {
        int size = readVarInt(in);
        long[] ls = new long[size];
        for (int i = 0; i < size; i++) {
            ls[i] = readVarLong(in);
        }
        return ls;
    }

    public static long[] readLongs(InputStream in, int count) throws IOException {
        long[] ls = new long[count];
        for (int i = 0; i < count; i++) {
            ls[i] = readLong(in);
        }
        return ls;
    }

    public static void writeString(OutputStream out, String s, String charsetName) throws IOException {
        writeBytes(out, s.getBytes(charsetName));
    }

    public static void writeString(OutputStream out, String s, int count, String charsetName) throws IOException {
        writeBytes(out, s.getBytes(charsetName), count);
    }

    public static WriterFunction<String> createStringWriter(final String charset) {
        return (out, s) -> writeString(out, s, charset);
    }

    public static String readString(InputStream in, String charsetName) throws IOException {
        byte[] bytes = readBytes(in);
        return new String(bytes, charsetName);
    }

    public static String readString(InputStream in, int count, String charsetName) throws IOException {
        byte[] bytes = readBytes(in, count);
        int i = 0; for (; i < count && bytes[i] != 0; i++) { }
        return new String(bytes, 0, i, charsetName);
    }

    public static void writeBytes(OutputStream out, byte[] bytes) throws IOException {
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    public static void writeBytes(OutputStream out, byte[] bytes, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            if (bytes != null && i < bytes.length)
                out.write(bytes[i]);
            else
                out.write(0);
        }
    }

    public static byte[] readBytes(InputStream in) throws IOException {
        int size = readVarInt(in);
        byte[] result = new byte[size];
        if (in.read(result) != size) throw newEndOfStreamException();
        return result;
    }

    public static byte[] readBytes(InputStream in, int count) throws IOException {
        byte[] result = new byte[count];
        if (in.read(result) != count) throw newEndOfStreamException();
        return result;
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

    public static void writeBoolean(OutputStream out, boolean b) throws IOException {
        out.write(b ? 1 : 0);
    }

    public static boolean readBoolean(InputStream in) throws IOException {
        return readByte(in) == 1;
    }

    public static void writeShort(OutputStream out, short s) throws IOException {
        out.write(s & 0xff);
        out.write((s >> 8) & 0xff);
    }

    public static short readShort(InputStream in) throws IOException {
        return (short) ((readByte(in) & 0xff) | ((readByte(in) & 0xff) << 8));
    }

    public static void writeChar(OutputStream out, char s) throws IOException {
        out.write(s & 0xff);
        out.write((s >> 8) & 0xff);
    }

    public static char readChar(InputStream in) throws IOException {
        return (char) ((readByte(in) & 0xff) | ((readByte(in) & 0xff) << 8));
    }

    public static void writeVarFloat(OutputStream out, float value) throws IOException {
        writeVarInt(out, Float.floatToRawIntBits(value));
    }

    public static void writeFloat(OutputStream out, float value) throws IOException {
        writeInt(out, Float.floatToRawIntBits(value));
    }

    public static void writeVarInt(OutputStream out, int value) throws IOException {
        while ((value & ~0x7F) != 0) {
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.write(value & 0x7F);
    }

    public static void writeInt(OutputStream out, int i) throws IOException {
        out.write(i & 0xff);
        out.write((i >> 8) & 0xff);
        out.write((i >> 16) & 0xff);
        out.write((i >> 24) & 0xff);
    }

    public static void writeVarDouble(OutputStream out, double value) throws IOException {
        writeVarLong(out, Double.doubleToLongBits(value));
    }

    public static void writeDouble(OutputStream out, double value) throws IOException {
        writeLong(out, Double.doubleToLongBits(value));
    }

    public static void writeVarLong(OutputStream out, long value) throws IOException {
        while ((value & ~0x7F) != 0) {
            out.write((int) ((value & 0x7F) | 0x80));
            value >>>= 7;
        }
        out.write((int) (value & 0x7F));
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
