package com.example.controller;

import com.example.dao.CertificateDao;
import com.example.entity.Certificate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/vaccine/users/")
public class CertificateController {

    private final CertificateDao certificateDao;


    public CertificateController(CertificateDao certificateDao) {
        this.certificateDao = certificateDao;
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
