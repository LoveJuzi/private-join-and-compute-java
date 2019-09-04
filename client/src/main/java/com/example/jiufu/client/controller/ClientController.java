package com.example.jiufu.client.controller;

import com.example.jiufu.client.mapper.ClientMapper;
import com.example.jiufu.client.model.Client;
import com.example.jiufu.grpc_message_def.client.ECCommutativeCipherRpcClient;
import com.example.jiufu.model.ClientRoundOne;
import com.example.jiufu.model.ServerRoundOne;
import com.example.jiufu.model.ServerRoundTwo;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@ComponentScan("com.example.jiufu.grpc_message_def")
public class ClientController {

    public ClientController() {
        clientKeys = new LinkedList<>();

        clientKeys.add("李四");
        clientKeys.add("张三");
        clientKeys.add("上帝");
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ECCommutativeCipherRpcClient ecCommutativeCipherRpcClient;

    @Autowired
    private ClientMapper clientMapper;

    private List<String> clientKeys;

    @GetMapping("/blacklist")
    public List<String> blacklist() {
        List<Client> clientList = clientMapper.getAllClient();

        RestTemplate restTemplate = new RestTemplate();

        ServerRoundOne serverRoundOne = restTemplate.getForObject("http://127.0.0.1:8080/start", ServerRoundOne.class);

        ByteString pk = ecCommutativeCipherRpcClient.GetPrivateKey();
        List<byte[]> clientCipherKeys = new LinkedList<>();
        List<byte[]> serverCipherKeys2 = new LinkedList<>();
        Map<ByteString, String> mapClientKeys = new HashMap<>();

        for (Client client : clientList) {
            ByteString byteClientCipherKeys = ecCommutativeCipherRpcClient.Encrypt(pk, ByteString.copyFromUtf8(client.getKey()));
            clientCipherKeys.add(byteClientCipherKeys.toByteArray());
            mapClientKeys.put(byteClientCipherKeys, client.getKey());
        }

        for (byte[] cipher : serverRoundOne.getServerCipherKeys()) {
            serverCipherKeys2.add(ecCommutativeCipherRpcClient.ReEncrypt(pk, ByteString.copyFrom(cipher)).toByteArray());
        }

        ClientRoundOne clientRoundOne = new ClientRoundOne();
        clientRoundOne.setServerPrivateKey(serverRoundOne.getServerPrivateKey());
        clientRoundOne.setClientCipherKeys(clientCipherKeys);
        clientRoundOne.setServerCipherKeys2(serverCipherKeys2);

        ServerRoundTwo serverRoundTwo = restTemplate.postForObject("http://127.0.0.1:8080/calculate", clientRoundOne, ServerRoundTwo.class);

        List<String> blackist = new LinkedList<>();
        for (byte[] cipherKeys : serverRoundTwo.getClientCipherKeys()) {
            blackist.add(mapClientKeys.get(ByteString.copyFrom(cipherKeys)));
        }

        return blackist;
    }

    @GetMapping("/blackvalue")
    public Integer blackvalue() {
        return 0;
    }
}
