package com.example;

import com.example.dao.CertificateDao;
import com.example.dao.UserDao;
import com.example.entity.Certificate;
import com.example.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
class UserServiceApplicationTests {


    @Autowired
    UserDao userdao;

    @Autowired
    CertificateDao certificatedao;

    @Test
    void contextLoads() {

    }


    @BeforeAll
    static void beforeAll() {
        log.trace("Test Cases called");
    }

    @Test
    public void getAllRecords_success()throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Sample");
        user.setAddress("Sample");
        user.setOfficialId(123);
        userdao.save(user);
        assertNotNull(userdao.findAll());



    }

    @Test
    public void getRecordbyofficialId(){

        //assertNotNull(userdao.findById(123));
    }

    @Test
    public void RegisterCertificate() throws ParseException {
        Certificate certificate = new Certificate();
        certificate.setId(1);
        certificate.setName("Subhomoy");

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String dateInString = "7-Jun-2013";
        Date date = formatter.parse(dateInString);
        certificate.setDate(date);

        certificate.setLocation("Durgapur");
        certificate.setLot(4);
        certificate.setVaccine_id(223);
        certificate.setOfficialId(123);
        certificatedao.save(certificate);
        assertNotNull(certificatedao.findAll());
    }

    @Test
    public void getCertificatebyofficalid(){
        assertNotNull(certificatedao.getCertificateByOfficialId(123));

    }



}
