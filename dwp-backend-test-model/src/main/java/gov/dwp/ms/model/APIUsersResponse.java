package gov.dwp.ms.model;


public class APIUsersResponse {

    private int id;
    private String first_name;
    private String last_name;
    private String email;
    private String ip_address;
    private double latitude;
    private double longitude;
    
    public int getId() {
        return id;
    }

    public void setId(int value) { 
        this.id = value;
    }
    
    public String getFirst_name() {       
        return first_name;
    }

    public void setFirst_name(String value) {   
        this.first_name = value;
    }
    
    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String value) {
        this.last_name = value;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }
    
    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String value) {
        this.ip_address = value;
    }
    
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double value) {
        this.latitude = value;
    }
    
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double value) {
        this.longitude = value;
    }
}
