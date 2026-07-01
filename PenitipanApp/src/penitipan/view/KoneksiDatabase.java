package penitipan.view;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Class untuk mengelola koneksi ke database db_penitipan_pindahan.
 * Menggunakan MySQL Connector/J.
 */
public class KoneksiDatabase {

    private static final String URL = "jdbc:mysql://localhost:3306/db_penitipan_pindahan";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // sesuaikan dengan password MySQL Anda

    private static Connection connection;

    /**
     * Mengambil koneksi aktif ke database.
     * Jika belum ada koneksi atau koneksi sudah tertutup, akan dibuat koneksi baru.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "Driver MySQL Connector/J tidak ditemukan.\n"
                    + "Pastikan library MySQL Connector/J sudah ditambahkan ke project.",
                    "Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal terhubung ke database:\n" + e.getMessage(),
                    "Koneksi Error", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

    /**
     * Menutup koneksi database jika sedang terbuka.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal menutup koneksi:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
