package com.example.jiufu.model;

import java.util.List;

public class ClientRoundOne2 {
    private List<byte[]> clientCipherKeys;
    private List<byte[]> serverCipherKeys2;
    private String serverPrivateKey;
    private byte[] clientPublicKey;
    private List<byte[]> clientCipherValues;

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

    public String getServerPrivateKey() {
        return serverPrivateKey;
    }

    public void setServerPrivateKey(String serverPrivateKey) {
        this.serverPrivateKey = serverPrivateKey;
    }

    public byte[] getClientPublicKey() {
        return clientPublicKey;
    }

    public void setClientPublicKey(byte[] clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public List<byte[]> getClientCipherValues() {
        return clientCipherValues;
    }

    public void setClientCipherValues(List<byte[]> clientCipherValues) {
        this.clientCipherValues = clientCipherValues;
    }
}
