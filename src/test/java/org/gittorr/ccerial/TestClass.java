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
