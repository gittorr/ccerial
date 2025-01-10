package org.gittorr.ccerial;

@CcSerializable(accessorType = AccessorType.CONSTRUCTOR)
public record TestClassRecord(Integer id,
                              String name,
                              String[] wallets) {
}
