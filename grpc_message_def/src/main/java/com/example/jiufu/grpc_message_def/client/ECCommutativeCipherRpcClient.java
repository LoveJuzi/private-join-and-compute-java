package com.example.jiufu.grpc_message_def.client;

import com.example.jiufu.grpc_message_def.*;
import com.google.protobuf.ByteString;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

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

    public boolean GeneratePQKey(int modulus_size, List<ByteString> refp, List<ByteString> refq) {
        ServerMessage serverMessage = this.ecCommutativeCipherRpcBlockingStub.handle(
                ClientMessage.newBuilder().setPaillierGeneratePqkey(
                        paillier_generate_pqkey_climsg.newBuilder().setModulusSize(modulus_size).build()
                ).build());

        refp.add(serverMessage.getPaillierGeneratePqkey().getP());
        refq.add(serverMessage.getPaillierGeneratePqkey().getQ());
        return true;
    }

    public boolean GeneratePubkey(ByteString p, ByteString q, List<ByteString> refpubkey) {
        ServerMessage serverMessage = this.ecCommutativeCipherRpcBlockingStub.handle(
                ClientMessage.newBuilder().setPaillierGeneratePubkey(
                        paillier_generate_pubkey_climsg.newBuilder()
                                .setP(p)
                                .setQ(q).build()
                ).build()

        );
        refpubkey.add(serverMessage.getPaillierGeneratePubkey().getPubkey());
        return true;
    }

    public boolean EncryptList(ByteString p, ByteString q, List<Long> datas, List<byte[]> cipherDatas) {
        ServerMessage serverMessage = this.ecCommutativeCipherRpcBlockingStub.handle(
                ClientMessage.newBuilder().setPaillierEnrcpytList(
                        paillier_encrypt_list_climsg.newBuilder()
                                .setP(p)
                                .setQ(q)
                                .addAllDatas(datas).build()
                ).build()
        );
        for (ByteString byteDatas : serverMessage.getPaillierEnrcpytList().getCipherDatasList()) {
            cipherDatas.add(byteDatas.toByteArray());
        }
        return true;
    }

    public boolean AddList(byte[] pubkey, List<byte[]> cipherDatas, List<byte[]> cipherSum) {
        List<ByteString> cipherByteDatas = new LinkedList<>();
        for (byte[] elem : cipherDatas) {
            cipherByteDatas.add(ByteString.copyFrom(elem));
        }
        ServerMessage serverMessage = this.ecCommutativeCipherRpcBlockingStub.handle(
                ClientMessage.newBuilder().setPaillierAddList(
                        paillier_add_list_climsg.newBuilder()
                                .setPubkey(ByteString.copyFrom(pubkey))
                                .addAllCipherDatas(cipherByteDatas).build()
                ).build()
        );

        cipherSum.add(serverMessage.getPaillierAddList().getCipherSum().toByteArray());
        return true;
    }

    public boolean DecrpytData(ByteString p, ByteString q, byte[] cipherData, List<Long> refData) {
        ServerMessage serverMessage = this.ecCommutativeCipherRpcBlockingStub.handle(
                ClientMessage.newBuilder().setPaillierDecrpyt(
                        paillier_decrpyt_climsg.newBuilder()
                                .setP(p)
                                .setQ(q)
                                .setCipherData(ByteString.copyFrom(cipherData)).build()
                ).build()
        );
        refData.add(serverMessage.getPaillierDecrpyt().getData());
        return true;
    }
}
