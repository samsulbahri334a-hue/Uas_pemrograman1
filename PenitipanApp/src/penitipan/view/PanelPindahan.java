package penitipan.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 * JPanel untuk menampilkan, menambah, mengedit, dan menghapus data pada
 * tabel `pindahan`. Setiap pindahan baru otomatis membuat satu baris
 * pada tabel `transaksi` (jenis_layanan = "Pindahan") sehingga menu
 * Pindahan terkoneksi langsung dengan menu Transaksi, dan satu baris
 * awal pada tabel `tracking` (status "Menunggu") sehingga menu Tracking
 * langsung memiliki riwayat untuk pindahan tersebut.
 */
public class PanelPindahan extends JPanel {

    private JTable tablePindahan;
    private DefaultTableModel modelTable;
    private JButton btnRefresh;
    private JButton btnTambah;
    private JButton btnEdit;
    private JButton btnHapus;

    private final String[] kolom = {
        "ID Pindahan", "Customer", "Alamat Asal", "Alamat Tujuan", "Tanggal Pindahan",
        "Jumlah Barang", "Berat Total (kg)", "Biaya Pindahan", "Status Pindahan"
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

    public PanelPindahan() {
        initComponents();
        muatData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblJudul = new JLabel("Data Pindahan", SwingConstants.LEFT);
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
        tablePindahan = new JTable(modelTable);
        tablePindahan.setRowHeight(24);
        tablePindahan.getTableHeader().setReorderingAllowed(false);
        tablePindahan.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tablePindahan);
        add(scrollPane, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> muatData());
        btnTambah.addActionListener(e -> bukaDialogTambah());
        btnEdit.addActionListener(e -> bukaDialogEdit());
        btnHapus.addActionListener(e -> hapusData());
    }

    private void muatData() {
        modelTable.setRowCount(0);

        String sql = "SELECT p.id_pindahan, c.nama_customer, p.alamat_asal, p.alamat_tujuan, "
                + "p.tanggal_pindahan, p.jumlah_barang, p.berat_total, p.biaya_pindahan, "
                + "p.status_pindahan "
                + "FROM pindahan p "
                + "JOIN customer c ON p.id_customer = c.id_customer "
                + "ORDER BY p.id_pindahan DESC";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] baris = {
                    rs.getInt("id_pindahan"),
                    rs.getString("nama_customer"),
                    rs.getString("alamat_asal"),
                    rs.getString("alamat_tujuan"),
                    rs.getDate("tanggal_pindahan"),
                    rs.getInt("jumlah_barang"),
                    rs.getBigDecimal("berat_total"),
                    rs.getBigDecimal("biaya_pindahan"),
                    rs.getString("status_pindahan")
                };
                modelTable.addRow(baris);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data pindahan:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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
        dialog.setTitle("Tambah Pindahan Baru");
        dialog.setModal(true);
        dialog.setSize(450, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<ComboItem> cbCustomer = new JComboBox<>(daftarCustomer.toArray(new ComboItem[0]));
        JTextArea txtAlamatAsal = new JTextArea(2, 15);
        JTextArea txtAlamatTujuan = new JTextArea(2, 15);
        JTextField txtTanggal = new JTextField(java.time.LocalDate.now().toString());
        JTextField txtJumlahBarang = new JTextField();
        JTextField txtBeratTotal = new JTextField();
        JTextField txtBiaya = new JTextField();
        String[] statusOptions = {"Menunggu", "Diproses", "Dikirim", "Selesai"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);

        form.add(new JLabel("Customer:"));
        form.add(cbCustomer);
        form.add(new JLabel("Alamat Asal:"));
        form.add(new JScrollPane(txtAlamatAsal));
        form.add(new JLabel("Alamat Tujuan:"));
        form.add(new JScrollPane(txtAlamatTujuan));
        form.add(new JLabel("Tanggal Pindahan (yyyy-mm-dd):"));
        form.add(txtTanggal);
        form.add(new JLabel("Jumlah Barang:"));
        form.add(txtJumlahBarang);
        form.add(new JLabel("Berat Total (kg):"));
        form.add(txtBeratTotal);
        form.add(new JLabel("Biaya Pindahan:"));
        form.add(txtBiaya);
        form.add(new JLabel("Status Pindahan:"));
        form.add(cbStatus);

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
            String alamatAsal = txtAlamatAsal.getText().trim();
            String alamatTujuan = txtAlamatTujuan.getText().trim();
            String tanggalStr = txtTanggal.getText().trim();
            String jumlahStr = txtJumlahBarang.getText().trim();
            String beratStr = txtBeratTotal.getText().trim();
            String biayaStr = txtBiaya.getText().trim();
            String status = (String) cbStatus.getSelectedItem();

            if (alamatAsal.isEmpty() || alamatTujuan.isEmpty() || tanggalStr.isEmpty() || biayaStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Alamat asal, alamat tujuan, tanggal, dan biaya pindahan harus diisi!",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date tanggal;
            int jumlahBarang;
            BigDecimal beratTotal;
            BigDecimal biaya;
            try {
                tanggal = Date.valueOf(tanggalStr);
                jumlahBarang = jumlahStr.isEmpty() ? 0 : Integer.parseInt(jumlahStr);
                beratTotal = beratStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(beratStr.replace(",", "."));
                biaya = new BigDecimal(biayaStr.replace(",", "."));
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Format tanggal harus yyyy-mm-dd, jumlah/berat/biaya harus berupa angka.",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (simpanPindahanBaru(customerTerpilih.id, alamatAsal, alamatTujuan, tanggal,
                    jumlahBarang, beratTotal, biaya, status)) {
                JOptionPane.showMessageDialog(dialog,
                        "Data pindahan berhasil ditambahkan.\n"
                        + "Transaksi dan riwayat tracking awal dibuat otomatis.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                muatData();
            }
        });

