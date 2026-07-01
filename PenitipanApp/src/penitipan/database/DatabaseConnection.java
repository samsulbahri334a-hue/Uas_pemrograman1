/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package penitipan.database;

/**
 *
 * @author samsu
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class DatabaseConnection {
    
    // =====================================================
    // KONFIGURASI DATABASE — Sesuaikan di sini
    // =====================================================
    private static final String DB_HOST     = "localhost";
    private static final String DB_PORT     = "3306";
    private static final String DB_NAME     = "db_penitipan_pindahan";
    private static final String DB_USER     = "root";       // ganti dengan username MySQL Anda
    private static final String DB_PASSWORD = "";           // ganti dengan password MySQL Anda

    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";

    private static Connection connection = null;

    /**
     * Mendapatkan koneksi ke database (singleton pattern)
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("✅ Koneksi database berhasil!");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL tidak ditemukan. Pastikan mysql-connector-java sudah ditambahkan ke project.\n" + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Menutup koneksi database
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Koneksi database ditutup.");
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }
}