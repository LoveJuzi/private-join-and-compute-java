package com.example.jiufu.server.Controller;

import com.example.jiufu.grpc_message_def.client.ECCommutativeCipherRpcClient;
import com.example.jiufu.model.ClientRoundOne;
import com.example.jiufu.model.ServerRoundOne;
import com.example.jiufu.model.ServerRoundTwo;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@ComponentScan("com.example.jiufu.grpc_message_def")
public class ServerController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public ServerController() {
        keys_ = new LinkedList<>();
        keys_.add("张三");
        keys_.add("李四");
        keys_.add("王五");
    }

    @Autowired
    private ECCommutativeCipherRpcClient ecCommutativeCipherRpcClient;

    // 这个i变量应该是放到
    private ByteString pk_;

    private List<String> keys_;

    @GetMapping("/start")
    public ServerRoundOne start() {
        // Read data from database

        ServerRoundOne serverRoundOne = new ServerRoundOne();

        this.pk_ = ecCommutativeCipherRpcClient.GetPrivateKey();

        serverRoundOne.setServerPrivateKey(this.pk_.toByteArray());

        List<byte[]> cipherKeys = new LinkedList<>();

        for (String key: this.keys_) {
            ByteString cipher = ecCommutativeCipherRpcClient.Encrypt(this.pk_, ByteString.copyFromUtf8(key));
            cipherKeys.add(cipher.toByteArray());
        }

        serverRoundOne.setServerCipherKeys(cipherKeys);

        return serverRoundOne;
    }

    @PostMapping("/calculate")
    public ServerRoundTwo calculate(@RequestBody ClientRoundOne clientRoundOne) {
        logger.info(clientRoundOne.getClientCipherKeys().toString());

        List<ByteString> serverCipherKeys2 = new LinkedList<>();
        List<ByteString> clientCipherKeys2 = new LinkedList<>();
        Map<ByteString, byte[]> mapClientKeys = new HashMap<>();

        for (byte[] serverCipherKey2 : clientRoundOne.getServerCipherKeys2()) {
            serverCipherKeys2.add(ByteString.copyFrom(serverCipherKey2));
        }

        for(byte[] cipherKeys : clientRoundOne.getClientCipherKeys()) {
            ByteString byteCipherKeys2 = ecCommutativeCipherRpcClient.ReEncrypt(ByteString.copyFrom(clientRoundOne.getServerPrivateKey()),
                    ByteString.copyFrom(cipherKeys));
            clientCipherKeys2.add(byteCipherKeys2);
            mapClientKeys.put(byteCipherKeys2, cipherKeys);
        }

        clientCipherKeys2.retainAll(serverCipherKeys2);

        List<byte[]> clientCipherKeys = new LinkedList<>();
        for (ByteString cipherKey2 : clientCipherKeys2) {
            clientCipherKeys.add(mapClientKeys.get(cipherKey2));
        }

        ServerRoundTwo serverRoundTwo = new ServerRoundTwo();

        serverRoundTwo.setClientCipherKeys(clientCipherKeys);

        return serverRoundTwo;
    }
}
