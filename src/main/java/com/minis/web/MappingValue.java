package com.minis.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MappingValue {
    private String uri;
    private String clz;
    private String method;
}
