package com.mycompany.myapp.service.dto;

import java.util.List;
import java.util.Set;

public class UserInfo {

    public UserInfo(String string, String login, String email2, List<String> roles2) {}

    public UserInfo(String string, List<String> roles2) {}

    public UserInfo(String string, Set<String> roles2, String user, Object roles22) {}

    private String id, displayName, email;
    private List<String> roles;
}
