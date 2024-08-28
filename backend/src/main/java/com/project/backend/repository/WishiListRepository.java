package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.WishList;

@Repository
public interface WishiListRepository extends JpaRepository<WishList, Long> {


}

    

