package fr.pantheonsorbonne.miage.game;

//Création de la classe Carte avec tout ses attributs
public class Carte {
    //Attributs de le classe Carte
    public final String nomCarte;
    public final String typeCarte;
    public final int valeurCarte;
    public final boolean carteBout;
    public final int pointsCarte;

    //Constructeur d'une carte, initialise une nouvelle carte avec les paramètres données
    public Carte(String type, int valeur, String nom, boolean bout, int points) {
        typeCarte = type;
        valeurCarte = valeur;
        nomCarte = nom;
        carteBout = bout;
        pointsCarte = points;
    }

    // méthode getters pour accèder aux attributs de la carte
    public String getType() {
        return typeCarte;
    }

    public int getValeur() {
        return valeurCarte;
    }

    public String getNom() {
        return nomCarte;
    }

    public boolean getBout() {
        return carteBout;
    }

    public int getPoints() {
        return pointsCarte;
    }
}
