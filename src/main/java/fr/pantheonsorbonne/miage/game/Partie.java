package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Partie {
    private Joueur joueur1Partie;
    private Joueur joueur2Partie;
    private Joueur joueur3Partie;
    private Joueur joueur4Partie;

    private Joueur joueur1Manche;
    private Joueur joueur2Manche;
    private Joueur joueur3Manche;
    private Joueur joueur4Manche;

    private List<Carte> deckMelangePartie;

    List<Carte> main1Partie = new ArrayList<>();
    List<Carte> main2Partie = new ArrayList<>();
    List<Carte> main3Partie = new ArrayList<>();
    List<Carte> main4Partie = new ArrayList<>();
    Map<Joueur, Integer> misesPartie = new HashMap<>();
    List<Joueur> joueurs = new ArrayList<>();
    Random rand = new Random();
    int typeChien;

    public Partie(int nombreJoueurs, Deck deckTarot, int nombreDeManches) {
        melangeDuDeck(deckTarot);
        joueur1Partie = new Joueur("Joueur 1", main1Partie, 0, 1);
        joueur2Partie = new Joueur("Joueur 2", main2Partie, 0, 1);
        joueur3Partie = new Joueur("Joueur 3", main3Partie, 0, 1);
        joueurs.add(joueur1Partie);
        joueurs.add(joueur2Partie);
        joueurs.add(joueur3Partie);
        joueur4Partie = null;
        Joueur swap = null;
        if (nombreJoueurs == 4) {
            joueur4Partie = new Joueur("Joueur 4", main4Partie, 0, 1);
            joueurs.add(joueur4Partie);

        }

        joueur1Manche = joueur1Partie;
        joueur2Manche = joueur2Partie;
        joueur3Manche = joueur3Partie;
        joueur4Manche = joueur4Partie;
        for (int i = 0; i < nombreDeManches; i++) {
            selectionChien(nombreJoueurs);
            System.out.println();
            System.out.println();
            System.out.println("Debut du pli " + i);
            System.out.println();
            System.out.println();
            typeChien = 9;
            misesPartie.put(joueur1Partie, 0);
            misesPartie.put(joueur2Partie, 0);
            misesPartie.put(joueur3Partie, 0);
            misesPartie.put(joueur4Partie, 0);
            while (misesPartie.get(joueur1Manche) == 0 && misesPartie.get(joueur2Manche) == 0
                    && misesPartie.get(joueur3Manche) == 0 && misesPartie.get(joueur4Manche) == 0) {
                Manche manche = new Manche(joueur1Manche, joueur2Manche, joueur3Manche, joueur4Manche,
                        deckMelangePartie,
                        typeChien);
                for (Joueur j : joueurs) {
                    switch (j.miseJoueur) {
                        case "Garde Contre":
                            misesPartie.put(j, 4);
                            break;
                        case "Garde Sans":
                            misesPartie.put(j, 3);
                            break;
                        case "Garde":
                            misesPartie.put(j, 2);
                            break;
                        case "Petite":
                            misesPartie.put(j, 1);
                            break;
                        default:
                            misesPartie.put(j, 0);
                            break;
                    }
                }
                joueur1Manche.mainJoueur.clear();
                joueur2Manche.mainJoueur.clear();
                joueur3Manche.mainJoueur.clear();
                manche.packetChien.clear();
                swap = joueur1Manche;
                joueur1Manche = joueur2Manche;
                joueur2Manche = joueur3Manche;
                joueur3Manche = swap;
            }
            if (nombreJoueurs == 4) {
                joueur3Manche = joueur4Manche;
                joueur4Manche = swap;
            }
            System.out.println("Points j1: " + joueur1Partie.pointsJoueur);
            System.out.println("Points j2: " + joueur2Partie.pointsJoueur);
            System.out.println("Points j3: " + joueur3Partie.pointsJoueur);
            // System.out.println("Points j4: " + joueur4.pointsJoueur);
        }
        String joueurGagnant = joueur1Partie.nomJoueur;
        if (joueur2Partie.pointsJoueur > joueur1Partie.pointsJoueur
                && joueur2Partie.pointsJoueur > joueur3Partie.pointsJoueur) {
            joueurGagnant = joueur2Partie.nomJoueur;
        }
        if (joueur3Partie.pointsJoueur > joueur1Partie.pointsJoueur
                && joueur3Partie.pointsJoueur > joueur2Partie.pointsJoueur) {
            joueurGagnant = joueur3Partie.nomJoueur;
        }
        System.out.println("Le "+joueurGagnant+" gagne la partie!");
    }

    public void melangeDuDeck(Deck deckTarot) {
        Collections.shuffle(deckTarot.deckComplet);
        deckMelangePartie = deckTarot.deckComplet;
    }

    public void selectionChien(int nombreJoueurs) {
        if (nombreJoueurs == 3) {
            int randomValue = rand.nextInt(3);
            switch (randomValue) {
                case 0:
                    typeChien = 3;
                    break;
                case 1:
                    typeChien = 6;
                    break;
                case 2:
                    typeChien = 9;
                    break;
                default:
                    break;
            }
        } else if (nombreJoueurs == 4) {
            int randomValue = rand.nextInt(3);
            switch (randomValue) {
                case 0:
                    typeChien = 4;
                    break;
                case 1:
                    typeChien = 6;
                    break;
                case 2:
                    typeChien = 10;
                    break;
                default:
                    break;
            }
        }
    }

}
