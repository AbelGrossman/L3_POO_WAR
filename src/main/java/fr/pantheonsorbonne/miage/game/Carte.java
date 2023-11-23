package fr.pantheonsorbonne.miage.game;

public class Carte {
    public final String nomCarte;
    public final String typeCarte;
    public final int valeurCarte;
    public final boolean carteBout;
    public final int pointsCarte;

    public Carte(String type, int valeur, String nom, boolean bout, int points) {
        typeCarte = type;
        valeurCarte = valeur;
        nomCarte = nom;
        carteBout = bout;
        pointsCarte = points;
    }

    // Accesseurs pour les propriétés de la carte.
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
