package com.himedias.varletserver.dao;

import com.himedias.varletserver.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {

    @Query("SELECT h FROM House h WHERE " +
            "LOWER(h.hname) LIKE LOWER(CONCAT('%', :query, '%')) ")
    List<House> searchByMultipleFields(String query);
}
