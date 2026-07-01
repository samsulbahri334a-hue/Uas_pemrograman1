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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * JPanel untuk menampilkan, menambah, mengedit, dan menghapus data pada
 * tabel `penitipan`. Setiap penitipan baru otomatis membuat satu baris
 * pada tabel `transaksi` (jenis_layanan = "Penitipan Barang") sehingga
 * menu Penitipan terkoneksi langsung dengan menu Transaksi.
 */
public class PanelPenitipan extends JPanel {

    private JTable tablePenitipan;
    private DefaultTableModel modelTable;
    private JButton btnRefresh;
    private JButton btnTambah;
    private JButton btnEdit;
    private JButton btnHapus;

    private final String[] kolom = {
        "ID Penitipan", "Customer", "Barang", "Tanggal Masuk", "Tanggal Keluar",
        "Durasi (hari)", "Biaya Penitipan", "Status Penitipan"
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

    public PanelPenitipan() {
        initComponents();
        muatData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblJudul = new JLabel("Data Penitipan", SwingConstants.LEFT);
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
        tablePenitipan = new JTable(modelTable);
        tablePenitipan.setRowHeight(24);
        tablePenitipan.getTableHeader().setReorderingAllowed(false);
        tablePenitipan.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tablePenitipan);
        add(scrollPane, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> muatData());
        btnTambah.addActionListener(e -> bukaDialogTambah());
        btnEdit.addActionListener(e -> bukaDialogEdit());
        btnHapus.addActionListener(e -> hapusData());
    }

    private void muatData() {
        modelTable.setRowCount(0);

        String sql = "SELECT p.id_penitipan, c.nama_customer, b.nama_barang, "
                + "p.tanggal_masuk, p.tanggal_keluar, p.durasi_penitipan, "
                + "p.biaya_penitipan, p.status_penitipan "
                + "FROM penitipan p "
                + "JOIN customer c ON p.id_customer = c.id_customer "
                + "JOIN barang b ON p.id_barang = b.id_barang "
                + "ORDER BY p.id_penitipan DESC";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] baris = {
                    rs.getInt("id_penitipan"),
                    rs.getString("nama_customer"),
                    rs.getString("nama_barang"),
                    rs.getDate("tanggal_masuk"),
                    rs.getDate("tanggal_keluar"),
                    rs.getObject("durasi_penitipan"),
                    rs.getBigDecimal("biaya_penitipan"),
                    rs.getString("status_penitipan")
                };
                modelTable.addRow(baris);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data penitipan:\n" + e.getMessage(),
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

