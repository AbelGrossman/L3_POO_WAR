package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class Partie {
    private Joueur joueur1;
    private Joueur joueur2;
    private Joueur joueur3;
    private Joueur joueur4;

    private Joueur joueur1Partie;
    private Joueur joueur2Partie;
    private Joueur joueur3Partie;
    private Joueur joueur4Partie;

    private List<Carte> deckMelange;

    List<Carte> main1 = new ArrayList<>();
    List<Carte> main2 = new ArrayList<>();
    List<Carte> main3 = new ArrayList<>();
    List<Carte> main4 = new ArrayList<>();

    public Partie(int nombreJoueurs, Deck deckTarot, int nombreDeManches) {
        melangeDuDeck(deckTarot);
        joueur1 = new Joueur("Joueur 1", main1, 0, 1);
        joueur2 = new Joueur("Joueur 2", main2, 0, 1);
        joueur3 = new Joueur("Joueur 3", main3, 0, 1);
        joueur4 = null;
        Joueur swap = null;
        if (nombreJoueurs == 4) {
            joueur4 = new Joueur("Joueur 4", main4, 0, 1);
        }
        joueur1Partie = joueur1;
        joueur2Partie = joueur2;
        joueur3Partie = joueur3;
        joueur4Partie = joueur4;
        for (int i = 0; i < nombreDeManches; i++) {
            int typeChien = 9;
            Manche manche = new Manche(joueur1Partie, joueur2Partie, joueur3Partie, joueur4Partie, deckMelange,
                    typeChien);
            swap = joueur1Partie;
            joueur1Partie = joueur2Partie;
            joueur2Partie = joueur3Partie;
            joueur3Partie = swap;
            if (nombreJoueurs == 4) {
                joueur3Partie = joueur4Partie;
                joueur4Partie = swap;
            }
            System.out.println("Points j1: " + joueur1.pointsJoueur);
            System.out.println("Points j2: " + joueur2.pointsJoueur);
            System.out.println("Points j3: " + joueur3.pointsJoueur);
            // System.out.println("Points j4: " + joueur4.pointsJoueur);
        }
    }

    public void melangeDuDeck(Deck deckTarot) {
        Collections.shuffle(deckTarot.deckComplet);
        deckMelange = deckTarot.deckComplet;
    }

    public void calculPointsPartie() {

    }
}
