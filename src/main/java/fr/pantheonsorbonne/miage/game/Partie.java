package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Partie {
    //On déclare des joueurs pour la partie
    public Joueur joueur1Partie;
    public Joueur joueur2Partie;
    public Joueur joueur3Partie;
    public Joueur joueur4Partie;

    //On déclare des joueurs pour les manches
    public Joueur joueur1Manche;
    public Joueur joueur2Manche;
    public Joueur joueur3Manche;
    public Joueur joueur4Manche;

    public List<Carte> deckMelangePartie;

    //Pas compris à quoi servent les 4 mains
    List<Carte> main1Partie = new ArrayList<>();
    List<Carte> main2Partie = new ArrayList<>();
    List<Carte> main3Partie = new ArrayList<>();
    List<Carte> main4Partie = new ArrayList<>();
    Map<Joueur, Integer> misesPartie = new HashMap<>();
    public List<Joueur> joueurs = new ArrayList<>();
    Random rand = new Random();
    public int typeChien;
    public Joueur joueurGagnant;

    //Constructeur d'une partie avec comme argument le nombre de joueurs, le jeu au complet et le nombre de manches
    public Partie(int nombreJoueurs, Deck deckTarot, int nombreDeManches) {
        //Mélange du deck car il est pour l'instant triée dans l'ordre
        melangeDuDeck(deckTarot);
        //Création des joueurs
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
        //On initialise des joueurs pour la 1ere manche 
        //Si tu peux expliquer juste pourquoi faut créer un joueur diff à chaque manche c'est carré
        joueur1Manche = joueur1Partie;
        joueur2Manche = joueur2Partie;
        joueur3Manche = joueur3Partie;
        joueur4Manche = joueur4Partie;
        //boucle pour le nombre de manches
        for (int i = 0; i < nombreDeManches; i++) {
            selectionChien(nombreJoueurs);
            System.out.println();
            System.out.println();
            //Affichage numéro de manches
            int numeroManche = i + 1;
            System.out.println("Debut de la manche " + numeroManche);
            System.out.println();
            System.out.println();

            //On initialise les mises des joueurs à 0
            misesPartie.put(joueur1Partie, 0);
            misesPartie.put(joueur2Partie, 0);
            misesPartie.put(joueur3Partie, 0);
            misesPartie.put(joueur4Partie, 0);

            //Boucle jusqu'à ce que tous les joueurs aient misé (0 = pas de mise)
            while (misesPartie.get(joueur1Manche) == 0 && misesPartie.get(joueur2Manche) == 0
                    && misesPartie.get(joueur3Manche) == 0 && misesPartie.get(joueur4Manche) == 0) {
                        //Création d'une manche suite à la mise
                Manche manche = new Manche(joueur1Manche, joueur2Manche, joueur3Manche, joueur4Manche,
                        deckMelangePartie,
                        typeChien);
                //Attribution des mises en fonction du choix de chaque joueur
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
                //On nettoye les mains des joueurs pour la prochaine manche
                joueur1Manche.mainJoueur.clear();
                joueur2Manche.mainJoueur.clear();
                joueur3Manche.mainJoueur.clear();
                manche.packetChien.clear();

                // Rotation des joueurs pour la prochaine manche
                //le distributeur se décale
                swap = joueur1Manche;
                joueur1Manche = joueur2Manche;
                joueur2Manche = joueur3Manche;
                joueur3Manche = swap;
                if (nombreJoueurs == 4) {
                    joueur4Manche.mainJoueur.clear();
                    joueur3Manche = joueur4Manche;
                    joueur4Manche = swap;
                }
            }
            List<Integer> scoreFinaux=new ArrayList();
            System.out.println("Points du Joueur 1: " + joueur1Partie.pointsJoueur);
            System.out.println("Points du Joueur 2: " + joueur2Partie.pointsJoueur);
            System.out.println("Points du Joueur 3: " + joueur3Partie.pointsJoueur);
            if (nombreJoueurs == 4) {
                System.out.println("Points du Joueur 4: " + joueur4Partie.pointsJoueur);
            }
        }
        //On détermine le joueur gagnant de la partie en fonction des points
        if (nombreJoueurs == 3) {
            if (joueur1Partie.pointsJoueur > joueur2Partie.pointsJoueur
                    && joueur1Partie.pointsJoueur > joueur3Partie.pointsJoueur) {
                joueurGagnant = joueur1Partie;
            } else if (joueur2Partie.pointsJoueur > joueur1Partie.pointsJoueur
                    && joueur2Partie.pointsJoueur > joueur3Partie.pointsJoueur) {
                joueurGagnant = joueur2Partie;
            } else if (joueur3Partie.pointsJoueur > joueur1Partie.pointsJoueur
                    && joueur3Partie.pointsJoueur > joueur2Partie.pointsJoueur) {
                joueurGagnant = joueur3Partie;
            }
        } else if (nombreJoueurs == 4) {
            if (joueur1Partie.pointsJoueur > joueur2Partie.pointsJoueur
                    && joueur1Partie.pointsJoueur > joueur3Partie.pointsJoueur
                    && joueur1Partie.pointsJoueur > joueur4Partie.pointsJoueur) {
                joueurGagnant = joueur1Partie;
            } else if (joueur2Partie.pointsJoueur > joueur1Partie.pointsJoueur
                    && joueur2Partie.pointsJoueur > joueur3Partie.pointsJoueur
                    && joueur2Partie.pointsJoueur > joueur4Partie.pointsJoueur) {
                joueurGagnant = joueur2Partie;
            } else if (joueur3Partie.pointsJoueur > joueur1Partie.pointsJoueur
                    && joueur3Partie.pointsJoueur > joueur2Partie.pointsJoueur
                    && joueur3Partie.pointsJoueur > joueur4Partie.pointsJoueur) {
                joueurGagnant = joueur3Partie;
            } else if (joueur4Partie.pointsJoueur > joueur1Partie.pointsJoueur
                    && joueur4Partie.pointsJoueur > joueur2Partie.pointsJoueur
                    && joueur4Partie.pointsJoueur > joueur3Partie.pointsJoueur) {
                joueurGagnant = joueur4Partie;
            }
        }
        System.out.println("Le "+joueurGagnant+" remporte la partie!");
    }

    //on mélange le deck
    public void melangeDuDeck(Deck deckTarot) {
        Collections.shuffle(deckTarot.deckComplet);
        deckMelangePartie = deckTarot.deckComplet;
    }

    //On sélectionne le type de chien en fonction du nombre de joueurs et surtout au hasard (règle spéciale)
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
                    typeChien = 2;
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
