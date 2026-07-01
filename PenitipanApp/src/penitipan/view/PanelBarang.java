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
 * JPanel untuk menampilkan, menambah, dan menghapus data pada tabel `barang`.
 */
public class PanelBarang extends JPanel {

    private JTable tableBarang;
    private DefaultTableModel modelTable;
    private JButton btnRefresh;
    private JButton btnTambah;
    private JButton btnEdit;
    private JButton btnHapus;

    private final String[] kolom = {
        "ID Barang", "ID Customer", "Nama Customer", "Nama Barang",
        "Jenis Barang", "Berat (kg)", "Status", "Tanggal Input", "Keterangan"
    };

    /**
     * Item pembantu untuk menampilkan "ID - Nama" pada JComboBox,
     * namun tetap menyimpan ID asli untuk disimpan ke database.
     */
    private static class ComboItem {
        final int id;
        final String label;

        ComboItem(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return id + " - " + label;
        }
    }

    public PanelBarang() {
        initComponents();
        muatData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblJudul = new JLabel("Data Barang", SwingConstants.LEFT);
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(lblJudul, BorderLayout.WEST);

        btnTambah = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        JPanel panelKanan = new JPanel();
        panelKanan.add(btnTambah);
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
        tableBarang = new JTable(modelTable);
        tableBarang.setRowHeight(24);
        tableBarang.getTableHeader().setReorderingAllowed(false);
        tableBarang.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tableBarang);
        add(scrollPane, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> muatData());
        btnTambah.addActionListener(e -> bukaDialogTambah());
        btnEdit.addActionListener(e -> bukaDialogEdit());
        btnHapus.addActionListener(e -> hapusData());
    }

    private void muatData() {
        modelTable.setRowCount(0);

        String sql = "SELECT b.id_barang, b.id_customer, c.nama_customer, b.nama_barang, "
                + "b.jenis_barang, b.berat_barang, b.status_barang, b.tanggal_input, b.keterangan "
                + "FROM barang b "
                + "JOIN customer c ON b.id_customer = c.id_customer "
                + "ORDER BY b.id_barang ASC";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] baris = {
                    rs.getInt("id_barang"),
                    rs.getInt("id_customer"),
                    rs.getString("nama_customer"),
                    rs.getString("nama_barang"),
                    rs.getString("jenis_barang"),
                    rs.getBigDecimal("berat_barang"),
                    rs.getString("status_barang"),
                    rs.getTimestamp("tanggal_input"),
                    rs.getString("keterangan")
                };
                modelTable.addRow(baris);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data barang:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mengambil daftar customer dari database untuk mengisi JComboBox.
     */
    private java.util.List<ComboItem> ambilDaftarCustomer() {
        java.util.List<ComboItem> daftar = new java.util.ArrayList<>();
        String sql = "SELECT id_customer, nama_customer FROM customer ORDER BY nama_customer ASC";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return daftar;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftar.add(new ComboItem(rs.getInt("id_customer"), rs.getString("nama_customer")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil daftar customer:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return daftar;
    }

    private void bukaDialogTambah() {
        java.util.List<ComboItem> daftarCustomer = ambilDaftarCustomer();
        if (daftarCustomer.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Belum ada data customer. Tambahkan customer terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("Tambah Barang Baru");
        dialog.setModal(true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<ComboItem> cbCustomer = new JComboBox<>(daftarCustomer.toArray(new ComboItem[0]));
        JTextField txtNamaBarang = new JTextField();
        JTextField txtJenisBarang = new JTextField();
        JTextField txtBerat = new JTextField();
        String[] statusOptions = {"dalam_proses", "diterima", "dikirim", "selesai"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        JTextArea txtKeterangan = new JTextArea(3, 15);

        form.add(new JLabel("Customer:"));
        form.add(cbCustomer);
        form.add(new JLabel("Nama Barang:"));
        form.add(txtNamaBarang);
        form.add(new JLabel("Jenis Barang:"));
        form.add(txtJenisBarang);
        form.add(new JLabel("Berat (kg):"));
        form.add(txtBerat);
        form.add(new JLabel("Status:"));
        form.add(cbStatus);
        form.add(new JLabel("Keterangan:"));
        form.add(new JScrollPane(txtKeterangan));

        dialog.add(form, BorderLayout.CENTER);

        JPanel panelTombol = new JPanel();
        JButton btnSimpan = new JButton("Simpan");
        JButton btnBatal = new JButton("Batal");
        panelTombol.add(btnSimpan);
        panelTombol.add(btnBatal);
        dialog.add(panelTombol, BorderLayout.SOUTH);

        btnBatal.addActionListener(e -> dialog.dispose());

        btnSimpan.addActionListener(e -> {
            ComboItem customerTerpilih = (ComboItem) cbCustomer.getSelectedItem();
            String namaBarang = txtNamaBarang.getText().trim();
            String jenisBarang = txtJenisBarang.getText().trim();
            String beratStr = txtBerat.getText().trim();
            String status = (String) cbStatus.getSelectedItem();
            String keterangan = txtKeterangan.getText().trim();

            if (namaBarang.isEmpty() || jenisBarang.isEmpty() || beratStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Nama barang, jenis barang, dan berat harus diisi!",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal berat;
            try {
                berat = new BigDecimal(beratStr.replace(",", "."));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Berat barang harus berupa angka, contoh: 12.5",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (simpanBarangBaru(customerTerpilih.id, namaBarang, jenisBarang, berat, status, keterangan)) {
                JOptionPane.showMessageDialog(dialog,
                        "Data barang berhasil ditambahkan.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                muatData();
            }
        });

        dialog.setVisible(true);
    }

    private boolean simpanBarangBaru(int idCustomer, String namaBarang, String jenisBarang,
            BigDecimal berat, String status, String keterangan) {
        String sql = "INSERT INTO barang (id_customer, nama_barang, jenis_barang, berat_barang, "
                + "status_barang, keterangan) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCustomer);
            ps.setString(2, namaBarang);
            ps.setString(3, jenisBarang);
            ps.setBigDecimal(4, berat);
            ps.setString(5, status);
            ps.setString(6, keterangan.isEmpty() ? null : keterangan);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan data barang:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Menampilkan dialog form untuk mengedit data barang yang sedang dipilih.
     * Customer pemilik barang TIDAK bisa diubah di sini, karena mengubah
     * kepemilikan barang berisiko membuat data transaksi/tracking jadi tidak konsisten.
     */
    private void bukaDialogEdit() {
        int row = tableBarang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data barang yang ingin diedit terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idBarang = (int) modelTable.getValueAt(row, 0);
        String namaCustomer = (String) modelTable.getValueAt(row, 2);
        String namaBarangLama = (String) modelTable.getValueAt(row, 3);
        String jenisBarangLama = (String) modelTable.getValueAt(row, 4);
        BigDecimal beratLama = (BigDecimal) modelTable.getValueAt(row, 5);
        String statusLama = (String) modelTable.getValueAt(row, 6);
        String keteranganLama = (String) modelTable.getValueAt(row, 8);

        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Barang");
        dialog.setModal(true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtCustomer = new JTextField(namaCustomer);
        txtCustomer.setEditable(false);
        JTextField txtNamaBarang = new JTextField(namaBarangLama);
        JTextField txtJenisBarang = new JTextField(jenisBarangLama);
        JTextField txtBerat = new JTextField(beratLama.toPlainString());
        String[] statusOptions = {"dalam_proses", "diterima", "dikirim", "selesai"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        cbStatus.setSelectedItem(statusLama);
        JTextArea txtKeterangan = new JTextArea(keteranganLama == null ? "" : keteranganLama, 3, 15);

        form.add(new JLabel("Customer:"));
        form.add(txtCustomer);
        form.add(new JLabel("Nama Barang:"));
        form.add(txtNamaBarang);
        form.add(new JLabel("Jenis Barang:"));
        form.add(txtJenisBarang);
        form.add(new JLabel("Berat (kg):"));
        form.add(txtBerat);
        form.add(new JLabel("Status:"));
        form.add(cbStatus);
        form.add(new JLabel("Keterangan:"));
        form.add(new JScrollPane(txtKeterangan));

        dialog.add(form, BorderLayout.CENTER);

        JPanel panelTombol = new JPanel();
        JButton btnSimpan = new JButton("Simpan Perubahan");
        JButton btnBatal = new JButton("Batal");
        panelTombol.add(btnSimpan);
        panelTombol.add(btnBatal);
        dialog.add(panelTombol, BorderLayout.SOUTH);

        btnBatal.addActionListener(e -> dialog.dispose());

        btnSimpan.addActionListener(e -> {
            String namaBarang = txtNamaBarang.getText().trim();
            String jenisBarang = txtJenisBarang.getText().trim();
            String beratStr = txtBerat.getText().trim();
            String status = (String) cbStatus.getSelectedItem();
            String keterangan = txtKeterangan.getText().trim();

            if (namaBarang.isEmpty() || jenisBarang.isEmpty() || beratStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Nama barang, jenis barang, dan berat harus diisi!",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal berat;
            try {
                berat = new BigDecimal(beratStr.replace(",", "."));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Berat barang harus berupa angka, contoh: 12.5",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (updateBarang(idBarang, namaBarang, jenisBarang, berat, status, keterangan)) {
                JOptionPane.showMessageDialog(dialog,
                        "Data barang berhasil diperbarui.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                muatData();
            }
        });

        dialog.setVisible(true);
    }

    private boolean updateBarang(int idBarang, String namaBarang, String jenisBarang,
            BigDecimal berat, String status, String keterangan) {
        String sql = "UPDATE barang SET nama_barang=?, jenis_barang=?, berat_barang=?, "
                + "status_barang=?, keterangan=? WHERE id_barang=?";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaBarang);
            ps.setString(2, jenisBarang);
            ps.setBigDecimal(3, berat);
            ps.setString(4, status);
            ps.setString(5, keterangan.isEmpty() ? null : keterangan);
            ps.setInt(6, idBarang);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memperbarui data barang:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Menghapus data barang yang sedang dipilih.
     * Relasi ke tracking & transaksi pakai ON DELETE CASCADE,
     * jadi data tracking/transaksi terkait juga akan ikut terhapus.
     */
    private void hapusData() {
        int row = tableBarang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data barang yang ingin dihapus terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idBarang = (int) modelTable.getValueAt(row, 0);
        String namaBarang = (String) modelTable.getValueAt(row, 3);

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus barang \"" + namaBarang + "\"?\n"
                + "Data tracking & transaksi terkait juga akan ikut terhapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM barang WHERE id_barang = ?";
        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBarang);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Data barang berhasil dihapus.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            muatData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menghapus data barang:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}