package com.example.jiufu.grpc_message_def.client;

import com.example.jiufu.grpc_message_def.ClientMessage;
import com.example.jiufu.grpc_message_def.ECCommutativeCipherRpcGrpc;
import com.google.protobuf.ByteString;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class ECCommutativeCipherRpcClient {

    @GrpcClient("commutative-cipher-server")
    private ECCommutativeCipherRpcGrpc.ECCommutativeCipherRpcBlockingStub ecCommutativeCipherRpcBlockingStub;

    public ByteString GetPrivateKey() {
        return this.ecCommutativeCipherRpcBlockingStub.handle(ClientMessage.newBuilder().build()).getPk();
    }

    public ByteString Encrypt(ByteString pk, ByteString plainText) {
        return this.ecCommutativeCipherRpcBlockingStub.handle(ClientMessage.newBuilder().setPk(pk).setPlaintext(plainText).build()).getCiphertext();
    }

    public ByteString ReEncrypt(ByteString pk, ByteString cipherText) {
        return this.ecCommutativeCipherRpcBlockingStub.handle(ClientMessage.newBuilder().setPk(pk).setCiphertext(cipherText).build())
                .getCiphertext2();
    }

    public ByteString Decrypt(ByteString pk, ByteString cipherText) {
        return this.ecCommutativeCipherRpcBlockingStub.handle(ClientMessage.newBuilder().setPk(pk).setDeciphertext(cipherText).build())
                .getDeciphertext();
    }
}
