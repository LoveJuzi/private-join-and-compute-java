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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private List<String> keys_;

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Map<String, Object> runtimeExceptionHandler(RuntimeException runtimeException) {
        logger.error(runtimeException.getLocalizedMessage());

        Map<String, Object> model = new TreeMap<>();
        model.put("status", false);
        return model;
    }

    @GetMapping("/start")
    public ServerRoundOne start() {
        // Read data from database

        ServerRoundOne serverRoundOne = new ServerRoundOne();

        ByteString pk = ecCommutativeCipherRpcClient.GetPrivateKey();

        List<byte[]> cipherKeys = new LinkedList<>();

        for (String key : this.keys_) {
            ByteString cipher = ecCommutativeCipherRpcClient.Encrypt(pk, ByteString.copyFromUtf8(key));
            cipherKeys.add(cipher.toByteArray());
        }

        serverRoundOne.setServerCipherKeys(cipherKeys);
        serverRoundOne.setServerPrivateKey(UUID.randomUUID().toString());

        // 将服务器的private key 存储到缓存中
        redisTemplate.opsForValue().set(serverRoundOne.getServerPrivateKey(), pk.toByteArray());
        redisTemplate.expire(serverRoundOne.getServerPrivateKey(),30, TimeUnit.MINUTES);

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

        byte[] pkBytes = (byte[]) redisTemplate.opsForValue().get(clientRoundOne.getServerPrivateKey());
        if (pkBytes == null) {
            return null;
        }
        ByteString pk = ByteString.copyFrom(pkBytes);
        for (byte[] cipherKeys : clientRoundOne.getClientCipherKeys()) {
            ByteString byteCipherKeys2 = ecCommutativeCipherRpcClient.ReEncrypt(
                    pk,
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

        redisTemplate.delete(clientRoundOne.getServerPrivateKey());

        return serverRoundTwo;
    }
}
