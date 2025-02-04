package org.gittorr.ccerial.utils;

import org.gittorr.ccerial.Serializer;
import org.gittorr.ccerial.SerializerFeature;

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
}
