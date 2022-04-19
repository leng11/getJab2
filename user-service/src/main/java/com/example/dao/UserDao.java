package  com.example.dao;

import com.example.entity.User;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface UserDao extends CrudRepository<User,Long> {
    User findByOfficialId(int id);

    List<User> findAll();

}