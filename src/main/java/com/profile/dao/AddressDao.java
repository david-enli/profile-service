package com.profile.dao;

import com.profile.model.DAOAddress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AddressDao extends CrudRepository<DAOAddress, Integer> {

    List<DAOAddress> findByUsername(String username);

}