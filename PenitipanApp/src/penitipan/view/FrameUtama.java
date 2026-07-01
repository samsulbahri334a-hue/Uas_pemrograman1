package penitipan.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * Frame utama aplikasi: menampilkan setiap tabel database
 * dalam JPanel yang berbeda, disusun menggunakan JTabbedPane.
 */
public class FrameUtama extends JFrame {

    public FrameUtama() {
        setTitle("Aplikasi Penitipan & Pindahan - Data Viewer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        
        // Mengatur layout utama frame menjadi BorderLayout
        setLayout(new BorderLayout());

        // --- TAMBAHAN PANEL ATAS UNTUK TOMBOL LOGOUT ---
        JPanel panelAtas = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Tombol rata kanan
        JButton btnLogout = new JButton("Logout");
        
        // Memberikan aksi pada tombol logout saat diklik
        btnLogout.addActionListener(e -> {
            int konfirmasi = JOptionPane.showConfirmDialog(
                this, 
                "Apakah Anda yakin ingin logout?", 
                "Konfirmasi Logout", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (konfirmasi == JOptionPane.YES_OPTION) {
                aksiLogout();
            }
        });
        
        panelAtas.add(btnLogout);
        // Tambahkan panel atas ke bagian NORTH dari frame
        add(panelAtas, BorderLayout.NORTH);
        // ----------------------------------------------

        // Bagian JTabbedPane yang sudah Anda buat
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Customer", new PanelCustomer());
        tabbedPane.addTab("Penitipan", new PanelPenitipan());
        tabbedPane.addTab("Pindahan", new PanelPindahan());
        tabbedPane.addTab("Transaksi", new PanelTransaksi());
        tabbedPane.addTab("Pickup", new PanelPickup());
        tabbedPane.addTab("Tracking", new PanelTracking());

        // Tambahkan tabbedPane ke bagian CENTER dari frame
        add(tabbedPane, BorderLayout.CENTER);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLocationRelativeTo(null);
    }

    /**
     * Method untuk menangani logika setelah logout berhasil terpilih
     */
/**
     * Method untuk menangani logika setelah logout berhasil terpilih
     */
    private void aksiLogout() {
        // 1. Tutup frame utama saat ini
        this.dispose();
        
        // 2. Jalankan kembali method main yang ada di class Main
        SwingUtilities.invokeLater(() -> {
            main.main(new String[0]);
        });
        
        System.out.println("User telah logout dan menjalankan Main.main()"); 
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // jika gagal set Look and Feel, lanjutkan dengan default
        }

        SwingUtilities.invokeLater(() -> {
            FrameUtama frame = new FrameUtama();
            frame.setVisible(true);
        });
    }
}