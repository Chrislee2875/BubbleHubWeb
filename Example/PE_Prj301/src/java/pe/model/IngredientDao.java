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
import java.util.ArrayList;
import java.util.List;
import pe.utils.DbUtils;

/**
 *
 * @author Computing Fundamental - HCM Campus
 */
public class IngredientDao {

    public List<IngredientDto> search(String keyword) throws ClassNotFoundException, SQLException {
        List<IngredientDto> result = new ArrayList<>();
        String searchValue = keyword == null ? "" : keyword.trim();
        String sql = "SELECT IngredientID, IngredientName, Quantity, Unit "
                + "FROM Ingredients "
                + "WHERE CAST(IngredientID AS VARCHAR(20)) LIKE ? "
                + "OR IngredientName LIKE ? "
                + "OR Unit LIKE ? "
                + "ORDER BY IngredientID";
        try (Connection conn = DbUtils.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)) {
            String likeValue = "%" + searchValue + "%";
            stm.setString(1, likeValue);
            stm.setString(2, likeValue);
            stm.setString(3, likeValue);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    result.add(new IngredientDto(
                            rs.getInt("IngredientID"),
                            rs.getString("IngredientName"),
                            rs.getDouble("Quantity"),
                            rs.getString("Unit")
                    ));
                }
            }
        }
        return result;
    }

    public IngredientDto getById(int ingredientID) throws ClassNotFoundException, SQLException {
        IngredientDto ingredient = null;
        String sql = "SELECT IngredientID, IngredientName, Quantity, Unit "
                + "FROM Ingredients "
                + "WHERE IngredientID = ?";
        try (Connection conn = DbUtils.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, ingredientID);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    ingredient = new IngredientDto(
                            rs.getInt("IngredientID"),
                            rs.getString("IngredientName"),
                            rs.getDouble("Quantity"),
                            rs.getString("Unit")
                    );
                }
            }
        }
        return ingredient;
    }

    public boolean update(IngredientDto ingredient) throws ClassNotFoundException, SQLException {
        boolean check = false;
        String sql = "UPDATE Ingredients "
                + "SET IngredientName = ?, Quantity = ?, Unit = ? "
                + "WHERE IngredientID = ?";
        try (Connection conn = DbUtils.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, ingredient.getIngredientName());
            stm.setDouble(2, ingredient.getQuantity());
            stm.setString(3, ingredient.getUnit());
            stm.setInt(4, ingredient.getIngredientID());
            check = stm.executeUpdate() > 0;
        }
        return check;
    }

    public boolean create(IngredientDto ingredient) throws ClassNotFoundException, SQLException {
        boolean check = false;
        String sql = "INSERT INTO Ingredients (IngredientID, IngredientName, Quantity, Unit) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DbUtils.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, ingredient.getIngredientID());
            stm.setString(2, ingredient.getIngredientName());
            stm.setDouble(3, ingredient.getQuantity());
            stm.setString(4, ingredient.getUnit());
            check = stm.executeUpdate() > 0;
        }
        return check;
    }

    public boolean delete(int ingredientID) throws ClassNotFoundException, SQLException {
        boolean check = false;
        String sql = "DELETE FROM Ingredients WHERE IngredientID = ?";
        try (Connection conn = DbUtils.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, ingredientID);
            check = stm.executeUpdate() > 0;
        }
        return check;
    }

    public int generateNextId() throws ClassNotFoundException, SQLException {
        int nextID = 1;
        String sql = "SELECT ISNULL(MAX(IngredientID), 0) + 1 AS NextID FROM Ingredients";
        try (Connection conn = DbUtils.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql);
                ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                nextID = rs.getInt("NextID");
            }
        }
        return nextID;
    }
}
