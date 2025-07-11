package com.minis.test.entity;


import lombok.Data;

import java.util.Date;

@Data
public class User {
    int id = 1;
    String name = "";
    Date birthday = new Date();
}
