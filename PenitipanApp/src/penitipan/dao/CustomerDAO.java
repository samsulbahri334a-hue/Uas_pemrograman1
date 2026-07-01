/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package penitipan.dao;

import penitipan.database.DatabaseConnection;
import penitipan.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
/**
 * Class CustomerDAO
 * Fungsi: Mengelola operasi database untuk tabel Customer (CRUD)
 */
public class CustomerDAO {
    
    /**
     * Method untuk menambah customer baru
     */
    public boolean tambahCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (nama_customer, alamat, no_hp, email, password, status_customer) "
                   + "VALUES (?, ?, ?, ?, ?, 'aktif')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getNamaCustomer());
            ps.setString(2, customer.getAlamat());
            ps.setString(3, customer.getNoHp());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getPassword());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        customer.setIdCustomer(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Method untuk mengambil semua customer
     */
    public List<Customer> getAllCustomer() {
        List<Customer> listCustomer = new ArrayList<>();
        String query = "SELECT * FROM customer ORDER BY id_customer DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("id_customer"),
                    rs.getString("nama_customer"),
                    rs.getString("alamat"),
                    rs.getString("no_hp"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("tanggal_daftar"),
                    rs.getString("status_customer")
                );
                listCustomer.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengambil data customer: " + e.getMessage());
            e.printStackTrace();
        }
        return listCustomer;
    }
    
    /**
     * Method untuk mencari customer berdasarkan ID
     */
    public Customer getCustomerById(int idCustomer) {
        String query = "SELECT * FROM customer WHERE id_customer = ?";
        Customer customer = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, idCustomer);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer(
                        rs.getInt("id_customer"),
                        rs.getString("nama_customer"),
                        rs.getString("alamat"),
                        rs.getString("no_hp"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("tanggal_daftar"),
                        rs.getString("status_customer")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saat mencari customer: " + e.getMessage());
            e.printStackTrace();
        }
        return customer;
    }
    
    /**
     * Method untuk update data customer
     */
    public boolean updateCustomer(Customer customer) {
        String query = "UPDATE customer SET nama_customer = ?, alamat = ?, no_hp = ?, " +
                      "email = ?, password = ?, status_customer = ? WHERE id_customer = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, customer.getNamaCustomer());
            pst.setString(2, customer.getAlamat());
            pst.setString(3, customer.getNoHp());
            pst.setString(4, customer.getEmail());
            pst.setString(5, customer.getPassword());
            pst.setString(6, customer.getStatusCustomer());
            pst.setInt(7, customer.getIdCustomer());
            
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error saat update customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Method untuk menghapus customer
     */
    public boolean deleteCustomer(int idCustomer) {
        String query = "DELETE FROM customer WHERE id_customer = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, idCustomer);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error saat menghapus customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Method untuk login customer (verifikasi email dan password)
     */
    public Customer login(String email, String password) {
        String query = "SELECT * FROM customer WHERE email = ? AND password = ?";
        Customer customer = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, email);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer(
                        rs.getInt("id_customer"),
                        rs.getString("nama_customer"),
                        rs.getString("alamat"),
                        rs.getString("no_hp"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("tanggal_daftar"),
                        rs.getString("status_customer")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saat login: " + e.getMessage());
            e.printStackTrace();
        }
        return customer;
    }
    
    /**
     * Method untuk mencari customer berdasarkan nama
     */
    public List<Customer> searchCustomerByNama(String nama) {
        List<Customer> listCustomer = new ArrayList<>();
        String query = "SELECT * FROM customer WHERE nama_customer LIKE ? ORDER BY id_customer DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, "%" + nama + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Customer customer = new Customer(
                        rs.getInt("id_customer"),
                        rs.getString("nama_customer"),
                        rs.getString("alamat"),
                        rs.getString("no_hp"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("tanggal_daftar"),
                        rs.getString("status_customer")
                    );
                    listCustomer.add(customer);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saat search customer: " + e.getMessage());
            e.printStackTrace();
        }
        return listCustomer;
    }
    
    public boolean register(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (nama_customer, alamat, no_hp, email, password, status_customer) "
                   + "VALUES (?, ?, ?, ?, ?, 'aktif')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getNamaCustomer());
            ps.setString(2, customer.getAlamat());
            ps.setString(3, customer.getNoHp());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getPassword());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        customer.setIdCustomer(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    // =====================================================
    // VALIDASI DUPLIKAT
    // =====================================================

    /**
     * Mengecek apakah email sudah terdaftar
     */
    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customer WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Mengecek apakah nomor HP sudah terdaftar
     */
    public boolean isNoHpExists(String noHp) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customer WHERE no_hp = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noHp);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // =====================================================
    // HELPER
    // =====================================================

    private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("id_customer"),
            rs.getString("nama_customer"),
            rs.getString("alamat"),
            rs.getString("no_hp"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("tanggal_daftar"),
            rs.getString("status_customer")
        );
    }
}