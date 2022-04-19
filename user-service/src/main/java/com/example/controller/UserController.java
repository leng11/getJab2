package com.example.controller;

import com.example.dao.UserDao;
import com.example.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/vaccine/users/")
public class UserController {
    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }


    @PostMapping("/register")
    public User registerPerson(@RequestBody User user) {
        return userDao.save(user);
    }


    @GetMapping("/data")
    public User findById(@RequestParam(name = "officialId") int officialId) {
        return userDao.findByOfficialId(officialId);
    }


    @GetMapping("/list")
    public List<User> findAll() {
        return userDao.findAll();
    }

}