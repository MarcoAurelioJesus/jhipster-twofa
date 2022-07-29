package com.mycompany.myapp.service.dto;

public class JwtAuthenticationResponse {

    public JwtAuthenticationResponse(String string, boolean b, AdminUserDTO userDTO) {}

    private String accessToken;
    private boolean authenticated;
    private UserInfo user;
}
