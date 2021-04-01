package com.github.devil.srv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * create by Yao 2021/3/31
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {

    private List<T> data;

    private long total;

    private int page;

    private int pageSize;
}
