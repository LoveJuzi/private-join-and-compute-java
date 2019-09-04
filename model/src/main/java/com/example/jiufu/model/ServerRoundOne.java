package com.example.jiufu.model;

import java.util.LinkedList;
import java.util.List;

public class ServerRoundOne {
    private String serverPrivateKey;
    private List<byte[]> serverCipherKeys = new LinkedList<>();

    public String getServerPrivateKey() {
        return serverPrivateKey;
    }

    public void setServerPrivateKey(String serverPrivateKey) {
        this.serverPrivateKey = serverPrivateKey;
    }

    public List<byte[]> getServerCipherKeys() {
        return serverCipherKeys;
    }

    public void setServerCipherKeys(List<byte[]> serverCipherKeys) {
        this.serverCipherKeys = serverCipherKeys;
    }
}
