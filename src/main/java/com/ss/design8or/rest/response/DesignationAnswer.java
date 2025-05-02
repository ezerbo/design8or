package com.ss.design8or.rest.response;

import lombok.Getter;

@Getter
public enum DesignationAnswer {

    ACCEPT("accept"),

    DECLINE("decline");

    private final String answer;

    DesignationAnswer(String answer) {
        this.answer = answer;
    }

}
