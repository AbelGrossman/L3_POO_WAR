package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Partie {

    // On déclare des joueurs pour la partie
    public Joueur joueur1Partie;
    public Joueur joueur2Partie;
    public Joueur joueur3Partie;
    public Joueur joueur4Partie;

    // On déclare des joueurs pour les manches
    // Cette distinction est importante pour l'ordre de distribution qui varie
    // pendant la partie
    public Joueur joueur1Manche;
    public Joueur joueur2Manche;
    public Joueur joueur3Manche;
    public Joueur joueur4Manche;

    public List<Carte> deckMelangePartie;

    // Creation d'une main pour chaque joueur
    List<Carte> main1Partie = new ArrayList<>();
    List<Carte> main2Partie = new ArrayList<>();
    List<Carte> main3Partie = new ArrayList<>();
    List<Carte> main4Partie = new ArrayList<>();

    // HashMap pour récupérer les valeurs des mises d'une manche au sein d'une
    // partie
    Map<Joueur, Integer> misesPartie = new HashMap<>();

    public List<Joueur> joueurs = new ArrayList<>();

    // Atribut distinguant les différents chiens possibles (3-6-9 à trois joueurs,
    // 2-6-10 à 4)
    public int typeChien;

    public Joueur joueurGagnant;

    Random rand = new Random();

    // Constructeur d'une partie avec comme argument le nombre de joueurs, le jeu au
    // complet et le nombre de manches
    public Partie(int nombreJoueurs, Deck deckTarot, int nombreDeManches) {
        // Mélange du deck car il est pour l'instant triée dans l'ordre
        melangeDuDeck(deckTarot);
        // Instanciation des joueurs
        joueur1Partie = new Joueur("Joueur 1", main1Partie, 0, 1);
        joueur2Partie = new Joueur("Joueur 2", main2Partie, 0, 1);
        joueur3Partie = new Joueur("Joueur 3", main3Partie, 0, 1);
        joueurs.add(joueur1Partie);
        joueurs.add(joueur2Partie);
        joueurs.add(joueur3Partie);
        joueur4Partie = null;
        // Instanciation du 4ème joueur si la partie se joue à 4
        if (nombreJoueurs == 4) {
            joueur4Partie = new Joueur("Joueur 4", main4Partie, 0, 1);
            joueurs.add(joueur4Partie);
        }
        // Variable permettant la permutation des joueurs (expliqué plus bas)
        Joueur swap = null;
        // Nous voulons une méthode permettant de faire une translation dans l'ordre des
        // joueurs, car
        // au tarot, un joueur joue en premier à la première manche, puis son voison de
        // droite et ainsi
        // de suite.
        // Le but est ici de créé des Joueurs permanent tout le long de la partie, les
        // "joueursPartie"
        // et des joueurs temporaires, les "joueursManche". Lorsqu'on créera une manche,
        // on entrera en
        // paramètre les joueursManche, dont toujours dans le meme ordre (joueur1,
        // joueur2, joueur3).
        // Ainsi, on assigne à ces joueursManches des joueursPartie Différent avant
        // chaque nouvelle manche
        // pour faire une permutation de l'ordre.
        // Pour la première manche on respecte l'ordre
        joueur1Manche = joueur1Partie;
        joueur2Manche = joueur2Partie;
        joueur3Manche = joueur3Partie;
        joueur4Manche = joueur4Partie;
        // boucle pour le nombre de manches
        for (int i = 0; i < nombreDeManches; i++) {
            // selection du chien
            selectionChien(nombreJoueurs);

            int numeroManche = i + 1;
            System.out.println("Debut de la manche " + numeroManche);

            // On initialise les mises des joueurs à 0 pour pouvoire entrer dans la boucle,
            // et controler le derouler des manches depuis la classe Partie
            misesPartie.put(joueur1Partie, 0);
            misesPartie.put(joueur2Partie, 0);
            misesPartie.put(joueur3Partie, 0);
            misesPartie.put(joueur4Partie, 0);

            //Boucle jusqu'à ce que tous les joueurs aient misé (0 = passé, càd pas de mise)
            while (misesPartie.get(joueur1Manche) == 0 && misesPartie.get(joueur2Manche) == 0
                    && misesPartie.get(joueur3Manche) == 0 && misesPartie.get(joueur4Manche) == 0) {
                // Création d'une manche prenant en parametres les joueurs (toujours 4), le deck melangé,
                //et le type de chien
                Manche manche = new Manche(joueur1Manche, joueur2Manche, joueur3Manche, joueur4Manche,
                        deckMelangePartie,
                        typeChien);
                // Attribution d'une valeur de mise en fonction des mises de chaque joueur, 
                // pour potentiellement sortir de la boucle.
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

                // On nettoye les mains des joueurs pour la prochaine manche, et le packet du chien
                // Les deux sont remplis dans la classe manche.
                joueur1Manche.mainJoueur.clear();
                joueur2Manche.mainJoueur.clear();
                joueur3Manche.mainJoueur.clear();
                manche.packetChien.clear();

                // Rotation des joueurs pour la prochaine manche
                // Le donneur est décalé d'un rang à droite et ainsi de suite
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
            
            System.out.println("Points du Joueur 1: " + joueur1Partie.pointsJoueur);
            System.out.println("Points du Joueur 2: " + joueur2Partie.pointsJoueur);
            System.out.println("Points du Joueur 3: " + joueur3Partie.pointsJoueur);
            if (nombreJoueurs == 4) {
                System.out.println("Points du Joueur 4: " + joueur4Partie.pointsJoueur);
            }
        }
        // On détermine le joueur gagnant de la partie en fonction des points
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
        System.out.println("Le " + joueurGagnant.nomJoueur + " remporte la partie!");
    }

    public void melangeDuDeck(Deck deckTarot) {
        Collections.shuffle(deckTarot.deckComplet);
        // On récupère le deck mélangé dans une variable
        deckMelangePartie = deckTarot.deckComplet;
    }

    // On sélectionne le type de chien aléatoirement et en fonction du nombre de
    // joueurs
    // pour respecter les règles bonus. Le chien peut être de 2-6-10 pour 4 joueurs
    // et de 3-6-9
    // pour 3 joueurs.
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
