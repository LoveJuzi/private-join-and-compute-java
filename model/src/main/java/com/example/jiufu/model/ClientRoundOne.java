package com.example.jiufu.model;

import java.util.List;

public class ClientRoundOne {
    private List<byte[]> clientCipherKeys;
    private List<byte[]> serverCipherKeys2;
    private byte[] serverPrivateKey;

    public List<byte[]> getClientCipherKeys() {
        return clientCipherKeys;
    }

    public void setClientCipherKeys(List<byte[]> clientCipherKeys) {
        this.clientCipherKeys = clientCipherKeys;
    }

    public List<byte[]> getServerCipherKeys2() {
        return serverCipherKeys2;
    }

    public void setServerCipherKeys2(List<byte[]> serverCipherKeys2) {
        this.serverCipherKeys2 = serverCipherKeys2;
    }

    public byte[] getServerPrivateKey() {
        return serverPrivateKey;
    }

    public void setServerPrivateKey(byte[] serverPrivateKey) {
        this.serverPrivateKey = serverPrivateKey;
    }
}
