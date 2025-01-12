package org.gittorr.ccerial.records;

import org.gittorr.ccerial.AccessorType;
import org.gittorr.ccerial.CcSerializable;

@CcSerializable(accessorType = AccessorType.CONSTRUCTOR)
public record ConfigData(
        String hostname,
        int port,
        String[] whitelist
) {

}