        dialog.setVisible(true);
    }

    /**
     * Menyimpan pindahan baru sekaligus membuat baris transaksi terkait
     * (jenis_layanan = "Pindahan") dan satu baris riwayat tracking awal,
     * sehingga menu Pindahan terkoneksi langsung dengan menu Transaksi
     * dan menu Tracking.
     */
    private boolean simpanPindahanBaru(int idCustomer, String alamatAsal, String alamatTujuan,
            Date tanggalPindahan, int jumlahBarang, BigDecimal beratTotal, BigDecimal biaya, String status) {
        String sqlInsertTransaksi = "INSERT INTO transaksi (id_customer, id_barang, jenis_layanan, "
                + "total_biaya, status_pembayaran) VALUES (?, NULL, 'Pindahan', ?, 'belum_dibayar')";
        String sqlInsertPindahan = "INSERT INTO pindahan (id_customer, id_transaksi, alamat_asal, "
                + "alamat_tujuan, tanggal_pindahan, jumlah_barang, berat_total, biaya_pindahan, "
                + "status_pindahan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlInsertTracking = "INSERT INTO tracking (id_pindahan, lokasi, status) VALUES (?, ?, ?)";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            int idTransaksi;
            try (PreparedStatement psTrans = conn.prepareStatement(sqlInsertTransaksi, Statement.RETURN_GENERATED_KEYS)) {
                psTrans.setInt(1, idCustomer);
                psTrans.setBigDecimal(2, biaya);
                psTrans.executeUpdate();
                try (ResultSet keys = psTrans.getGeneratedKeys()) {
                    if (!keys.next()) {
                        return false;
                    }
                    idTransaksi = keys.getInt(1);
                }
            }

            int idPindahan;
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertPindahan, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idCustomer);
                ps.setInt(2, idTransaksi);
                ps.setString(3, alamatAsal);
                ps.setString(4, alamatTujuan);
                ps.setDate(5, tanggalPindahan);
                ps.setInt(6, jumlahBarang);
                ps.setBigDecimal(7, beratTotal);
                ps.setBigDecimal(8, biaya);
                ps.setString(9, status);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        return false;
                    }
                    idPindahan = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlInsertTracking)) {
                ps.setInt(1, idPindahan);
                ps.setString(2, alamatAsal);
                ps.setString(3, status);
                ps.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan data pindahan:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void bukaDialogEdit() {
        int row = tablePindahan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data pindahan yang ingin diedit terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPindahan = (int) modelTable.getValueAt(row, 0);
        String namaCustomer = (String) modelTable.getValueAt(row, 1);
        String alamatAsalLama = (String) modelTable.getValueAt(row, 2);
        String alamatTujuanLama = (String) modelTable.getValueAt(row, 3);
        int jumlahLama = (int) modelTable.getValueAt(row, 5);
        BigDecimal beratLama = (BigDecimal) modelTable.getValueAt(row, 6);
        BigDecimal biayaLama = (BigDecimal) modelTable.getValueAt(row, 7);
        String statusLama = (String) modelTable.getValueAt(row, 8);

        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Pindahan");
        dialog.setModal(true);
        dialog.setSize(450, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtCustomer = new JTextField(namaCustomer);
        txtCustomer.setEditable(false);
        JTextArea txtAlamatAsal = new JTextArea(alamatAsalLama, 2, 15);
        JTextArea txtAlamatTujuan = new JTextArea(alamatTujuanLama, 2, 15);
        JTextField txtJumlahBarang = new JTextField(String.valueOf(jumlahLama));
        JTextField txtBeratTotal = new JTextField(beratLama.toPlainString());
        JTextField txtBiaya = new JTextField(biayaLama.toPlainString());
        String[] statusOptions = {"Menunggu", "Diproses", "Dikirim", "Selesai"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        cbStatus.setSelectedItem(statusLama);

        form.add(new JLabel("Customer:"));
        form.add(txtCustomer);
        form.add(new JLabel("Alamat Asal:"));
        form.add(new JScrollPane(txtAlamatAsal));
        form.add(new JLabel("Alamat Tujuan:"));
        form.add(new JScrollPane(txtAlamatTujuan));
        form.add(new JLabel("Jumlah Barang:"));
        form.add(txtJumlahBarang);
        form.add(new JLabel("Berat Total (kg):"));
        form.add(txtBeratTotal);
        form.add(new JLabel("Biaya Pindahan:"));
        form.add(txtBiaya);
        form.add(new JLabel("Status Pindahan:"));
        form.add(cbStatus);

        dialog.add(form, BorderLayout.CENTER);

        JPanel panelTombol = new JPanel();
        JButton btnSimpan = new JButton("Simpan Perubahan");
        JButton btnBatal = new JButton("Batal");
        panelTombol.add(btnSimpan);
        panelTombol.add(btnBatal);
        dialog.add(panelTombol, BorderLayout.SOUTH);

        btnBatal.addActionListener(e -> dialog.dispose());

        btnSimpan.addActionListener(e -> {
            String alamatAsal = txtAlamatAsal.getText().trim();
            String alamatTujuan = txtAlamatTujuan.getText().trim();
            String jumlahStr = txtJumlahBarang.getText().trim();
            String beratStr = txtBeratTotal.getText().trim();
            String biayaStr = txtBiaya.getText().trim();
            String status = (String) cbStatus.getSelectedItem();

            if (alamatAsal.isEmpty() || alamatTujuan.isEmpty() || biayaStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Alamat asal, alamat tujuan, dan biaya pindahan harus diisi!",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int jumlahBarang;
            BigDecimal beratTotal;
            BigDecimal biaya;
            try {
                jumlahBarang = jumlahStr.isEmpty() ? 0 : Integer.parseInt(jumlahStr);
                beratTotal = beratStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(beratStr.replace(",", "."));
                biaya = new BigDecimal(biayaStr.replace(",", "."));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Jumlah barang, berat total, dan biaya harus berupa angka.",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (updatePindahan(idPindahan, alamatAsal, alamatTujuan, jumlahBarang, beratTotal, biaya, status)) {
                JOptionPane.showMessageDialog(dialog,
                        "Data pindahan berhasil diperbarui.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                muatData();
            }
        });

        dialog.setVisible(true);
    }

    /**
     * Memperbarui data pindahan, menyamakan total_biaya pada transaksi
     * terkait, dan jika status berubah menambahkan satu baris riwayat
     * tracking baru agar histori perjalanan tetap tercatat.
     */
    private boolean updatePindahan(int idPindahan, String alamatAsal, String alamatTujuan,
            int jumlahBarang, BigDecimal beratTotal, BigDecimal biaya, String status) {
        String sqlCekStatusLama = "SELECT status_pindahan FROM pindahan WHERE id_pindahan = ?";
        String sqlUpdatePindahan = "UPDATE pindahan SET alamat_asal=?, alamat_tujuan=?, jumlah_barang=?, "
                + "berat_total=?, biaya_pindahan=?, status_pindahan=? WHERE id_pindahan=?";
        String sqlUpdateTransaksi = "UPDATE transaksi t "
                + "JOIN pindahan p ON p.id_transaksi = t.id_transaksi "
                + "SET t.total_biaya = ? WHERE p.id_pindahan = ?";
        String sqlInsertTracking = "INSERT INTO tracking (id_pindahan, lokasi, status) VALUES (?, ?, ?)";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String statusLama = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlCekStatusLama)) {
                ps.setInt(1, idPindahan);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statusLama = rs.getString("status_pindahan");
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdatePindahan)) {
                ps.setString(1, alamatAsal);
                ps.setString(2, alamatTujuan);
                ps.setInt(3, jumlahBarang);
                ps.setBigDecimal(4, beratTotal);
                ps.setBigDecimal(5, biaya);
                ps.setString(6, status);
                ps.setInt(7, idPindahan);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateTransaksi)) {
                ps.setBigDecimal(1, biaya);
                ps.setInt(2, idPindahan);
                ps.executeUpdate();
            }

            if (statusLama != null && !statusLama.equals(status)) {
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertTracking)) {
                    ps.setInt(1, idPindahan);
                    ps.setString(2, alamatTujuan);
                    ps.setString(3, status);
                    ps.executeUpdate();
                }
            }

            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memperbarui data pindahan:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Menghapus data pindahan beserta transaksi dan riwayat tracking terkait
     * (tracking ikut terhapus otomatis lewat ON DELETE CASCADE).
     */
    private void hapusData() {
        int row = tablePindahan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data pindahan yang ingin dihapus terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPindahan = (int) modelTable.getValueAt(row, 0);

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus data pindahan ini?\n"
                + "Transaksi dan riwayat tracking terkait juga akan ikut terhapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        String sqlAmbilTransaksi = "SELECT id_transaksi FROM pindahan WHERE id_pindahan = ?";
        String sqlHapusPindahan = "DELETE FROM pindahan WHERE id_pindahan = ?";
        String sqlHapusTransaksi = "DELETE FROM transaksi WHERE id_transaksi = ?";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try {
            Integer idTransaksi = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlAmbilTransaksi)) {
                ps.setInt(1, idPindahan);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int val = rs.getInt("id_transaksi");
                        if (!rs.wasNull()) {
                            idTransaksi = val;
                        }
                    }
                }
            }

            // Tracking ikut terhapus otomatis (ON DELETE CASCADE pada FK id_pindahan)
            try (PreparedStatement ps = conn.prepareStatement(sqlHapusPindahan)) {
                ps.setInt(1, idPindahan);
                ps.executeUpdate();
            }

            if (idTransaksi != null) {
                try (PreparedStatement ps = conn.prepareStatement(sqlHapusTransaksi)) {
                    ps.setInt(1, idTransaksi);
                    ps.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Data pindahan berhasil dihapus.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            muatData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menghapus data pindahan:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
