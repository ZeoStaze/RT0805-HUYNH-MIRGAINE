package Auth;

import java.io.IOException;

public class Utilisateur {
    private String identifiant;
    private String mdp;

    public Utilisateur() throws IOException, InterruptedException {
        this.identifiant = "";
        this.mdp = "";
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }
}