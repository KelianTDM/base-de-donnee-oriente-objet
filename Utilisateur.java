import java.io.Serializable;

public class Utilisateur implements Serializable {
    private final String pseudo;
    private final String mdp;
    private final String choix;

    public Utilisateur(String pseudo, String mdp, String choix) {
        this.pseudo = pseudo;
        this.mdp = mdp;
        this.choix = choix;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getMdp() {
        return mdp;
    }

    public String getChoix() {
        return choix;
    }
}
