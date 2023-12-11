package fr.pantheonsorbonne.miage.game;

//Création de la classe Carte avec tout ses attributs
public class Carte {
    //Attributs de le classe Carte
    public final String nomCarte;
    public final String typeCarte;//Couleur (Atout et Excuse sont des couleurs)
    //(1,2,3,...13, 14, 21). Jusqu'à 14, il y a donc doublon entre les atouts et les couleurs. 
    public final int valeurCarte;
    //Si la carte est 1, 21 ou l'excuse
    public final boolean carteBout;
    //Le nombre de points que la carte rapporte (Roi->5, Valet->2 etc)
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
