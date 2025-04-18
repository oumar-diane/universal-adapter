/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.support.jsse;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * KeyManager to select a key with desired alias while delegating processing to specified KeyManager Can be used both
 * with server and client sockets
 */
public class AliasedX509ExtendedKeyManager extends X509ExtendedKeyManager {
    private final String keyAlias;
    private final X509KeyManager keyManager;

    /**
     * Construct KeyManager instance
     *
     * @param keyAlias   Alias of the key to be selected
     * @param keyManager Instance of KeyManager to be wrapped
     */
    public AliasedX509ExtendedKeyManager(String keyAlias, X509KeyManager keyManager) {
        this.keyAlias = keyAlias;
        this.keyManager = keyManager;
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return keyAlias == null ? keyManager.chooseClientAlias(keyType, issuers, socket) : keyAlias;
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return keyAlias == null ? keyManager.chooseServerAlias(keyType, issuers, socket) : keyAlias;
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return keyManager.getClientAliases(keyType, issuers);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return keyManager.getServerAliases(keyType, issuers);
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return keyManager.getCertificateChain(alias);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return keyManager.getPrivateKey(alias);
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        return keyAlias == null ? super.chooseEngineServerAlias(keyType, issuers, engine) : keyAlias;
    }

    @Override
    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        return keyAlias == null ? super.chooseEngineClientAlias(keyType, issuers, engine) : keyAlias;
    }
}
