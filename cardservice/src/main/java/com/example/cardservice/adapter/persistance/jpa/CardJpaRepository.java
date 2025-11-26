package com.example.cardservice.adapter.persistance.jpa;

import com.example.cardservice.domain.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CardJpaRepository extends JpaRepository<CardEntity, Long> {


}
