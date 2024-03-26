package test.mapperTest;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from account where username = #{username}")
    UserDetail selectUserByName(String username);
}
