package fr.pantheonsorbonne.miage.game;

import java.util.List;

// Classe principale, c'est ici que le programme commence son exécution et lance donc une partie
public class Application {
    public static void main(String... args) {
        //Création d'un deck
        Deck deckTarot = new Deck();
        //Création d'une nouvelle partie
        Partie partie = new Partie(4, deckTarot, 3); //(nb_joueur, deck, nb_manches)
    }
}
