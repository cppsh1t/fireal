package test.mapperTest;

import org.apache.ibatis.annotations.Select;

public interface UserMapper {

    @Select("select * from account where username = #{username}")
    UserDetail selectUserByName(String username);
}
