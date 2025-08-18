package com.heart.service.impl;

import com.heart.annotation.DataSourceSwitcher;
import com.heart.datasource.DataSourceType;
import com.heart.mapper.UserRepository;
import com.heart.entity.User;
import com.heart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    // @Resource
    private UserRepository userRepository;

    @Override
    public List<User> getAllFromPrimary() {
        return userRepository.findAll();
    }

    @DataSourceSwitcher(DataSourceType.SECONDARY)
    @Override
    public List<User> getAllFromSecondary() {
        return userRepository.findAll();
    }

    @DataSourceSwitcher(DataSourceType.PRIMARY)
    @Override
    public List<User> getUserByIdFromPrimary(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @DataSourceSwitcher(DataSourceType.SECONDARY)
    @Override
    public List<User> getUserByIdFromSecondary(List<Long> ids) {
        return userRepository.findAllById(ids);
    }
}
