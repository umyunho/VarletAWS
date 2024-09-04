package com.himedias.varletserver.dao;


import com.himedias.varletserver.entity.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Integer> {

    Page<Qna> findAllByUserid(Pageable pageable, String userid);

}
