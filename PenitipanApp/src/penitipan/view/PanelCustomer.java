package penitipan.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
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
 * JPanel untuk menampilkan, menambah, dan menghapus data pada tabel `customer`.
 */
public class PanelCustomer extends JPanel {

    private JTable tableCustomer;
    private DefaultTableModel modelTable;
    private JButton btnRefresh;
    private JButton btnTambah;
    private JButton btnEdit;
    private JButton btnHapus;
    private JTextField txtCari;
    private JButton btnCari;

    private final String[] kolom = {
        "ID Customer", "Nama Customer", "Alamat", "No HP",
        "Email", "Tanggal Daftar", "Status"
    };

    public PanelCustomer() {
        initComponents();
        muatData("");
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblJudul = new JLabel("Data Customer", SwingConstants.LEFT);
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel panelAtas = new JPanel(new BorderLayout(10, 10));
        panelAtas.add(lblJudul, BorderLayout.WEST);

        JPanel panelKanan = new JPanel();
        txtCari = new JTextField(15);
        btnCari = new JButton("Cari");
        btnTambah = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        panelKanan.add(new JLabel("Cari:"));
        panelKanan.add(txtCari);
        panelKanan.add(btnCari);
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
        tableCustomer = new JTable(modelTable);
        tableCustomer.setRowHeight(24);
        tableCustomer.getTableHeader().setReorderingAllowed(false);
        tableCustomer.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tableCustomer);
        add(scrollPane, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> {
            txtCari.setText("");
            muatData("");
        });
        btnCari.addActionListener(e -> muatData(txtCari.getText().trim()));
        btnTambah.addActionListener(e -> bukaDialogTambah());
        btnEdit.addActionListener(e -> bukaDialogEdit());
        btnHapus.addActionListener(e -> hapusData());
    }

    private void muatData(String keyword) {
        modelTable.setRowCount(0);

        String sql = "SELECT id_customer, nama_customer, alamat, no_hp, email, "
                + "tanggal_daftar, status_customer FROM customer";
        if (!keyword.isEmpty()) {
            sql += " WHERE nama_customer LIKE ? OR email LIKE ?";
        }
        sql += " ORDER BY id_customer ASC";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] baris = {
                        rs.getInt("id_customer"),
                        rs.getString("nama_customer"),
                        rs.getString("alamat"),
                        rs.getString("no_hp"),
                        rs.getString("email"),
                        rs.getTimestamp("tanggal_daftar"),
                        rs.getString("status_customer")
                    };
                    modelTable.addRow(baris);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data customer:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Menampilkan dialog form untuk menambahkan data customer baru.
     */
    private void bukaDialogTambah() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Tambah Customer Baru");
        dialog.setModal(true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtNama = new JTextField();
        JTextField txtAlamat = new JTextField();
        JTextField txtNoHp = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPassword = new JTextField();
        String[] statusOptions = {"aktif", "nonaktif"};
        javax.swing.JComboBox<String> cbStatus = new javax.swing.JComboBox<>(statusOptions);

        form.add(new JLabel("Nama Customer:"));
        form.add(txtNama);
        form.add(new JLabel("Alamat:"));
        form.add(txtAlamat);
        form.add(new JLabel("No HP:"));
        form.add(txtNoHp);
        form.add(new JLabel("Email:"));
        form.add(txtEmail);
        form.add(new JLabel("Password:"));
        form.add(txtPassword);
        form.add(new JLabel("Status:"));
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
            String nama = txtNama.getText().trim();
            String alamat = txtAlamat.getText().trim();
            String noHp = txtNoHp.getText().trim();
            String email = txtEmail.getText().trim();
            String password = txtPassword.getText().trim();
            String status = (String) cbStatus.getSelectedItem();

            if (nama.isEmpty() || alamat.isEmpty() || noHp.isEmpty()
                    || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Semua field harus diisi!",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (simpanCustomerBaru(nama, alamat, noHp, email, password, status)) {
                JOptionPane.showMessageDialog(dialog,
                        "Data customer berhasil ditambahkan.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                muatData("");
            }
        });

        dialog.setVisible(true);
    }

    private boolean simpanCustomerBaru(String nama, String alamat, String noHp,
            String email, String password, String status) {
        String sql = "INSERT INTO customer (nama_customer, alamat, no_hp, email, password, status_customer) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, alamat);
            ps.setString(3, noHp);
            ps.setString(4, email);
            ps.setString(5, password);
            ps.setString(6, status);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan data customer:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Menampilkan dialog form untuk mengedit data customer yang sedang dipilih.
     */
    private void bukaDialogEdit() {
        int row = tableCustomer.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data customer yang ingin diedit terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCustomer = (int) modelTable.getValueAt(row, 0);
        String namaLama = (String) modelTable.getValueAt(row, 1);
        String alamatLama = (String) modelTable.getValueAt(row, 2);
        String noHpLama = (String) modelTable.getValueAt(row, 3);
        String emailLama = (String) modelTable.getValueAt(row, 4);
        String statusLama = (String) modelTable.getValueAt(row, 6);

        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Customer");
        dialog.setModal(true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtNama = new JTextField(namaLama);
        JTextField txtAlamat = new JTextField(alamatLama);
        JTextField txtNoHp = new JTextField(noHpLama);
        JTextField txtEmail = new JTextField(emailLama);
        JTextField txtPassword = new JTextField();
        txtPassword.setToolTipText("Biarkan kosong jika tidak ingin mengubah password");
        String[] statusOptions = {"aktif", "nonaktif"};
        javax.swing.JComboBox<String> cbStatus = new javax.swing.JComboBox<>(statusOptions);
        cbStatus.setSelectedItem(statusLama);

        form.add(new JLabel("Nama Customer:"));
        form.add(txtNama);
        form.add(new JLabel("Alamat:"));
        form.add(txtAlamat);
        form.add(new JLabel("No HP:"));
        form.add(txtNoHp);
        form.add(new JLabel("Email:"));
        form.add(txtEmail);
        form.add(new JLabel("Password baru (opsional):"));
        form.add(txtPassword);
        form.add(new JLabel("Status:"));
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
            String nama = txtNama.getText().trim();
            String alamat = txtAlamat.getText().trim();
            String noHp = txtNoHp.getText().trim();
            String email = txtEmail.getText().trim();
            String passwordBaru = txtPassword.getText().trim();
            String status = (String) cbStatus.getSelectedItem();

            if (nama.isEmpty() || alamat.isEmpty() || noHp.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Nama, alamat, no HP, dan email harus diisi!",
                        "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (updateCustomer(idCustomer, nama, alamat, noHp, email, passwordBaru, status)) {
                JOptionPane.showMessageDialog(dialog,
                        "Data customer berhasil diperbarui.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                muatData("");
            }
        });

        dialog.setVisible(true);
    }

    /**
     * Memperbarui data customer. Jika passwordBaru kosong, password lama
     * tidak diubah (agar user tidak perlu mengetik ulang password setiap edit).
     */
    private boolean updateCustomer(int idCustomer, String nama, String alamat, String noHp,
            String email, String passwordBaru, String status) {
        String sql;
        if (passwordBaru.isEmpty()) {
            sql = "UPDATE customer SET nama_customer=?, alamat=?, no_hp=?, email=?, "
                    + "status_customer=? WHERE id_customer=?";
        } else {
            sql = "UPDATE customer SET nama_customer=?, alamat=?, no_hp=?, email=?, "
                    + "password=?, status_customer=? WHERE id_customer=?";
        }

        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, alamat);
            ps.setString(3, noHp);
            ps.setString(4, email);
            if (passwordBaru.isEmpty()) {
                ps.setString(5, status);
                ps.setInt(6, idCustomer);
            } else {
                ps.setString(5, passwordBaru);
                ps.setString(6, status);
                ps.setInt(7, idCustomer);
            }
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memperbarui data customer:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Menghapus data customer yang sedang dipilih di tabel.
     * Perlu diperhatikan: relasi ke barang & transaksi pakai ON DELETE CASCADE,
     * jadi data barang/transaksi milik customer ini juga akan ikut terhapus.
     */
    private void hapusData() {
        int row = tableCustomer.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data customer yang ingin dihapus terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCustomer = (int) modelTable.getValueAt(row, 0);
        String nama = (String) modelTable.getValueAt(row, 1);

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus customer \"" + nama + "\"?\n"
                + "Semua data barang & transaksi milik customer ini juga akan terhapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM customer WHERE id_customer = ?";
        Connection conn = KoneksiDatabase.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCustomer);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Data customer berhasil dihapus.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            muatData("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menghapus data customer:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}