package com.project.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.userservice.model.WishList;

@Repository
public interface WishiListRepository extends JpaRepository<WishList, Long> {


}

    

