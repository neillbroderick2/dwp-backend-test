package gov.dwp.ms.model;


public class APILocationResponse {
    
    private String display_name;
    private String type;
    private String lat;
    private String lon;
    
    public String getDisplayName() {
        return display_name;
    }

    public void setDisplay_name(String value) { 
        this.display_name = value;
    }
    
    public String getType() { 
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }
    
    public String getLat() { 
        return lat;
    }

    public void setLat(String value) {       
        this.lat = value;
    }
    
    public String getLon() {       
        return lon;
    }

    public void setLon(String value) {       
        this.lon = value;
    }
}
