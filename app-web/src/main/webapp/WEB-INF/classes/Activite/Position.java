package Activite;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class Position {
    public OffsetDateTime horodatage;
    public double latitude;
    public double longitude;

    public Position(OffsetDateTime horodatage, double latitude, double longitude) {
        this.horodatage = horodatage;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getHorodatage() {
        DateTimeFormatter formated = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm:ss");
        return formated.format(horodatage.plusHours(2));
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setHorodatage(OffsetDateTime horodatage) {
        this.horodatage = horodatage;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