    private java.util.List<ComboItem> ambilDaftarBarang(int idCustomer) {
        java.util.List<ComboItem> daftar = new java.util.ArrayList<>();
        String sql = "SELECT id_barang, nama_barang FROM barang WHERE id_customer = ? ORDER BY nama_barang ASC";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return daftar;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCustomer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(new ComboItem(rs.getInt("id_barang"), rs.getString("nama_barang")));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil daftar barang:\n" + e.getMessage(),
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
        dialog.setTitle("Tambah Penitipan Baru");
        dialog.setModal(true);
        dialog.setSize(420, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<ComboItem> cbCustomer = new JComboBox<>(daftarCustomer.toArray(new ComboItem[0]));
        JComboBox<ComboItem> cbBarang = new JComboBox<>();
        JTextField txtTanggalMasuk = new JTextField(java.time.LocalDate.now().toString());
        JTextField txtTanggalKeluar = new JTextField();
        JTextField txtDurasi = new JTextField();
        JTextField txtBiaya = new JTextField();
        String[] statusOptions = {"Dititipkan", "Diambil", "Selesai"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);

        ComboItem customerAwal = (ComboItem) cbCustomer.getSelectedItem();
        if (customerAwal != null) {
            for (ComboItem item : ambilDaftarBarang(customerAwal.id)) {
                cbBarang.addItem(item);
            }
        }
        cbCustomer.addActionListener(e -> {
            cbBarang.removeAllItems();
            ComboItem customerTerpilih = (ComboItem) cbCustomer.getSelectedItem();
            if (customerTerpilih != null) {
                for (ComboItem item : ambilDaftarBarang(customerTerpilih.id)) {
                    cbBarang.addItem(item);
                }
            }
        });

        form.add(new JLabel("Customer:"));
        form.add(cbCustomer);
        form.add(new JLabel("Barang:"));
        form.add(cbBarang);
        form.add(new JLabel("Tanggal Masuk (yyyy-mm-dd):"));
        form.add(txtTanggalMasuk);
        form.add(new JLabel("Tanggal Keluar (opsional):"));
        form.add(txtTanggalKeluar);
        form.add(new JLabel("Durasi (hari):"));
        form.add(txtDurasi);
        form.add(new JLabel("Biaya Penitipan:"));
        form.add(txtBiaya);
        form.add(new JLabel("Status Penitipan:"));
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
            ComboItem barangTerpilih = (ComboItem) cbBarang.getSelectedItem();
            String tglMasukStr = txtTanggalMasuk.getText().trim();
            String tglKeluarStr = txtTanggalKeluar.getText().trim();
            String durasiStr = txtDurasi.getText().trim();
            String biayaStr = txtBiaya.getText().trim();
            String status = (String) cbStatus.getSelectedItem();

            if (barangTerpilih == null) {
                JOptionPane.showMessageDialog(dialog,
                        "Customer ini belum memiliki data barang yang bisa dititipkan.",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (tglMasukStr.isEmpty() || biayaStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Tanggal masuk dan biaya penitipan harus diisi!",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date tglMasuk;
            Date tglKeluar = null;
            Integer durasi = null;
            BigDecimal biaya;
            try {
                tglMasuk = Date.valueOf(tglMasukStr);
                if (!tglKeluarStr.isEmpty()) {
                    tglKeluar = Date.valueOf(tglKeluarStr);
                }
                if (!durasiStr.isEmpty()) {
                    durasi = Integer.parseInt(durasiStr);
                }
                biaya = new BigDecimal(biayaStr.replace(",", "."));
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Format tanggal harus yyyy-mm-dd, durasi & biaya harus berupa angka.",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (simpanPenitipanBaru(customerTerpilih.id, barangTerpilih.id, tglMasuk,
                    tglKeluar, durasi, biaya, status)) {
                JOptionPane.showMessageDialog(dialog,
                        "Data penitipan berhasil ditambahkan dan transaksi terkait dibuat otomatis.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                muatData();
            }
        });

        dialog.setVisible(true);
    }

    /**
     * Menyimpan penitipan baru sekaligus membuat baris transaksi terkait
     * (jenis_layanan = "Penitipan Barang") agar menu Penitipan terkoneksi
     * langsung dengan menu Transaksi.
     */
    private boolean simpanPenitipanBaru(int idCustomer, int idBarang, Date tanggalMasuk,
            Date tanggalKeluar, Integer durasi, BigDecimal biaya, String status) {
        String sqlInsertTransaksi = "INSERT INTO transaksi (id_customer, id_barang, jenis_layanan, "
                + "total_biaya, status_pembayaran) VALUES (?, ?, 'Penitipan Barang', ?, 'belum_dibayar')";
        String sqlInsertPenitipan = "INSERT INTO penitipan (id_customer, id_barang, id_transaksi, "
                + "tanggal_masuk, tanggal_keluar, durasi_penitipan, biaya_penitipan, status_penitipan) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            int idTransaksi;
            try (PreparedStatement psTrans = conn.prepareStatement(sqlInsertTransaksi, Statement.RETURN_GENERATED_KEYS)) {
                psTrans.setInt(1, idCustomer);
                psTrans.setInt(2, idBarang);
                psTrans.setBigDecimal(3, biaya);
                psTrans.executeUpdate();
                try (ResultSet keys = psTrans.getGeneratedKeys()) {
                    if (!keys.next()) {
                        return false;
                    }
                    idTransaksi = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlInsertPenitipan)) {
                ps.setInt(1, idCustomer);
                ps.setInt(2, idBarang);
                ps.setInt(3, idTransaksi);
                ps.setDate(4, tanggalMasuk);
                if (tanggalKeluar != null) {
                    ps.setDate(5, tanggalKeluar);
                } else {
                    ps.setNull(5, java.sql.Types.DATE);
                }
                if (durasi != null) {
                    ps.setInt(6, durasi);
                } else {
                    ps.setNull(6, java.sql.Types.INTEGER);
                }
                ps.setBigDecimal(7, biaya);
                ps.setString(8, status);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan data penitipan:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void bukaDialogEdit() {
        int row = tablePenitipan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data penitipan yang ingin diedit terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPenitipan = (int) modelTable.getValueAt(row, 0);
        String namaCustomer = (String) modelTable.getValueAt(row, 1);
        String namaBarang = (String) modelTable.getValueAt(row, 2);
        Object tglKeluarLama = modelTable.getValueAt(row, 4);
        Object durasiLama = modelTable.getValueAt(row, 5);
        BigDecimal biayaLama = (BigDecimal) modelTable.getValueAt(row, 6);
        String statusLama = (String) modelTable.getValueAt(row, 7);

        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Penitipan");
        dialog.setModal(true);
        dialog.setSize(420, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtCustomer = new JTextField(namaCustomer);
        txtCustomer.setEditable(false);
        JTextField txtBarang = new JTextField(namaBarang);
        txtBarang.setEditable(false);
        JTextField txtTanggalKeluar = new JTextField(tglKeluarLama == null ? "" : tglKeluarLama.toString());
        JTextField txtDurasi = new JTextField(durasiLama == null ? "" : durasiLama.toString());
        JTextField txtBiaya = new JTextField(biayaLama.toPlainString());
        String[] statusOptions = {"Dititipkan", "Diambil", "Selesai"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        cbStatus.setSelectedItem(statusLama);

        form.add(new JLabel("Customer:"));
        form.add(txtCustomer);
        form.add(new JLabel("Barang:"));
        form.add(txtBarang);
        form.add(new JLabel("Tanggal Keluar (yyyy-mm-dd):"));
        form.add(txtTanggalKeluar);
        form.add(new JLabel("Durasi (hari):"));
        form.add(txtDurasi);
        form.add(new JLabel("Biaya Penitipan:"));
        form.add(txtBiaya);
        form.add(new JLabel("Status Penitipan:"));
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
            String tglKeluarStr = txtTanggalKeluar.getText().trim();
            String durasiStr = txtDurasi.getText().trim();
            String biayaStr = txtBiaya.getText().trim();
            String status = (String) cbStatus.getSelectedItem();

            if (biayaStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Biaya penitipan harus diisi!",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date tglKeluar = null;
            Integer durasi = null;
            BigDecimal biaya;
            try {
                if (!tglKeluarStr.isEmpty()) {
                    tglKeluar = Date.valueOf(tglKeluarStr);
                }
                if (!durasiStr.isEmpty()) {
                    durasi = Integer.parseInt(durasiStr);
                }
                biaya = new BigDecimal(biayaStr.replace(",", "."));
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Format tanggal harus yyyy-mm-dd, durasi & biaya harus berupa angka.",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (updatePenitipan(idPenitipan, tglKeluar, durasi, biaya, status)) {
                JOptionPane.showMessageDialog(dialog,
                        "Data penitipan berhasil diperbarui.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                muatData();
            }
        });

        dialog.setVisible(true);
    }

    /**
     * Memperbarui data penitipan, sekaligus menyamakan total_biaya pada
     * transaksi terkait agar data Penitipan dan Transaksi tetap konsisten.
     */
    private boolean updatePenitipan(int idPenitipan, Date tanggalKeluar, Integer durasi,
            BigDecimal biaya, String status) {
        String sqlUpdatePenitipan = "UPDATE penitipan SET tanggal_keluar=?, durasi_penitipan=?, "
                + "biaya_penitipan=?, status_penitipan=? WHERE id_penitipan=?";
        String sqlUpdateTransaksi = "UPDATE transaksi t "
                + "JOIN penitipan p ON p.id_transaksi = t.id_transaksi "
                + "SET t.total_biaya = ? WHERE p.id_penitipan = ?";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdatePenitipan)) {
                if (tanggalKeluar != null) {
                    ps.setDate(1, tanggalKeluar);
                } else {
                    ps.setNull(1, java.sql.Types.DATE);
                }
                if (durasi != null) {
                    ps.setInt(2, durasi);
                } else {
                    ps.setNull(2, java.sql.Types.INTEGER);
                }
                ps.setBigDecimal(3, biaya);
                ps.setString(4, status);
                ps.setInt(5, idPenitipan);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateTransaksi)) {
                ps.setBigDecimal(1, biaya);
                ps.setInt(2, idPenitipan);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memperbarui data penitipan:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Menghapus data penitipan beserta transaksi terkait.
     */
    private void hapusData() {
        int row = tablePenitipan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data penitipan yang ingin dihapus terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPenitipan = (int) modelTable.getValueAt(row, 0);

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus data penitipan ini?\n"
                + "Transaksi terkait juga akan ikut terhapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        String sqlAmbilTransaksi = "SELECT id_transaksi FROM penitipan WHERE id_penitipan = ?";
        String sqlHapusPenitipan = "DELETE FROM penitipan WHERE id_penitipan = ?";
        String sqlHapusTransaksi = "DELETE FROM transaksi WHERE id_transaksi = ?";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try {
            Integer idTransaksi = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlAmbilTransaksi)) {
                ps.setInt(1, idPenitipan);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int val = rs.getInt("id_transaksi");
                        if (!rs.wasNull()) {
                            idTransaksi = val;
                        }
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlHapusPenitipan)) {
                ps.setInt(1, idPenitipan);
                ps.executeUpdate();
            }

            if (idTransaksi != null) {
                try (PreparedStatement ps = conn.prepareStatement(sqlHapusTransaksi)) {
                    ps.setInt(1, idTransaksi);
                    ps.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Data penitipan berhasil dihapus.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            muatData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menghapus data penitipan:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
