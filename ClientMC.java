import java.io.*;
import java.net.*;
import java.util.*;

public class ClientMC {
    private Socket socket;
    private ObjectOutputStream out;
    private BufferedReader in;
    private Scanner scanner;
    private Hashtable<String,List<ItemMinecraft>> baseData;

    public ClientMC(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            scanner = new Scanner(System.in);

            System.out.println("Connexion réussie au serveur Minecraft ");

            System.out.println("1 - Crée Utilisateur");
            System.out.println("2 - Connexion ");
            int choix = scanner.nextInt(); //lis l'entrée de l'utilisateur
            scanner.nextLine(); // Nettoyer l'entrée

            switch (choix) {
                case 1:
                    creeUtilisateur();
                    break;
                case 2:
                    connexionUtilisateur();
                    break;
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void creeUtilisateur(){
        System.out.println("Entrez votre pseudo :");
        String pseudo = scanner.nextLine();
        System.out.println("Entrez votre mot de passe :");
        String mdp = scanner.nextLine();
        String choix = "creer";
        Utilisateur user = new Utilisateur(pseudo,mdp,choix);
        try{
            out.writeObject(user); //envoie les identifiants de l'utilisateur au serveur
        }
        catch (IOException e){
            e.printStackTrace();
        }
        menuPrincipal();
    }


    private void connexionUtilisateur(){
        System.out.println("Entrez votre pseudo :");
        String pseudo = scanner.nextLine();
        System.out.println("Entrez votre mot de passe :");
        String mdp = scanner.nextLine();
        String choix = "connexion";
        Utilisateur user = new Utilisateur(pseudo,mdp,choix);
        try{
            out.writeObject(user);
            String reponse = in.readLine();
            if(reponse.equals("ok")){
                System.out.println("Connexion réussie");
            }
            else if (reponse.equals("erreur")){
                System.out.println("Erreur de connexion");
                return;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }  
        menuPrincipal();
    }


    private void menuPrincipal() {
        while (true) {
            System.out.println("\nMenu :");
            System.out.println("1 - Ajouter un item");
            System.out.println("2 - Rechercher un item");
            System.out.println("3 - Modifier un item");
            System.out.println("4 - Supprimer un item");
            System.out.println("5 - Quitter");
            System.out.print("Choix : ");
            int choix = scanner.nextInt(); //lis l'entrée de l'utilisateur
            scanner.nextLine(); // Nettoyer l'entrée

            switch (choix) {
                case 1:
                    ajouterItem();
                    break;
                case 2:
                    rechercherItem();
                    break;
                case 3:
                    modifierItem();
                    break;
                case 4:
                    supprimerItem();
                    break;
                case 5:
                    fermerConnexion();
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void ajouterItem() {
        System.out.print("Nom de l'item : ");
        String nom = scanner.nextLine();
        System.out.print("Type : ");
        String type = scanner.nextLine();
        System.out.print("Durabilité : ");
        int durabilite = scanner.nextInt();
        System.out.print("Valeur : ");
        int valeur = scanner.nextInt();
        scanner.nextLine(); // Nettoyer l'entrée

        ItemMinecraft item = new ItemMinecraft(nom, type, durabilite, valeur);
        envoyerMessage(new Message(Commande.Create, item));

        lireReponse();
    }


    private void rechercherItem() {
        System.out.print("Entrez le nom de l'item : ");
        String nom = scanner.nextLine();

        envoyerMessage(new Message(Commande.Read, nom));
        lireReponse();
    }

    private void modifierItem() {
        System.out.print("Nom de l'item à modifier : ");
        String nom = scanner.nextLine();
        System.out.print("Nouveau type : ");
        String type = scanner.nextLine();
        System.out.print("Nouvelle durabilité : ");
        int durabilite = scanner.nextInt();
        System.out.print("Nouvelle valeur : ");
        int valeur = scanner.nextInt();
        scanner.nextLine(); // Nettoyer l'entrée

        ItemMinecraft item = new ItemMinecraft(nom, type, durabilite, valeur);
        envoyerMessage(new Message(Commande.Update, item));

        lireReponse();
    }

    private void supprimerItem() {
        System.out.print("Nom de l'item à supprimer : ");
        String nom = scanner.nextLine();

        envoyerMessage(new Message(Commande.Delete, nom));
        lireReponse();
    }

    private void envoyerMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lireReponse() {
        try {
            String reponse;
            while ((reponse = in.readLine()) != null) {
                System.out.println("Réponse du serveur : " + reponse);
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fermerConnexion() {
        try {
            System.out.println("Déconnexion...");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ClientMC(args[0], 8080);
    }
}
