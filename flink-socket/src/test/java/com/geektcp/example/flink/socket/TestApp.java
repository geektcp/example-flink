package com.geektcp.example.flink.socket;

import com.geektcp.common.core.system.Sys;

/**
 * @author Administrator on 2023/3/28 14:11.
 */
public class TestApp {

    public static void main(String[] args) {
        Sys.p(Sys.getEnv("user.name"));
        Sys.p(Sys.getUserName());
    }

}
