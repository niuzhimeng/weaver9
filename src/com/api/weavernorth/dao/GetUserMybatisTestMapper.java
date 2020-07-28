package com.api.weavernorth.dao;

import com.api.weavernorth.entity.HrmVOTest;

public interface GetUserMybatisTestMapper {

    HrmVOTest getUserById(String loginId);
}
