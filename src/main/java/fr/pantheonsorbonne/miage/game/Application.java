package fr.pantheonsorbonne.miage.game;

import java.util.List;

public class Application {
    public static void main(String... args) {
        Deck deckTarot = new Deck();
        Partie partie = new Partie(3, deckTarot, 9); //(nb_joueur, deck, nb_manches)
    }
}
