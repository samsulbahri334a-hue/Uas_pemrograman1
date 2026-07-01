package penitipan.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * JPanel untuk menampilkan, menambah, dan menghapus data pada tabel `transaksi`.
 */
public class PanelTransaksi extends JPanel {

    private JTable tableTransaksi;
    private DefaultTableModel modelTable;
    private JButton btnRefresh;
    private JButton btnEdit;
    private JButton btnHapus;

    private final String[] kolom = {
        "ID Transaksi", "Nama Customer", "Nama Barang", "Jenis Layanan",
        "Tanggal Pemesanan", "Total Biaya", "Status Pembayaran",
        "Metode Pembayaran", "Tanggal Pembayaran", "Catatan"
    };

    public PanelTransaksi() {
        initComponents();
        muatData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblJudul = new JLabel("Data Transaksi", SwingConstants.LEFT);
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(lblJudul, BorderLayout.WEST);

        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        JPanel panelKanan = new JPanel();
        panelKanan.add(btnEdit);
        panelKanan.add(btnHapus);
        panelKanan.add(btnRefresh);
        panelAtas.add(panelKanan, BorderLayout.EAST);

        add(panelAtas, BorderLayout.NORTH);

        modelTable = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTransaksi = new JTable(modelTable);
        tableTransaksi.setRowHeight(24);
        tableTransaksi.getTableHeader().setReorderingAllowed(false);
        tableTransaksi.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tableTransaksi);
        add(scrollPane, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> muatData());
        btnEdit.addActionListener(e -> bukaDialogEdit());
        btnHapus.addActionListener(e -> hapusData());
    }

    private void muatData() {
        modelTable.setRowCount(0);

        String sql = "SELECT t.id_transaksi, c.nama_customer, b.nama_barang, t.jenis_layanan, "
                + "t.tanggal_booking, t.total_biaya, t.status_pembayaran, "
                + "t.metode_pembayaran, t.tanggal_pembayaran, t.catatan "
                + "FROM transaksi t "
                + "JOIN customer c ON t.id_customer = c.id_customer "
                + "LEFT JOIN barang b ON t.id_barang = b.id_barang "
                + "ORDER BY t.id_transaksi ASC";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] baris = {
                    rs.getInt("id_transaksi"),
                    rs.getString("nama_customer"),
                    rs.getString("nama_barang"),
                    rs.getString("jenis_layanan"),
                    rs.getTimestamp("tanggal_booking"),
                    rs.getBigDecimal("total_biaya"),
                    statusTampilan(rs.getString("status_pembayaran")),
                    rs.getString("metode_pembayaran"),
                    rs.getTimestamp("tanggal_pembayaran"),
                    rs.getString("catatan")
                };
                modelTable.addRow(baris);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data transaksi:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mengubah nilai status_pembayaran di database ("sudah_dibayar",
     * "belum_dibayar", "dibatalkan") menjadi label tampilan ("Lunas" / "Belum Bayar").
     */
    private String statusTampilan(String statusDb) {
        if ("sudah_dibayar".equals(statusDb)) {
            return "Lunas";
        }
        return "Belum Bayar";
    }

    /**
     * Mengubah label tampilan ("Lunas" / "Belum Bayar") kembali menjadi
     * nilai status_pembayaran yang disimpan di database.
     */
    private String statusKeDb(String statusTampilan) {
        return "Lunas".equals(statusTampilan) ? "sudah_dibayar" : "belum_dibayar";
    }

    /**
     * Menampilkan dialog form untuk mengedit data transaksi yang sedang dipilih.
     * Customer dan Barang TIDAK bisa diubah di sini agar histori transaksi tetap konsisten;
     * jika perlu mengganti barang/customer, sebaiknya hapus transaksi dan buat yang baru.
     */
    private void bukaDialogEdit() {
        int row = tableTransaksi.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data transaksi yang ingin diedit terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTransaksi = (int) modelTable.getValueAt(row, 0);
        String namaCustomer = (String) modelTable.getValueAt(row, 1);
        String namaBarang = (String) modelTable.getValueAt(row, 2);
        String jenisLayananLama = (String) modelTable.getValueAt(row, 3);
        BigDecimal totalBiayaLama = (BigDecimal) modelTable.getValueAt(row, 5);
        String statusBayarLama = (String) modelTable.getValueAt(row, 6);
        String metodeLama = (String) modelTable.getValueAt(row, 7);
        java.sql.Timestamp tglBayarLama = (java.sql.Timestamp) modelTable.getValueAt(row, 8);
        String catatanLama = (String) modelTable.getValueAt(row, 9);

        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Transaksi");
        dialog.setModal(true);
        dialog.setSize(420, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtCustomer = new JTextField(namaCustomer);
        txtCustomer.setEditable(false);
        JTextField txtBarang = new JTextField(namaBarang == null ? "-" : namaBarang);
        txtBarang.setEditable(false);
        JTextField txtJenisLayanan = new JTextField(jenisLayananLama);
        JTextField txtTotalBiaya = new JTextField(totalBiayaLama.toPlainString());
        String[] statusOptions = {"Belum Bayar", "Lunas"};
        JComboBox<String> cbStatusBayar = new JComboBox<>(statusOptions);
        cbStatusBayar.setSelectedItem(statusBayarLama);
        String[] metodeOptions = {"-", "Transfer Bank", "Cash", "E-Wallet", "Kartu Kredit"};
        JComboBox<String> cbMetode = new JComboBox<>(metodeOptions);
        cbMetode.setSelectedItem(metodeLama == null ? "-" : metodeLama);
        JTextField txtTanggalBayar = new JTextField(
                tglBayarLama == null ? "" : tglBayarLama.toLocalDateTime().toLocalDate().toString());
        txtTanggalBayar.setToolTipText("Format: yyyy-mm-dd. Kosongkan jika belum dibayar.");
        JTextArea txtCatatan = new JTextArea(catatanLama == null ? "" : catatanLama, 3, 15);

        form.add(new JLabel("Customer:"));
        form.add(txtCustomer);
        form.add(new JLabel("Barang:"));
        form.add(txtBarang);
        form.add(new JLabel("Jenis Layanan:"));
        form.add(txtJenisLayanan);
        form.add(new JLabel("Total Biaya:"));
        form.add(txtTotalBiaya);
        form.add(new JLabel("Status Pembayaran:"));
        form.add(cbStatusBayar);
        form.add(new JLabel("Metode Pembayaran:"));
        form.add(cbMetode);
        form.add(new JLabel("Tanggal Pembayaran (yyyy-mm-dd):"));
        form.add(txtTanggalBayar);
        form.add(new JLabel("Catatan:"));
        form.add(new JScrollPane(txtCatatan));

        dialog.add(form, BorderLayout.CENTER);

        JPanel panelTombol = new JPanel();
        JButton btnSimpan = new JButton("Simpan Perubahan");
        JButton btnBatal = new JButton("Batal");
        panelTombol.add(btnSimpan);
        panelTombol.add(btnBatal);
        dialog.add(panelTombol, BorderLayout.SOUTH);

        btnBatal.addActionListener(e -> dialog.dispose());

        btnSimpan.addActionListener(e -> {
            String jenisLayanan = txtJenisLayanan.getText().trim();
            String totalBiayaStr = txtTotalBiaya.getText().trim();
            String statusBayar = (String) cbStatusBayar.getSelectedItem();
            String metode = (String) cbMetode.getSelectedItem();
            String tanggalBayarStr = txtTanggalBayar.getText().trim();
            String catatan = txtCatatan.getText().trim();

            if (jenisLayanan.isEmpty() || totalBiayaStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Jenis layanan dan total biaya harus diisi!",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal totalBiaya;
            try {
                totalBiaya = new BigDecimal(totalBiayaStr.replace(",", "."));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Total biaya harus berupa angka, contoh: 150000",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.time.LocalDate tanggalBayar = null;
            if (!tanggalBayarStr.isEmpty()) {
                try {
                    tanggalBayar = java.time.LocalDate.parse(tanggalBayarStr);
                } catch (java.time.format.DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Format tanggal pembayaran harus yyyy-mm-dd, contoh: 2026-06-30",
                            "Validasi Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String metodeFinal = "-".equals(metode) ? null : metode;

            if (updateTransaksi(idTransaksi, jenisLayanan, totalBiaya, statusKeDb(statusBayar),
                    metodeFinal, tanggalBayar, catatan)) {
                JOptionPane.showMessageDialog(dialog,
                        "Data transaksi berhasil diperbarui.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                muatData();
            }
        });

        dialog.setVisible(true);
    }

    /**
     * Memperbarui data transaksi. Tanggal pembayaran kini bisa diisi/diubah
     * secara manual lewat dialog Edit. Jika dikosongkan tetapi status diubah
     * menjadi "Lunas", tanggal pembayaran otomatis diisi dengan tanggal hari ini.
     */
    private boolean updateTransaksi(int idTransaksi, String jenisLayanan, BigDecimal totalBiaya,
            String statusPembayaran, String metodePembayaran, java.time.LocalDate tanggalPembayaranInput,
            String catatan) {
        String sqlUpdate = "UPDATE transaksi SET jenis_layanan=?, total_biaya=?, status_pembayaran=?, "
                + "metode_pembayaran=?, tanggal_pembayaran=? WHERE id_transaksi=?";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return false;
        }

        java.sql.Timestamp tanggalPembayaran;
        if (tanggalPembayaranInput != null) {
            tanggalPembayaran = java.sql.Timestamp.valueOf(tanggalPembayaranInput.atStartOfDay());
        } else if ("sudah_dibayar".equals(statusPembayaran)) {
            // Status Lunas tapi tanggal pembayaran dikosongkan -> isi otomatis hari ini
            tanggalPembayaran = new java.sql.Timestamp(System.currentTimeMillis());
        } else {
            tanggalPembayaran = null;
        }

        try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
            ps.setString(1, jenisLayanan);
            ps.setBigDecimal(2, totalBiaya);
            ps.setString(3, statusPembayaran);
            ps.setString(4, metodePembayaran);
            if (tanggalPembayaran != null) {
                ps.setTimestamp(5, tanggalPembayaran);
            } else {
                ps.setNull(5, java.sql.Types.TIMESTAMP);
            }
            ps.setInt(6, idTransaksi);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memperbarui data transaksi:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Menghapus data transaksi yang sedang dipilih.
     * Relasi ke pickup pakai ON DELETE CASCADE, jadi data pickup terkait
     * juga akan ikut terhapus.
     */
    private void hapusData() {
        int row = tableTransaksi.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data transaksi yang ingin dihapus terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTransaksi = (int) modelTable.getValueAt(row, 0);

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus transaksi ini?\n"
                + "Data pickup terkait juga akan ikut terhapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM transaksi WHERE id_transaksi = ?";
        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTransaksi);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Data transaksi berhasil dihapus.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            muatData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menghapus data transaksi:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}