package org.gittorr.ccerial.utils;

public interface ClassIdentifierGenerator {

    int generateIdentifier(String className);

    final ClassIdentifierGenerator INSTANCE = new FNV1aClassIdentifierGenerator();

    class FNV1aClassIdentifierGenerator implements ClassIdentifierGenerator {
        private static final int FNV_OFFSET_BASIS = 0x811C9DC5;
        private static final int FNV_PRIME = 0x01000193;

        public int generateIdentifier(String className) {
            int hash = FNV_OFFSET_BASIS;
            for (char c : className.toCharArray()) {
                hash ^= c;
                hash *= FNV_PRIME;
            }
            return hash;
        }
    }

}