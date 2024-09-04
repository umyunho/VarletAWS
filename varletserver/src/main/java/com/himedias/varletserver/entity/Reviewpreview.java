package com.himedias.varletserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@Entity
@NoArgsConstructor
public class Reviewpreview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rseq;

    private String userid;
    private int readcount;
    private String title;
    private String content;
    private Timestamp indate;
    private String ipath;
    private String profileimg;
}