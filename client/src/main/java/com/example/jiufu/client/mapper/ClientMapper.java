package com.example.jiufu.client.mapper;

import com.example.jiufu.client.model.Client;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClientMapper {

    @Select("select * from client")
    public List<Client> getAllClient();
}
