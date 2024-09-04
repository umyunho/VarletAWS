package com.himedias.varletserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "reviewimg")
@NoArgsConstructor
public class Reviewimg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iseq;

    @Column(nullable = false)
    private int rseq;

    private String ipath;

    private String iname;

    @ManyToOne
    @JoinColumn(name = "rseq", insertable = false, updatable = false)
    private Review review;
}
