package penitipan.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
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
 * JPanel untuk menampilkan, menambah, dan menghapus data pada tabel `pickup`.
 */
public class PanelPickup extends JPanel {

    private JTable tablePickup;
    private DefaultTableModel modelTable;
    private JButton btnRefresh;
    private JButton btnTambah;
    private JButton btnEdit;
    private JButton btnHapus;

    private final String[] kolom = {
        "ID Pickup", "ID Transaksi", "Tanggal Pickup", "Jam Pickup",
        "Alamat Pickup", "Status", "Nama Driver", "No HP Driver",
        "Kendaraan", "Catatan"
    };

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

    public PanelPickup() {
        initComponents();
        muatData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblJudul = new JLabel("Data Pickup", SwingConstants.LEFT);
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
        tablePickup = new JTable(modelTable);
        tablePickup.setRowHeight(24);
        tablePickup.getTableHeader().setReorderingAllowed(false);
        tablePickup.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tablePickup);
        add(scrollPane, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> muatData());
        btnTambah.addActionListener(e -> bukaDialogTambah());
        btnEdit.addActionListener(e -> bukaDialogEdit());
        btnHapus.addActionListener(e -> hapusData());
    }

    private void muatData() {
        modelTable.setRowCount(0);

        String sql = "SELECT id_pickup, id_transaksi, tanggal_pickup, jam_pickup, "
                + "alamat_pickup, status_pickup, nama_driver, no_hp_driver, "
                + "kendaraan, catatan FROM pickup ORDER BY id_pickup ASC";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] baris = {
                    rs.getInt("id_pickup"),
                    rs.getInt("id_transaksi"),
                    rs.getDate("tanggal_pickup"),
                    rs.getTime("jam_pickup"),
                    rs.getString("alamat_pickup"),
                    rs.getString("status_pickup"),
                    rs.getString("nama_driver"),
                    rs.getString("no_hp_driver"),
                    rs.getString("kendaraan"),
                    rs.getString("catatan")
                };
                modelTable.addRow(baris);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data pickup:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mengambil daftar transaksi (label berisi nama customer & nama barang)
     * untuk mengisi JComboBox saat menambahkan pickup baru.
     */
    private java.util.List<ComboItem> ambilDaftarTransaksi() {
        java.util.List<ComboItem> daftar = new java.util.ArrayList<>();
        String sql = "SELECT t.id_transaksi, c.nama_customer, b.nama_barang "
                + "FROM transaksi t "
                + "JOIN customer c ON t.id_customer = c.id_customer "
                + "JOIN barang b ON t.id_barang = b.id_barang "
                + "ORDER BY t.id_transaksi ASC";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return daftar;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String label = rs.getString("nama_customer") + " (" + rs.getString("nama_barang") + ")";
                daftar.add(new ComboItem(rs.getInt("id_transaksi"), label));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil daftar transaksi:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return daftar;
    }

    private void bukaDialogTambah() {
        java.util.List<ComboItem> daftarTransaksi = ambilDaftarTransaksi();
        if (daftarTransaksi.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Belum ada data transaksi. Tambahkan transaksi terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("Tambah Pickup Baru");
        dialog.setModal(true);
        dialog.setSize(420, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<ComboItem> cbTransaksi = new JComboBox<>(daftarTransaksi.toArray(new ComboItem[0]));
        JTextField txtTanggal = new JTextField("yyyy-MM-dd");
        JTextField txtJam = new JTextField("HH:mm:ss");
        JTextField txtAlamat = new JTextField();
        String[] statusOptions = {"dijadwalkan", "menuju_lokasi", "selesai", "dibatalkan"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        JTextField txtNamaDriver = new JTextField();
        JTextField txtNoHpDriver = new JTextField();
        JTextField txtKendaraan = new JTextField();
        JTextArea txtCatatan = new JTextArea(2, 15);

        form.add(new JLabel("Transaksi:"));
        form.add(cbTransaksi);
        form.add(new JLabel("Tanggal Pickup:"));
        form.add(txtTanggal);
        form.add(new JLabel("Jam Pickup:"));
        form.add(txtJam);
        form.add(new JLabel("Alamat Pickup:"));
        form.add(txtAlamat);
        form.add(new JLabel("Status:"));
        form.add(cbStatus);
        form.add(new JLabel("Nama Driver:"));
        form.add(txtNamaDriver);
        form.add(new JLabel("No HP Driver:"));
        form.add(txtNoHpDriver);
        form.add(new JLabel("Kendaraan:"));
        form.add(txtKendaraan);

        dialog.add(form, BorderLayout.CENTER);

        JPanel panelCatatan = new JPanel(new BorderLayout(5, 5));
        panelCatatan.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 15, 15, 15));
        panelCatatan.add(new JLabel("Catatan:"), BorderLayout.NORTH);
        panelCatatan.add(new JScrollPane(txtCatatan), BorderLayout.CENTER);

        JPanel panelTombol = new JPanel();
        JButton btnSimpan = new JButton("Simpan");
        JButton btnBatal = new JButton("Batal");
        panelTombol.add(btnSimpan);
        panelTombol.add(btnBatal);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.add(panelCatatan, BorderLayout.CENTER);
        panelBawah.add(panelTombol, BorderLayout.SOUTH);
        dialog.add(panelBawah, BorderLayout.SOUTH);

        btnBatal.addActionListener(e -> dialog.dispose());

        btnSimpan.addActionListener(e -> {
            ComboItem transaksiTerpilih = (ComboItem) cbTransaksi.getSelectedItem();
            String tanggalStr = txtTanggal.getText().trim();
            String jamStr = txtJam.getText().trim();
            String alamat = txtAlamat.getText().trim();
            String status = (String) cbStatus.getSelectedItem();
            String namaDriver = txtNamaDriver.getText().trim();
            String noHpDriver = txtNoHpDriver.getText().trim();
            String kendaraan = txtKendaraan.getText().trim();
            String catatan = txtCatatan.getText().trim();

            if (tanggalStr.isEmpty() || jamStr.isEmpty() || alamat.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Tanggal, jam, dan alamat pickup harus diisi!",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date tanggalPickup;
            Time jamPickup;
            try {
                tanggalPickup = Date.valueOf(tanggalStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Format tanggal harus yyyy-MM-dd, contoh: 2026-07-01",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                jamPickup = Time.valueOf(jamStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Format jam harus HH:mm:ss, contoh: 09:30:00",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (simpanPickupBaru(transaksiTerpilih.id, tanggalPickup, jamPickup, alamat,
                    status, namaDriver, noHpDriver, kendaraan, catatan)) {
                JOptionPane.showMessageDialog(dialog,
                        "Data pickup berhasil ditambahkan.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                muatData();
            }
        });

        dialog.setVisible(true);
    }

    private boolean simpanPickupBaru(int idTransaksi, Date tanggalPickup, Time jamPickup,
            String alamat, String status, String namaDriver, String noHpDriver,
            String kendaraan, String catatan) {
        String sql = "INSERT INTO pickup (id_transaksi, tanggal_pickup, jam_pickup, alamat_pickup, "
                + "status_pickup, nama_driver, no_hp_driver, kendaraan, catatan) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTransaksi);
            ps.setDate(2, tanggalPickup);
            ps.setTime(3, jamPickup);
            ps.setString(4, alamat);
            ps.setString(5, status);
            ps.setString(6, namaDriver.isEmpty() ? null : namaDriver);
            ps.setString(7, noHpDriver.isEmpty() ? null : noHpDriver);
            ps.setString(8, kendaraan.isEmpty() ? null : kendaraan);
            ps.setString(9, catatan.isEmpty() ? null : catatan);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan data pickup:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Menampilkan dialog form untuk mengedit data pickup yang sedang dipilih.
     * Transaksi terkait TIDAK bisa diubah di sini.
     */
    private void bukaDialogEdit() {
        int row = tablePickup.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data pickup yang ingin diedit terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPickup = (int) modelTable.getValueAt(row, 0);
        int idTransaksi = (int) modelTable.getValueAt(row, 1);
        Date tanggalLama = (Date) modelTable.getValueAt(row, 2);
        Time jamLama = (Time) modelTable.getValueAt(row, 3);
        String alamatLama = (String) modelTable.getValueAt(row, 4);
        String statusLama = (String) modelTable.getValueAt(row, 5);
        String namaDriverLama = (String) modelTable.getValueAt(row, 6);
        String noHpDriverLama = (String) modelTable.getValueAt(row, 7);
        String kendaraanLama = (String) modelTable.getValueAt(row, 8);
        String catatanLama = (String) modelTable.getValueAt(row, 9);

        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Pickup");
        dialog.setModal(true);
        dialog.setSize(420, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtTransaksi = new JTextField("ID Transaksi: " + idTransaksi);
        txtTransaksi.setEditable(false);
        JTextField txtTanggal = new JTextField(tanggalLama.toString());
        JTextField txtJam = new JTextField(jamLama.toString());
        JTextField txtAlamat = new JTextField(alamatLama);
        String[] statusOptions = {"dijadwalkan", "menuju_lokasi", "selesai", "dibatalkan"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        cbStatus.setSelectedItem(statusLama);
        JTextField txtNamaDriver = new JTextField(namaDriverLama == null ? "" : namaDriverLama);
        JTextField txtNoHpDriver = new JTextField(noHpDriverLama == null ? "" : noHpDriverLama);
        JTextField txtKendaraan = new JTextField(kendaraanLama == null ? "" : kendaraanLama);
        JTextArea txtCatatan = new JTextArea(catatanLama == null ? "" : catatanLama, 2, 15);

        form.add(new JLabel("Transaksi:"));
        form.add(txtTransaksi);
        form.add(new JLabel("Tanggal Pickup:"));
        form.add(txtTanggal);
        form.add(new JLabel("Jam Pickup:"));
        form.add(txtJam);
        form.add(new JLabel("Alamat Pickup:"));
        form.add(txtAlamat);
        form.add(new JLabel("Status:"));
        form.add(cbStatus);
        form.add(new JLabel("Nama Driver:"));
        form.add(txtNamaDriver);
        form.add(new JLabel("No HP Driver:"));
        form.add(txtNoHpDriver);
        form.add(new JLabel("Kendaraan:"));
        form.add(txtKendaraan);

        dialog.add(form, BorderLayout.CENTER);

        JPanel panelCatatan = new JPanel(new BorderLayout(5, 5));
        panelCatatan.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 15, 15, 15));
        panelCatatan.add(new JLabel("Catatan:"), BorderLayout.NORTH);
        panelCatatan.add(new JScrollPane(txtCatatan), BorderLayout.CENTER);

        JPanel panelTombol = new JPanel();
        JButton btnSimpan = new JButton("Simpan Perubahan");
        JButton btnBatal = new JButton("Batal");
        panelTombol.add(btnSimpan);
        panelTombol.add(btnBatal);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.add(panelCatatan, BorderLayout.CENTER);
        panelBawah.add(panelTombol, BorderLayout.SOUTH);
        dialog.add(panelBawah, BorderLayout.SOUTH);

        btnBatal.addActionListener(e -> dialog.dispose());

        btnSimpan.addActionListener(e -> {
            String tanggalStr = txtTanggal.getText().trim();
            String jamStr = txtJam.getText().trim();
            String alamat = txtAlamat.getText().trim();
            String status = (String) cbStatus.getSelectedItem();
            String namaDriver = txtNamaDriver.getText().trim();
            String noHpDriver = txtNoHpDriver.getText().trim();
            String kendaraan = txtKendaraan.getText().trim();
            String catatan = txtCatatan.getText().trim();

            if (tanggalStr.isEmpty() || jamStr.isEmpty() || alamat.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Tanggal, jam, dan alamat pickup harus diisi!",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date tanggalPickup;
            Time jamPickup;
            try {
                tanggalPickup = Date.valueOf(tanggalStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Format tanggal harus yyyy-MM-dd, contoh: 2026-07-01",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                jamPickup = Time.valueOf(jamStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Format jam harus HH:mm:ss, contoh: 09:30:00",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (updatePickup(idPickup, tanggalPickup, jamPickup, alamat, status,
                    namaDriver, noHpDriver, kendaraan, catatan)) {
                JOptionPane.showMessageDialog(dialog,
                        "Data pickup berhasil diperbarui.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                muatData();
            }
        });

        dialog.setVisible(true);
    }

    private boolean updatePickup(int idPickup, Date tanggalPickup, Time jamPickup,
            String alamat, String status, String namaDriver, String noHpDriver,
            String kendaraan, String catatan) {
        String sql = "UPDATE pickup SET tanggal_pickup=?, jam_pickup=?, alamat_pickup=?, "
                + "status_pickup=?, nama_driver=?, no_hp_driver=?, kendaraan=?, catatan=? "
                + "WHERE id_pickup=?";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, tanggalPickup);
            ps.setTime(2, jamPickup);
            ps.setString(3, alamat);
            ps.setString(4, status);
            ps.setString(5, namaDriver.isEmpty() ? null : namaDriver);
            ps.setString(6, noHpDriver.isEmpty() ? null : noHpDriver);
            ps.setString(7, kendaraan.isEmpty() ? null : kendaraan);
            ps.setString(8, catatan.isEmpty() ? null : catatan);
            ps.setInt(9, idPickup);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memperbarui data pickup:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void hapusData() {
        int row = tablePickup.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data pickup yang ingin dihapus terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPickup = (int) modelTable.getValueAt(row, 0);

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus data pickup ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM pickup WHERE id_pickup = ?";
        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPickup);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Data pickup berhasil dihapus.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            muatData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menghapus data pickup:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}