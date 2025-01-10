package org.gittorr.ccerial;

@CcSerializable(accessorType = AccessorType.CONSTRUCTOR)
public class TestClass {
    private Integer id;
    private String name;
    private String[] wallets;

    public TestClass(Integer id, String name, String[] wallets) {
        this.id = id;
        this.name = name;
        this.wallets = wallets;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String[] getWallets() { return wallets; }
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setWallets(String[] wallets) { this.wallets = wallets; }
}
