import java.io.*;
import java.net.*;
import java.util.*;

public class ServeurMC {
    static int port = 8080;
    static final int maxClients = 50;
    static PrintWriter pw[];
    static int numClient = 0;
    static String pseudo = ConnexionClient.pseudo;
    static HashMap<String,String> Users = new HashMap<String,String>();
    static HashMap<String,List<ItemMinecraft>> database = new HashMap<String,List<ItemMinecraft>>();

    public static void main(String[] args) throws Exception {
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }
        pw = new PrintWriter[maxClients];

        // Charger les items enregistrés
        //chargerItems();

        ServerSocket s = new ServerSocket(port);
        System.out.println("SOCKET ECOUTE CREE => " + s);
        
        while (numClient < maxClients) {
            Socket soc = s.accept();
            ConnexionClient cc = new ConnexionClient(numClient, soc);
            System.out.println("Nouvelle connexion : " + soc);
            numClient++;
            cc.start();
        }
    }

    public static void sauvegarderItems() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("items.dat"))) {
            oos.writeObject(database.get(pseudo));
            System.out.println("Base de données sauvegardée.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ConnexionClient extends Thread {
    private int id;
    private Socket s;
    private ObjectInputStream sisr;
    private PrintWriter sisw;
    static String pseudo;
    static String mdp;
    static String choix;

    public ConnexionClient(int id, Socket s) throws ClassNotFoundException {
        this.id = id;
        this.s = s;
        try {
            sisr = new ObjectInputStream(s.getInputStream());
            sisw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
            Utilisateur user = (Utilisateur) sisr.readObject(); //lis les identifiants du client
            pseudo = user.getPseudo();
            mdp = user.getMdp();
            choix = user.getChoix();

            if(ServeurMC.database.get(pseudo) == null && choix.equals("creer")){ //si l'utilisateur n'existe pas et qu'il veut se créer un compte
                ServeurMC.database.put(pseudo,new ArrayList<ItemMinecraft>());
                ServeurMC.Users.put(pseudo, mdp);
            }
            else{
                boolean connexionOK = false;
                for (String key : ServeurMC.Users.keySet()) { //parcours de la liste des utilisateurs déjà existants
                    if (key.equals(pseudo) && ServeurMC.Users.get(key).equals(mdp)) {
                        connexionOK = true;
                        break;
                    }
                }
                
                if (connexionOK) {
                    sisw.println("ok");
                }
                else {
                    sisw.println("erreur");
                }
                }
        } catch (IOException e) { 
            e.printStackTrace();
        }
        ServeurMC.pw[id] = sisw;
    }

    public void run() {
        try {
            while (true) {
                Message message = (Message) sisr.readObject(); // Lire le message du client
                if (message.commande.equals("End")) break;
                
                switch (message.commande) {
                    case Create:
                        ServeurMC.database.get(pseudo).add((ItemMinecraft) message.parametre); //cree un objet minecraft qui se rajoute a la liste
                        ServeurMC.sauvegarderItems();
                        sisw.println("Item ajouté.");
                        break;
                    
                    case Read:
                        String nomRecherche = (String) message.parametre; //item que l'utilisateur recherche
                        boolean found = false;
                        for (ItemMinecraft item : ServeurMC.database.get(pseudo)) { //recherche dans la liste des items celui recherché par l'user
                            if (item.getNom().equalsIgnoreCase(nomRecherche)) { //ignore les differences ("ILIKE" en PG SQL)
                                sisw.println("Item trouvé : " + item.getNom() + "\ttype  : " + item.getType() + "\tdurabilité : " + item.getDurabilite() + "\tvaleur : " + item.getValeur());
                                found = true;
                            }
                        }
                        if (!found) {
                            sisw.println("Item non trouvé.");
                        }
                        break;

                    case Update:
                        ItemMinecraft modifItem = (ItemMinecraft) message.parametre;
                        for (ItemMinecraft item : ServeurMC.database.get(pseudo)){
                            if (item.getNom().equalsIgnoreCase(modifItem.getNom())){
                                item.setNom(modifItem.getNom());
                                item.setType(modifItem.getType());
                                item.setDurabilite(modifItem.getDurabilite());
                                item.setValeur(modifItem.getValeur());
                                ServeurMC.sauvegarderItems();
                                sisw.println("Item modifié.");
                            }
                            else{
                                sisw.println("Item non trouvé."); 
                            }
                        }
                        break;

                    case Delete:
                        String nomASupprimer = (String) message.parametre;
                        ServeurMC.database.get(pseudo).removeIf(item -> item.getNom().equalsIgnoreCase(nomASupprimer));
                        ServeurMC.sauvegarderItems();
                        sisw.println("Item supprimé.");
                        break;
                }

            }
            //fermeture des flux
            sisr.close();
            sisw.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
