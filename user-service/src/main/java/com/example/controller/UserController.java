package com.example.controller;

import com.example.dao.CertificateDao;
import com.example.dao.UserDao;
import com.example.entity.Certificate;
import com.example.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/vaccine/users/")
public class UserController {
    private final UserDao userDao;
    private final CertificateDao certificateDao;


    public UserController(UserDao userDao, CertificateDao certificateDao) {
        this.userDao = userDao;
        this.certificateDao = certificateDao;
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
    @GetMapping("/retrieveCertificate")
    public Certificate getCertificateByUser(@RequestParam(value = "officialId") final int officialId){
        return certificateDao.getCertificateByOfficialId(officialId);
    }

    @PostMapping("/add")
    public Certificate saveCertificate(@RequestBody Certificate certificate){
        return certificateDao.save(certificate);
    }


}