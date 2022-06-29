package Activite;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Activite {
    public String id = "";
    public String utilisateur;
    public String sport;
    public List<Position> positions = Arrays.asList();
    public OffsetDateTime dateCreation;
    public OffsetDateTime dateFin;

    public Activite() {
    }

    public Activite(String proprietaire, String sport) {
        this.utilisateur = proprietaire;
        this.sport = sport;
    }

    public Activite(String id, String proprietaire, String sport, List<Position> positions, OffsetDateTime dateCreation, OffsetDateTime dateFin) {
        this.id = id;
        this.utilisateur = proprietaire;
        this.sport = sport;
        this.positions = new ArrayList<Position>(positions);
        this.dateCreation = dateCreation;
        this.dateFin = dateFin;
    }

    public String getId() {
        return id;
    }

    public String getProprietaire() {
        return utilisateur;
    }

    public String getSport() {
        return sport;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public String getDateCreation() {
        DateTimeFormatter formated = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm:ss");
        return formated.format(dateCreation.plusHours(2));
    }

    public String getDateFin() {
        DateTimeFormatter formated = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm:ss");
        return dateFin == null ? null : formated.format(dateFin.plusHours(2));
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProprietaire(String proprietaire) {
        this.utilisateur = proprietaire;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public void setPositions(List<Position> positions) {
        this.positions = new ArrayList<Position>(positions);
    }

    public void setDateCreation(OffsetDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDateFin(OffsetDateTime dateFin) {
        this.dateCreation = dateFin;
    }

    public double calculDistance() {
        double result = 0.0;

        for(int i = 1; i < positions.size(); i++) {
            /*
             * Code calcul GPS trouvé sur:
             * https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
             */
            final int R = 6371; // Radius of the earth
    
            double latDistance = Math.toRadians(positions.get(i).latitude - positions.get(i - 1).latitude);
            double lonDistance = Math.toRadians(positions.get(i).longitude - positions.get(i - 1).longitude);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(positions.get(i - 1).latitude)) * Math.cos(Math.toRadians(positions.get(i).latitude))
                            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c; // convert to meters
    
            distance = Math.pow(distance, 2);
    
            result += Math.sqrt(distance);
        }

        return result;
    }

    public String calculDuree() {
        String result = "";
        if (dateFin != null) {
            Duration difference = Duration.between(dateCreation, dateFin);
            result = String.valueOf(difference.toMinutes()) + " minutes";
        }else {
            result = "Activité non terminée";
        }
        return result;
    }
}
