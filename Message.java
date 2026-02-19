import java.io.*;

public class Message implements Serializable {
    public Commande commande;
    public Object parametre;

    public Message(Commande commande, Object parametre) {
        this.commande = commande;
        this.parametre = parametre;
    }
}
