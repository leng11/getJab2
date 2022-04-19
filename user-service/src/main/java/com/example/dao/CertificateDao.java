package com.example.dao;

import com.example.entity.Certificate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateDao extends CrudRepository<Certificate,Integer> {
    Certificate getCertificateByOfficialId(int id);
}
