/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import pe.utils.DbUtils;

/**
 *
 * @author Computing Fundamental - HCM Campus
 */
public class UserDao {

    public UserDto checkLogin(String username, String password) throws ClassNotFoundException, SQLException {
        UserDto user = null;
        String sql = "SELECT username, name, password "
                + "FROM users "
                + "WHERE username = ? AND password = ?";
        try (Connection conn = DbUtils.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, username);
            stm.setString(2, password);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    user = new UserDto(
                            rs.getString("username"),
                            rs.getString("name"),
                            rs.getString("password")
                    );
                }
            }
        }
        return user;
    }
}
