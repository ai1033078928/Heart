package com.heart.service;

import com.heart.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllFromPrimary();

    List<User> getAllFromSecondary();

    List<User> getUserByIdFromPrimary(List<Long> ids);

    List<User> getUserByIdFromSecondary(List<Long> ids);
}
