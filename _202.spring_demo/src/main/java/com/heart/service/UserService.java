package com.heart.service;

import com.heart.annotation.DataSourceSwitcher;
import com.heart.datasource.DataSourceType;
import com.heart.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllFromPrimary();

    List<User> getAllFromSecondary();

    List<User> getUserByIdFromPrimary(List<Long> ids);

    List<User> getUserByIdFromSecondary(List<Long> ids);
}
