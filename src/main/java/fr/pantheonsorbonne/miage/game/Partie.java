package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class Partie {
    private Joueur joueur1;
    private Joueur joueur2;
    private Joueur joueur3;
    private Joueur joueur4;

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
        if (nombreJoueurs == 4) {
            joueur4 = new Joueur("Joueur 4", main4, 0, 1);
            // for (int i = 1; i <= nombreDeManches; i++) {
            int typeChien = 10;
            int count = 0;
            Manche manche = new Manche(joueur1, joueur2, joueur3, joueur4, deckMelange, typeChien);
            joueur1.mainJoueur = manche.joueur1.mainJoueur;
            joueur2.mainJoueur = manche.joueur2.mainJoueur;
            joueur3.mainJoueur = manche.joueur3.mainJoueur;
            joueur4.mainJoueur = manche.joueur4.mainJoueur;
            for (int i = 0; i < joueur1.mainJoueur.size(); i++) {
                System.out.println(joueur1.mainJoueur.get(i).nomCarte);
                count++;
            }
            System.out.println(count);
            // }
            calculPointsPartie();
        } else if (nombreJoueurs == 3) {
            // for (int i = 1; i <= nombreDeManches; i++) {
            int typeChien = 9;
            int count = 0;
            Manche manche = new Manche(joueur1, joueur2, joueur3, deckMelange, typeChien);
            joueur1.mainJoueur = manche.joueur1.mainJoueur;
            joueur2.mainJoueur = manche.joueur2.mainJoueur;
            joueur3.mainJoueur = manche.joueur3.mainJoueur;
            /*
             * for (int i = 0; i < joueur2.mainJoueur.size(); i++) {
             * System.out.println(joueur1.mainJoueur.get(i).nomCarte);
             * count++;
             * }
             * System.out.println(count);
             * // }
             * calculPointsPartie();
             */
        }
    }

    public void melangeDuDeck(Deck deckTarot) {
        Collections.shuffle(deckTarot.deckComplet);
        deckMelange = deckTarot.deckComplet;
    }

    public void calculPointsPartie() {

    }
}
