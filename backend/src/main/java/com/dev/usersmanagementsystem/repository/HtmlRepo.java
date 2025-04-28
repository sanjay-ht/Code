package com.dev.usersmanagementsystem.repository;

import com.dev.usersmanagementsystem.entity.Html;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HtmlRepo extends JpaRepository<Html, Integer> {

    Html findByName(String name);

}
