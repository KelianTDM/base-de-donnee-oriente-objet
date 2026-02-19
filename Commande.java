import java.io.*;

public enum Commande implements Serializable{
    Create("CREATE"),
    Read("READ"),
    Update("UPDATE"),
    Delete("DELETE"),
    End("END");

    public String text = "";

    private Commande(String text){
        this.text = text;
    }
}