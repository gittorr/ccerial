package org.gittorr.ccerial.utils.impl;

import org.gittorr.ccerial.utils.FieldAccessorWriter;

import javax.lang.model.type.TypeKind;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractFieldAccessorWriter implements FieldAccessorWriter {
    final TypeKind kind;
    final boolean variable;
    final String typeName;

    public AbstractFieldAccessorWriter(TypeKind kind, boolean variable, String typeName) {
        this.kind = kind;
        this.variable = variable;
        this.typeName = typeName;
    }

    @Override
    public TypeKind getKind() {
        return kind;
    }

    @Override
    public boolean isVariable() {
        return variable;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractFieldAccessorWriter)) return false;

        AbstractFieldAccessorWriter that = (AbstractFieldAccessorWriter) o;

        if (variable != that.variable) return false;
        return kind == that.kind && typeName.equals(that.typeName);
    }

    @Override
    public int hashCode() {
        int result = typeName.hashCode() * 31 + kind.hashCode();
        result = 31 * result + (variable ? 1 : 0);
        return result;
    }

    private static final Pattern ACC_PAT = Pattern.compile("^(set|get)(\\w+)\\(.*?");

    private static final Pattern RACC_PAT = Pattern.compile("^(\\w+)\\(.*?");

    protected String toCtorArgName(String accessorName, boolean isRecord) {
        Matcher m = (isRecord ? RACC_PAT : ACC_PAT).matcher(accessorName);
        if (m.matches()) {
            return "arg" + fstUp(m.group(isRecord ? 1 : 2));
        } else throw new RuntimeException("Error converting constructor argument name");
    }

    private String fstUp(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

    protected String toSetterName(String accessorName, boolean isRecord) {
        Matcher m = (isRecord ? RACC_PAT : ACC_PAT).matcher(accessorName);
        if (m.matches()) {
            return (isRecord ? "" : "set") + m.group(isRecord ? 1 : 2);
        } else throw new RuntimeException("Error converting setter name");
    }
}
