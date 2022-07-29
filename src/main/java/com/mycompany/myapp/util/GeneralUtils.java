package com.mycompany.myapp.util;

import com.mycompany.myapp.service.dto.AdminUserDTO;
import com.mycompany.myapp.service.dto.UserInfo;
import java.util.Set;

/**
 *
 * @author
 *
 */
public class GeneralUtils {

    public static UserInfo buildUserInfo(AdminUserDTO userDTO) {
        Set<String> roles = userDTO.getAuthorities();
        String user = userDTO.getLogin().toString();
        return new UserInfo(user.toString(), roles, user, null);
    }

    public static Object buildUserInfo(String login) {
        return null;
    }
}
