/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package penitipan.model;

public class Customer {
    private int idCustomer;
    private String namaCustomer;
    private String alamat;
    private String noHp;
    private String email;
    private String password;
    private String tanggalDaftar;
    private String statusCustomer;
    
    // Constructor default
    public Customer(String namaCustomer, String alamat, String noHp, String email, String password) {
        this.namaCustomer    = namaCustomer;
        this.alamat         = alamat;
        this.noHp           = noHp;
        this.email          = email;
        this.password       = password;
        this.statusCustomer = "aktif";
    }
    
    // Constructor dengan parameter
    public Customer(int idCustomer, String namaCustomer, String alamat, 
                    String noHp, String email, String password, 
                    String tanggalDaftar, String statusCustomer) {
        this.idCustomer = idCustomer;
        this.namaCustomer = namaCustomer;
        this.alamat = alamat;
        this.noHp = noHp;
        this.email = email;
        this.password = password;
        this.tanggalDaftar = tanggalDaftar;
        this.statusCustomer = statusCustomer;
    }
    
    // Getter dan Setter
    public int getIdCustomer() {
        return idCustomer;
    }
    
    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }
    
    public String getNamaCustomer() {
        return namaCustomer;
    }
    
    public void setNamaCustomer(String namaCustomer) {
        this.namaCustomer = namaCustomer;
    }
    
    public String getAlamat() {
        return alamat;
    }
    
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    
    public String getNoHp() {
        return noHp;
    }
    
    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getTanggalDaftar() {
        return tanggalDaftar;
    }
    
    public void setTanggalDaftar(String tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar;
    }
    
    public String getStatusCustomer() {
        return statusCustomer;
    }
    
    public void setStatusCustomer(String statusCustomer) {
        this.statusCustomer = statusCustomer;
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "idCustomer=" + idCustomer +
                ", namaCustomer='" + namaCustomer + '\'' +
                ", alamat='" + alamat + '\'' +
                ", noHp='" + noHp + '\'' +
                ", email='" + email + '\'' +
                ", statusCustomer='" + statusCustomer + '\'' +
                '}';
    }

}