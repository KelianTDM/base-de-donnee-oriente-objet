import java.io.Serializable;

public class ItemMinecraft implements Serializable {
    private String nom;
    private String type;
    private int durabilite;
    private int valeur;

    public ItemMinecraft(String nom, String type, int durabilite, int valeur) {
        this.nom = nom;
        this.type = type;
        this.durabilite = durabilite;
        this.valeur = valeur;
    }

    public String getNom() {
        return nom;
    }

    public String getType(){
        return type;
    }

    public int getDurabilite(){
        return durabilite;
    }

    public int getValeur(){
        return valeur;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDurabilite(int durabilite) {
        this.durabilite = durabilite;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }
    

    @Override
    public String toString() {
        return "nom : " + nom + "\ntype : " + type + "\ndurabilite : " + durabilite + "\nvaleur" + valeur;
    }
}
