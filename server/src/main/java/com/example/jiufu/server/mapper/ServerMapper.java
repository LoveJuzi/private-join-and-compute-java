package com.example.jiufu.server.mapper;

import com.example.jiufu.server.modle.Server;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ServerMapper {

    @Select("select * from server")
    public List<Server> getAllServer();
}
