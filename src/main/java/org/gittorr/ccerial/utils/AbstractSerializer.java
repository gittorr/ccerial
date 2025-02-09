package org.gittorr.ccerial.utils;

import org.gittorr.ccerial.Serializer;
import org.gittorr.ccerial.SerializerFeature;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractSerializer<E> implements Serializer<E> {

    protected boolean featureForceHeaders = false;
    protected boolean featureSkipHeaders = false;
    protected boolean featureForceVariableSize = false;
    protected boolean featureForceZeroOrEmptyAsNull = false;

    @Override
    public void setFeatureEnabled(SerializerFeature feature, boolean enabled) {
        switch (feature) {
            case FORCE_HEADERS:
                if (featureSkipHeaders)
                    throw new IllegalStateException("Conflicting features FORCE_HEADERS and SKIP_HEADERS.");
                featureForceHeaders = enabled;
                break;
            case SKIP_HEADERS:
                if (featureForceHeaders)
                    throw new IllegalStateException("Conflicting features FORCE_HEADERS and SKIP_HEADERS.");
                featureSkipHeaders = enabled;
                break;
            case FORCE_VARIABLE_SIZE:
                featureForceVariableSize = enabled;
                break;
            case FORCE_ZERO_OR_EMPTY_AS_NULL:
                featureForceZeroOrEmptyAsNull = enabled;
                break;
            default:
                throw new IllegalArgumentException("Unknown feature: " + feature);
        }
    }

    @Override
    public boolean isFeatureEnabled(SerializerFeature feature) {
        switch (feature) {
            case FORCE_HEADERS:
                return featureForceHeaders;
            case SKIP_HEADERS:
                return featureSkipHeaders;
            case FORCE_VARIABLE_SIZE:
                return featureForceVariableSize;
            case FORCE_ZERO_OR_EMPTY_AS_NULL:
                return featureForceZeroOrEmptyAsNull;
            default:
                throw new IllegalArgumentException("Unknown feature: " + feature);
        }
    }

    protected byte[] nullIfEmptyOrZero(byte[] ar, boolean defaultBehavior) {
        if (featureForceZeroOrEmptyAsNull || defaultBehavior)
            return BinaryUtils.isNullOrEmpty(ar) ? null : ar;
        return ar;
    }

    protected char[] nullIfEmptyOrZero(char[] ar, boolean defaultBehavior) {
        if (featureForceZeroOrEmptyAsNull || defaultBehavior)
            return BinaryUtils.isNullOrEmpty(ar) ? null : ar;
        return ar;
    }

    protected int[] nullIfEmptyOrZero(int[] ar, boolean defaultBehavior) {
        if (featureForceZeroOrEmptyAsNull || defaultBehavior)
            return BinaryUtils.isNullOrEmpty(ar) ? null : ar;
        return ar;
    }

    protected short[] nullIfEmptyOrZero(short[] ar, boolean defaultBehavior) {
        if (featureForceZeroOrEmptyAsNull || defaultBehavior)
            return BinaryUtils.isNullOrEmpty(ar) ? null : ar;
        return ar;
    }

    protected long[] nullIfEmptyOrZero(long[] ar, boolean defaultBehavior) {
        if (featureForceZeroOrEmptyAsNull || defaultBehavior)
            return BinaryUtils.isNullOrEmpty(ar) ? null : ar;
        return ar;
    }

    protected float[] nullIfEmptyOrZero(float[] ar, boolean defaultBehavior) {
        if (featureForceZeroOrEmptyAsNull || defaultBehavior)
            return BinaryUtils.isNullOrEmpty(ar) ? null : ar;
        return ar;
    }

    protected double[] nullIfEmptyOrZero(double[] ar, boolean defaultBehavior) {
        if (featureForceZeroOrEmptyAsNull || defaultBehavior)
            return BinaryUtils.isNullOrEmpty(ar) ? null : ar;
        return ar;
    }

    protected <T> T[] nullIfEmptyOrZero(T[] ar, boolean defaultBehavior) {
        if (featureForceZeroOrEmptyAsNull || defaultBehavior)
            return BinaryUtils.isNullOrEmpty(ar) ? null : ar;
        return ar;
    }

    protected <T> String nullIfEmptyOrZero(String s, boolean defaultBehavior) {
        if (featureForceZeroOrEmptyAsNull || defaultBehavior)
            return BinaryUtils.isNullOrEmpty(s) ? null : s;
        return s;
    }

    protected <T, L extends Collection<T>> L nullIfEmptyOrZero(L ar, boolean defaultBehavior) {
        if (featureForceZeroOrEmptyAsNull || defaultBehavior)
            return BinaryUtils.isNullOrEmpty(ar) ? null : ar;
        return ar;
    }

    protected <K,V, M extends Map<K,V>> M nullIfEmptyOrZero(M map, boolean defaultBehavior) {
        if (featureForceZeroOrEmptyAsNull || defaultBehavior)
            return BinaryUtils.isNullOrEmpty(map) ? null : map;
        return map;
    }

    protected Byte nullIfEmptyOrZero(byte v, boolean defaultBehavior) {
        return v == 0 && (featureForceZeroOrEmptyAsNull || defaultBehavior) ? null : v;
    }

    protected Short nullIfEmptyOrZero(short v, boolean defaultBehavior) {
        return v == 0 && (featureForceZeroOrEmptyAsNull || defaultBehavior) ? null : v;
    }

    protected Integer nullIfEmptyOrZero(int v, boolean defaultBehavior) {
        return v == 0 && (featureForceZeroOrEmptyAsNull || defaultBehavior) ? null : v;
    }

    protected Long nullIfEmptyOrZero(long v, boolean defaultBehavior) {
        return v == 0 && (featureForceZeroOrEmptyAsNull || defaultBehavior) ? null : v;
    }

    protected Character nullIfEmptyOrZero(char v, boolean defaultBehavior) {
        return v == 0 && (featureForceZeroOrEmptyAsNull || defaultBehavior) ? null : v;
    }

    protected Float nullIfEmptyOrZero(float v, boolean defaultBehavior) {
        return v == 0 && (featureForceZeroOrEmptyAsNull || defaultBehavior) ? null : v;
    }

    protected Double nullIfEmptyOrZero(double v, boolean defaultBehavior) {
        return v == 0 && (featureForceZeroOrEmptyAsNull || defaultBehavior) ? null : v;
    }

    protected Boolean nullIfEmptyOrZero(boolean v, boolean defaultBehavior) {
        return !v && (featureForceZeroOrEmptyAsNull || defaultBehavior) ? null : v;
    }

    protected <E extends Enum<E>> E nullIfEmptyOrZero(Function<String, E> enumValueOf, String v, boolean defaultBehavior) {
        return v.isEmpty() && (featureForceZeroOrEmptyAsNull || defaultBehavior) ? null : enumValueOf.apply(v);
    }

    protected <V> void writeWithFeature(WriterFunction<V> variableWriter, WriterFunction<V> normalWriter, OutputStream out, V value) throws IOException {
        if (featureForceVariableSize)
            variableWriter.write(out, value);
        else
            normalWriter.write(out, value);
    }

    protected <V> void writeWithFeature(WriterFunctionSized<V> variableWriter, WriterFunctionSized<V> normalWriter, OutputStream out, V value, int count) throws IOException {
        if (featureForceVariableSize)
            variableWriter.write(out, value, -1);
        else
            normalWriter.write(out, value, count);
    }

}
