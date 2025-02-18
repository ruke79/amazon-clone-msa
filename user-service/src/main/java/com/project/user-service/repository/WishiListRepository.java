package com.project.user-service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.user-service.model.WishList;

@Repository
public interface WishiListRepository extends JpaRepository<WishList, Long> {


}

    

