package com.tsn.serv.mem.mapper.mem;

import java.util.List;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.mem.NewUser;

public interface NewUserMapper {
	int delete(Integer id);

	int insert(NewUser newUser);

	int insertDynamic(NewUser newUser);

	int updateDynamic(NewUser newUser);

	int update(NewUser newUser);

	NewUser selectById(Integer id);

	NewUser selectNewUserByOldAccount(String oldAccount);

	NewUser selectNewUserByNewAccount(String newAccount);

    List<NewUser> newUserList(Page page);
}