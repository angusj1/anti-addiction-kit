package com.taptap.tds.registration.server.dto.rpc;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class TdsIdentificationRequest {

    @Size(max = 32)
    @NotBlank
    private String userId;
    public String getUserId() {
        return this.userId;
    }

    @NotBlank
    private String name;
    public String getName() {
        return this.name;
    }

    @NotBlank
    @Size(min = 18, max = 18)
    private String idCard;
    public String getIdCard() {
        return this.idCard;
    }


}