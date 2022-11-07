package com.tencent.bk.devops.atom.task.pojo;

import lombok.Data;

@Data
public class IdParam {

    private String key;
    private Object value;
    private boolean enable = true;
}
