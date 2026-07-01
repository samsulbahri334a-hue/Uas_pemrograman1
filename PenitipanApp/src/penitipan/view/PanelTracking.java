package penitipan.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * JPanel untuk menampilkan riwayat tracking perjalanan (tabel `tracking`),
 * yang terhubung ke tabel `pindahan`.
 * Panel ini bersifat read-only: tidak ada tombol Tambah, Edit, maupun Hapus,
 * karena riwayat tracking idealnya dibuat otomatis oleh proses pindahan,
 * bukan diisi manual.
 */
public class PanelTracking extends JPanel {

    private JTable tableTracking;
    private DefaultTableModel modelTable;
    private JButton btnRefresh;

    private final String[] kolom = {
        "ID Tracking", "ID Pindahan", "Customer", "Lokasi", "Waktu Update", "Status"
    };

    public PanelTracking() {
        initComponents();
        muatData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblJudul = new JLabel("Tracking / Riwayat Pindahan", SwingConstants.LEFT);
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(lblJudul, BorderLayout.WEST);

        btnRefresh = new JButton("Refresh");
        JPanel panelKanan = new JPanel();
        panelKanan.add(btnRefresh);
        panelAtas.add(panelKanan, BorderLayout.EAST);

        add(panelAtas, BorderLayout.NORTH);

        modelTable = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTracking = new JTable(modelTable);
        tableTracking.setRowHeight(24);
        tableTracking.getTableHeader().setReorderingAllowed(false);
        tableTracking.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tableTracking);
        add(scrollPane, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> muatData());
    }

    private void muatData() {
        modelTable.setRowCount(0);

        String sql = "SELECT tr.id_tracking, tr.id_pindahan, c.nama_customer, "
                + "tr.lokasi, tr.waktu_update, tr.status "
                + "FROM tracking tr "
                + "JOIN pindahan p ON tr.id_pindahan = p.id_pindahan "
                + "JOIN customer c ON p.id_customer = c.id_customer "
                + "ORDER BY tr.waktu_update DESC";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] baris = {
                    rs.getInt("id_tracking"),
                    rs.getInt("id_pindahan"),
                    rs.getString("nama_customer"),
                    rs.getString("lokasi"),
                    rs.getTimestamp("waktu_update"),
                    rs.getString("status")
                };
                modelTable.addRow(baris);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data tracking:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
