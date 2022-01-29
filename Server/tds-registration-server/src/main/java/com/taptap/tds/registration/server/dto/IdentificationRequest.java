package com.taptap.tds.registration.server.dto;

import lombok.Data;

@Data
public class IdentificationRequest {

    private String ai;
    public String getAi() {
        return this.ai;
    }
    public void setAi(String data) {
        this.ai = data;
    }

    private String name;
    public String getName() {
        return this.name;
    }
    public void setName(String data) {
        this.name = data;
    }

    private String idNum;
    public String getIdNum() {
        return this.idNum;
    }
    public void setIdNum(String data) {
        this.idNum = data;
    }
}
