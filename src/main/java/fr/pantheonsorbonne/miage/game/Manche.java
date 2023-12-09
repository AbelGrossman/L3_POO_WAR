package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;

public class Manche {

    Random rand = new Random();
    Joueur joueur1;
    Joueur joueur2;
    Joueur joueur3;
    Joueur joueur4;
    List<Carte> deckMelange; // deck après le mélange
    int typeChien;
    List<Carte> packetChien = new ArrayList<>(); // Cartes du chien

    // Différents types de prises
    String GC = "Garde Contre";
    String GS = "Garde Sans";
    String G = "Garde";
    String P2 = "Petite";
    String P1 = "Passer";
    List<Joueur> joueurs = new ArrayList<>();

    // Compteur pour chaque type de prise.
    Map<String, Integer> conteurDeMises = new HashMap<String, Integer>() {
        {
            put(GC, 0);
            put(GS, 0);
            put(G, 0);
            put(P2, 0);
            put(P1, 0);
        }
    };
    Map<Joueur, Integer> misesPartie = new HashMap<>();
    Joueur attaquant;
    public static List<Carte> pliDefense = new ArrayList<>(); // Plis des defenseurs
    public static List<Carte> pliAttaque = new ArrayList<>(); // Plis de l'attaquant
    String[] couleurVerifiees = new String[4];
    int nombrePointsTotal;
    int scoreARealiser;
    int countBoutAttaquant; // Nombre de bouts dans la main de l'attaquant
    Boolean excusePassee = false; // Indique si l'Excuse a été jouée pendant un pli

    // Les attributs suivants permettent de gérer les différents cas de l'excuse
    // (don d'une carte si le pli est perdu)
    public static Boolean donALAttaque = false; // Indique si le don a été fait à l'attaquant
    public static Boolean donALaDefense = false; // Indique si le don a été fait à la défense
    Boolean excuseALaFin = false; // Indique si l'excuse a été jouée à la fin de la partie
    Boolean bonusPetitAuBoutAttaquant = false; // Indique si le bonus "Petit au bout" a été obtenu par l'attaquant
    Boolean bonusPetitAuBoutDefenseur = false; // Indique si le bonus "Petit au bout" a été obtenu par la défense

    // Constructeur de la classe Manche avec les 4 joueurs, le deck mélangé ainsi
    // que le type de chien
    public Manche(Joueur j1, Joueur j2, Joueur j3, Joueur j4, List<Carte> dM, int tC) {
        joueur1 = j1;
        joueur2 = j2;
        joueur3 = j3;
        joueur4 = j4;
        misesPartie.put(joueur1, 0);
        misesPartie.put(joueur2, 0);
        misesPartie.put(joueur3, 0);
        misesPartie.put(joueur4, 0);
        joueurs.add(joueur2);
        joueurs.add(joueur3);
        if (joueur4 != null) {
            joueurs.add(joueur4);
        }
        joueurs.add(joueur1);
        for (Joueur j : joueurs) {
            System.out.println("joueur: " + j.nomJoueur);
        }
        deckMelange = dM;
        typeChien = tC;

        // Distribution des cartes en fonction du nombre de joueurs
        if (joueur4 != null) {
            distributionQuatreJoueurs();
            System.out.println("fin de distribution");
            misesJoueurs();
        } else {
            distributionTroisJoueurs();
            System.out.println("fin de distribution");
            misesJoueurs();
        }
        // Vérifie si un joueur prend, si oui on lance la méthode suiteDeManche
        if (!(misesPartie.get(joueur1) == 0 && misesPartie.get(joueur2) == 0
                && misesPartie.get(joueur3) == 0 && misesPartie.get(joueur4) == 0)) {
            suiteDeManche();
        }

    }

    public void suiteDeManche() {
        int maxValue = 0;
        // Verifie quel joueur a fait la mise la plus haute
        for (Joueur i : joueurs) {
            if (misesPartie.get(i) > maxValue) {
                maxValue = misesPartie.get(i);
                attaquant = i; // le joueur ayant la mise la plus haute devient l'attaquant
            }
        }
        System.out.println("mise de l'attaquant: " + attaquant.miseJoueur);

        // Score à réaliser en fonction du nombre de bouts
        countBoutAttaquant = nombreCartesSpeciales(attaquant, attaquant.mainJoueur)[0];
        switch (countBoutAttaquant) {
            case 0:
                scoreARealiser = 56;
                break;
            case 1:
                scoreARealiser = 51;
                break;
            case 2:
                scoreARealiser = 41;
                break;
            case 3:
                scoreARealiser = 36;
                break;
            default:
                break;
        }
        // Initialisation des plis et gestion du chien
        pliAttaque.clear();
        pliDefense.clear();
        gestionDuChien();
        donALAttaque = false;
        donALaDefense = false;
        lancementDesPlis();
        gestionDuDon();
        calculPointsDeManche(pliAttaque);
    }

    public void lancementDesPlis() {
        List<Joueur> swapJoueurs = joueurs;
        // Boucle jusqu'à ce que tous les joueurs aient joué toutes leurs cartes
        while (!joueur1.mainJoueur.isEmpty()) {
            System.out.println(joueur1.mainJoueur.size());

            // Création d'un pli
            Pli pli = new Pli(joueurs, excusePassee);
            excusePassee = pli.excusePassee;
            // La je comprends rien mdrr c'est pour le changement de carte dont il avait
            // parlé ?
            // Gestion de l'Excuse et réorganisation des joueurs pour le prochain pli
            for (int i = 0; i < joueurs.size(); i++) {
                if (joueurs.get(i) == pli.joueurGagnant) {
                    Joueur swap1;
                    Joueur swap2;
                    Joueur swap3;
                    switch (i) {
                        case 1:
                            swap1 = joueurs.get(0);
                            if (joueur4 != null) {
                                joueurs.set(0, joueurs.get(1));
                                joueurs.set(1, joueurs.get(2));
                                joueurs.set(2, joueurs.get(3));
                                joueurs.set(3, swap1);
                            } else {
                                joueurs.set(0, joueurs.get(1));
                                joueurs.set(1, joueurs.get(2));
                                joueurs.set(2, swap1);
                            }
                            break;
                        case 2:
                            swap1 = joueurs.get(0);
                            swap2 = joueurs.get(1);
                            if (joueur4 != null) {
                                joueurs.set(0, joueurs.get(2));
                                joueurs.set(1, joueurs.get(3));
                                joueurs.set(2, swap1);
                                joueurs.set(3, swap2);
                            } else {
                                joueurs.set(0, joueurs.get(2));
                                joueurs.set(1, swap1);
                                joueurs.set(2, swap2);
                            }
                            break;
                        case 3:
                            swap1 = joueurs.get(0);
                            swap2 = joueurs.get(1);
                            swap3 = joueurs.get(2);
                            joueurs.set(0, joueurs.get(3));
                            joueurs.set(1, swap1);
                            joueurs.set(2, swap2);
                            joueurs.set(3, swap3);
                            break;
                        default:
                            break;
                    }
                }
            }
            // Gestion des cartes spéciales et des bonus "Petit au bout"
            if (joueur1.mainJoueur.size() == 1) {
                for (Carte c : pli.cartesJouees) {
                    if (c.nomCarte.equals("1 d'Atout")) {
                        if (pli.joueurGagnant.roleJoueur.equals("Attaquant")
                                && pli.petitMap.get("1 d'Atout").roleJoueur.equals("Attaquant")) {
                            bonusPetitAuBoutAttaquant = true;
                        } else if (pli.joueurGagnant.roleJoueur.equals("Defenseur")
                                && pli.petitMap.get("1 d'Atout").roleJoueur.equals("Defenseur")) {
                            // Vérification des conditions pour le bonus "Petit au bout" pour l'attaquant
                            if (pli.joueurGagnant.roleJoueur.equals("Attaquant")
                                    && pli.petitMap.get("1 d'Atout").roleJoueur.equals("Attaquant")) {
                                bonusPetitAuBoutAttaquant = true;
                                // Vérification des conditions pour le bonus "Petit au bout" pour la defense
                            } else if (pli.joueurGagnant.roleJoueur.equals("Defenseur")
                                    && pli.petitMap.get("1 d'Atout").roleJoueur.equals("Defenseur")) {
                                bonusPetitAuBoutDefenseur = true;
                            }
                            // Rajoute un commentaire ici en gros je pense cest quand tu fais don de
                            // l'excuse donc tu enleves la carte du pli ?
                            if (c.nomCarte.equals("Excuse")) {
                                excuseALaFin = true;
                                Boolean inAttaque = false;
                                // Recherche de l'Excuse dans le pli d'attaque
                                for (Carte d : pliAttaque) {
                                    if (d.getNom().equals("Excuse")) {
                                        pliAttaque.remove(d);
                                        pliDefense.add(d);
                                        inAttaque = true;
                                        break;
                                    }

                                }
                                // Si l'Excuse n'est pas dans le pli d'attaque, recherche dans le pli de défense
                                if (!inAttaque) {
                                    for (Carte d : pliDefense) {
                                        if (d.getNom().equals("Excuse")) {
                                            pliDefense.remove(d);
                                            pliAttaque.add(d);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                joueurs = swapJoueurs;
            }
        }
    }

    public void misesJoueurs() {
        for (Joueur i : joueurs) {
            i.miseJoueur = joueurMise(i, i.mainJoueur);
            conteurDeMises.put(i.miseJoueur, 1);
            switch (i.miseJoueur) {
                case "Garde Contre":
                    // ici la value est le multiplicateur du score (donc plus la mise est haute plus
                    // le multiplicateur est haut)
                    misesPartie.put(i, 4);
                    break;
                case "Garde Sans":
                    misesPartie.put(i, 3);
                    break;
                case "Garde":
                    misesPartie.put(i, 2);
                    break;
                case "Petite":
                    misesPartie.put(i, 1);
                    break;
                default:
                    misesPartie.put(i, 0);
                    break;
            }
        }
    }

    // Fonction pour déterminer la mise d'un joueur en fonction de sa main
    public String joueurMise(Joueur joueur, List<Carte> main) {
        int[] cartesSpeciales = nombreCartesSpeciales(joueur, main);
        int countBout = cartesSpeciales[0];
        int countRoi = cartesSpeciales[1];
        int countAtout = cartesSpeciales[2];
        // !=1 verifie qu'une Garde Contre n'a pas deja été annoncée
        if (conteurDeMises.get(GC) != 1) {
            // Condition pour la garde contre : avoir 16 atouts (pas une règle officielle
            // mais définit par nous même grace à notre expérience du jeu)
            if (countAtout > 16) {
                System.out.println("case 1!");
                return GC;
            }
            if (conteurDeMises.get(GS) != 1) {
                // Pareil ici mais pour la Garde Sans etc...
                if (countAtout >= 14) {
                    System.out.println("case 2!");
                    return GS;
                }
                if (conteurDeMises.get(G) != 1) {
                    if (countAtout >= 12) {
                        System.out.println("case 3!");
                        return G;
                    }
                    if (countAtout >= 10 && conteurDeMises.get(P2) != 1) {
                        System.out.println("case 4");
                        return P2;
                    }
                }
            }
            // Conditions du type de prise basées sur le nombre de bouts, le nombre de rois et le nombre d'atouts
            // Meme logique jusqu'a la ligne 457 nous avons adapté les prises en fonction du
            // nombre de bouts de rois et d'atouts qui sont les cartes les plus importantes pour gagner une partie
            if (countBout == 3) {
                if (countRoi >= 1) {
                    if (countAtout >= 6) {
                        //Si on a les 3 bouts, 1 roi ou plus et 6 atouts ou plus on fait une Garde Contre
                        System.out.println("case 5!");
                        return GC;
                    } else if (conteurDeMises.get(GS) != 1) {
                        System.out.println("case 6!");
                        return GS;
                    }
                }
                if (conteurDeMises.get(GS) != 1) {
                    if (countAtout >= 6) {
                        System.out.println("case 7!");
                        return GS;
                    } else if (conteurDeMises.get(G) != 1) {
                        System.out.println("case 8!");
                        return G;
                    }
                }
            }
            if (countBout == 2) {
                if (countRoi == 4) {
                    if (countAtout >= 9) {
                        // Si on a 2 bouts, les 4 rois et 9 atouts ou plus on fait une Garde Contre
                        System.out.println("case 9!");
                        return GC;
                    }
                    if (conteurDeMises.get(GS) != 1) {
                        if (countAtout >= 6) {
                            System.out.println("case 10!");
                            return GS;
                        }
                        if (conteurDeMises.get(G) != 1) {
                            if (countAtout >= 4) {
                                System.out.println("case 11!");
                                return G;
                            }
                            if (conteurDeMises.get(P2) != 1) {
                                System.out.println("case 12!");
                                return P2;
                            }
                        }
                    }
                }
                if (countRoi == 3) {
                    if (countAtout >= 12) {
                        System.out.println("case 13!");
                        return GC;
                    }
                    if (conteurDeMises.get(GS) != 1) {
                        if (countAtout >= 8) {
                            System.out.println("case 14!");
                            return GS;
                        }
                        if (conteurDeMises.get(G) != 1) {
                            if (countAtout >= 6) {
                                System.out.println("case 15!");
                                return G;
                            }
                            if (countAtout >= 4 && conteurDeMises.get(P2) != 1) {
                                System.out.println("case 16!");
                                return P2;
                            }
                        }
                    }
                }
                if (conteurDeMises.get(GS) != 1) {
                    if (countRoi == 2) {
                        if (countAtout >= 10) {
                            System.out.println("case 17!");
                            return GS;
                        }
                        if (conteurDeMises.get(G) != 1) {
                            if (countAtout >= 6) {
                                System.out.println("case 18!");
                                return G;
                            }
                            if (countAtout >= 4 && conteurDeMises.get(P2) != 1) {
                                System.out.println("case 19!");
                                return P2;
                            }
                        }
                    }
                    if (conteurDeMises.get(G) != 1) {
                        if (countRoi == 1) {
                            if (countAtout >= 8) {
                                System.out.println("case 20!");
                                return G;
                            } else if (countAtout >= 4 && conteurDeMises.get(P2) != 1) {
                                System.out.println("case 21!");
                                return P2;
                            }
                        }
                        if (countRoi == 0) {
                            if (countAtout >= 6 && conteurDeMises.get(P2) != 1) {
                                System.out.println("case 22!");
                                return P2;
                            }
                        }
                    }
                }
            }
            if (conteurDeMises.get(G) != 1 && conteurDeMises.get(GS) != 1) {
                if (countBout == 1) {
                    if (countRoi == 4) {
                        if (countAtout >= 6) {
                            System.out.println("case 23!");
                            return G;
                        }
                        if (countAtout >= 4 && conteurDeMises.get(P2) != 1) {
                            System.out.println("case 24!");
                            return P2;
                        }
                    }
                    if (countRoi == 3) {
                        if (countAtout >= 8) {
                            System.out.println("case 25!");
                            return G;
                        } else if (countAtout >= 6 && conteurDeMises.get(P2) != 1) {
                            System.out.println("case 26!");
                            return P2;
                        }
                    }
                    if (countRoi == 2) {
                        if (countAtout >= 8 && conteurDeMises.get(P2) != 1) {
                            System.out.println("case 27!");
                            return P2;
                        }
                    }
                }
                if (countBout == 0 && countRoi == 4) {
                    if (countAtout >= 10) {
                        System.out.println("case 28!");
                        return G;
                    }
                    if (countAtout >= 8 && conteurDeMises.get(P2) != 1) {
                        System.out.println("case 29!");
                        return P2;
                    }
                }
            }
        }
        System.out.println("case 30!");
        return P1;
    }

    //Méthode pour compter le nombre de cartes spéciales (une carte "spéciale" est soit un bout, soit un roi, soit un atout)
    public int[] nombreCartesSpeciales(Joueur joueur, List<Carte> main) {
        int[] cartesSpeciales = new int[3];
        for (int i = 0; i < main.size(); i++) {
            if (joueur.mainJoueur.get(i).carteBout) {
                cartesSpeciales[0]++;
            }
            if (joueur.mainJoueur.get(i).typeCarte.equals("Atout")) {
                cartesSpeciales[2]++;
            } else if (joueur.mainJoueur.get(i).valeurCarte == 14) {
                cartesSpeciales[1]++;
            }
        }
        System.out.println(
                "Bouts: " + cartesSpeciales[0] + " Rois: " + cartesSpeciales[1] + " Atouts: " + cartesSpeciales[2]);
        return cartesSpeciales;
    }

    public void distributionQuatreJoueurs() {
        // Rotation aléatoire du deck mélangé
        Collections.rotate(deckMelange, deckMelange.size() / (rand.nextInt(4) + 1));
        int countJ1 = 0;
        int countJ2 = 0;
        int countJ3 = 0;
        int countJ4 = 0;
        //indices pour parcourir le deck
        int i = 0;
        int count = 0;
        //Boucle infinie (break à la fin de la distribution)
        for (;;) {
            // Distribution des cartes aux joueurs en fonction du compteur
            if (countJ1 == countJ2) {
                for (int j = 0; j < 3; j++) {
                    //Ajoute la carte du deck mélangé à la main du deuxième joueur et incrémente l'indice pour la prochaine carte
                    joueur2.mainJoueur.add(deckMelange.get(i++));
                }
                countJ2++;
            } else if (countJ2 - 1 == countJ3) {
                for (int j = 0; j < 3; j++) {
                    joueur3.mainJoueur.add(deckMelange.get(i++));
                }
                if (typeChien == 10 && count != 1) {
                    packetChien.add(deckMelange.get(i++));
                }
                countJ3++;
            } else if (countJ3 - 1 == countJ4) {
                for (int j = 0; j < 3; j++) {
                    joueur4.mainJoueur.add(deckMelange.get(i++));
                }
                countJ4++;
            } else if (countJ4 - 1 == countJ1) {
                for (int j = 0; j < 3; j++) {
                    joueur1.mainJoueur.add(deckMelange.get(i++));
                }
                countJ1++;
            }
            count++;
            // Distribution du chien en fonction de typeChien
            if (count % 2 == 0 && count <= 12) {
                if (typeChien == 6 || typeChien == 10) {
                    packetChien.add(deckMelange.get(i++));
                }
                if (typeChien == 2 && count % 6 == 0) {
                    packetChien.add(deckMelange.get(i++));
                }
            }
            //Condition de sortie de la boucle,  20 cartes par joueur
            if (count == 20)
                break;
        }

        //Distribution des cartes restantes en fonction de typeChien
        if (typeChien == 10) {
            while (i < 72) {
                joueur2.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 74) {
                joueur3.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 76) {
                joueur4.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 78) {
                joueur1.mainJoueur.add(deckMelange.get(i++));
            }
        } else if (typeChien == 6) {
            while (i < 69) {
                joueur2.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 72) {
                joueur3.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 75) {
                joueur4.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 78) {
                joueur1.mainJoueur.add(deckMelange.get(i++));
            }
        } else if (typeChien == 2) {
            while (i < 65) {
                joueur2.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 68) {
                joueur3.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 71) {
                joueur4.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 74) {
                joueur1.mainJoueur.add(deckMelange.get(i++));
            }
            joueur2.mainJoueur.add(deckMelange.get(i++));
            joueur3.mainJoueur.add(deckMelange.get(i++));
            joueur4.mainJoueur.add(deckMelange.get(i++));
            joueur1.mainJoueur.add(deckMelange.get(i));
        }
    }

    //Meme logique que pour la distributionQuatreJoueurs
    public void distributionTroisJoueurs() {
        Collections.rotate(deckMelange, deckMelange.size() / (rand.nextInt(4) + 1));
        int countJ1 = 0;
        int countJ2 = 0;
        int countJ3 = 0;
        int count = 0;
        int i = 0;
        for (;;) {
            if (countJ1 == countJ2) {
                for (int j = 0; j < 3; j++) {
                    joueur2.mainJoueur.add(deckMelange.get(i++));
                }
                countJ2++;
            } else if (countJ2 - 1 == countJ3) {
                for (int j = 0; j < 3; j++) {
                    joueur3.mainJoueur.add(deckMelange.get(i++));
                }
                if (typeChien == 9 && count % 2 == 0) {
                    packetChien.add(deckMelange.get(i++));
                }
                countJ3++;
            } else if (countJ3 - 1 == countJ1) {
                for (int j = 0; j < 3; j++) {
                    joueur1.mainJoueur.add(deckMelange.get(i++));
                }
                countJ1++;
            }
            count++;
            if (count % 3 == 0 && count <= 18) {
                if (typeChien == 6 || typeChien == 9) {
                    packetChien.add(deckMelange.get(i++));
                }
                if (typeChien == 3 && count % 6 == 0) {
                    packetChien.add(deckMelange.get(i++));
                }
            }
            if (count == 21)
                break;
        }
        if (typeChien == 9) {
            while (i < 74) {
                joueur2.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 76) {
                joueur3.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 78) {
                joueur1.mainJoueur.add(deckMelange.get(i++));
            }
        } else if (typeChien == 6) {
            while (i < 72) {
                joueur2.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 75) {
                joueur3.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 78) {
                joueur1.mainJoueur.add(deckMelange.get(i++));
            }
        } else if (typeChien == 3) {
            while (i < 69) {
                joueur2.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 72) {
                joueur3.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 75) {
                joueur1.mainJoueur.add(deckMelange.get(i++));
            }
            joueur2.mainJoueur.add(deckMelange.get(i++));
            joueur3.mainJoueur.add(deckMelange.get(i++));
            joueur1.mainJoueur.add(deckMelange.get(i));
        }
    }

    //méthode de gestion du chien
    public void gestionDuChien() {
        System.out.println("type chien: " + typeChien + ". Taille packet chien: " + packetChien.size());

        //Vérifie la mise du joueur attaquant pour déterminer le traitement du chien car on ne regarde pas le chien si GC ou GS
        if (attaquant.miseJoueur.equals("Petite") || attaquant.miseJoueur.equals("Garde")) {
            for (Joueur i : joueurs) {
                if (i != attaquant) {
                    //Trie les mains des défenseurs par valeur et type de carte
                    Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getValeur));
                    Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getType));
                }
                //Trie la main du joueur attaquant pour choisir quelles cartes mettrent dans le chien
                if (i == attaquant) {
                    i.roleJoueur = "Attaquant";
                    i.mainJoueur.addAll(packetChien);
                    Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getValeur));
                    Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getType));
                    getCouleursDansMain(i);
                    i.mainJoueur = defausserCartes(i);
                } else {
                    i.roleJoueur = "Defenseur";
                }
            }
            System.out.println("taille pli attaquant après gestion du chien: " + pliAttaque.size() + ". Défense: "
                    + pliDefense.size());
        } else if (attaquant.miseJoueur.equals("Garde Sans")) {
            pliAttaque.addAll(packetChien);
            System.out.println("taille pli attaquant après gestion du chien: " + pliAttaque.size() + ". Défense: "
                    + pliDefense.size());
        } else if (attaquant.miseJoueur.equals("Garde Contre")) {
            pliDefense.addAll(packetChien);
            System.out.println("taille pli attaquant après gestion du chien: " + pliAttaque.size() + ". Défense: "
                    + pliDefense.size());
        }
    }

    public void getCouleursDansMain(Joueur joueur) {
        //Initialise les compteurs pour chaque couleur
        int t = 0;  //trefle
        int p = 0;  //pique
        int c = 0;  //coeur
        int c2 = 0; //carreau
        for (Carte i : joueur.mainJoueur) {
            //Switch pour traiter chaque type de carte
            switch (i.typeCarte) {
                //Enregistre la valeur de la carte dans le dictionnaire des Trefles et met à jour le compteur
                case "Trefle":
                    joueur.treflesDansMain.put(i.valeurCarte, true);
                    joueur.nombreCarteCouleurDansMain.put(i.typeCarte, t++);
                    break;
                //meme logique pour pique et ainsi de suite
                case "Pique":
                    joueur.piquesDansMain.put(i.valeurCarte, true);
                    joueur.nombreCarteCouleurDansMain.put(i.typeCarte, p++);
                    break;
                case "Coeur":
                    joueur.coeursDansMain.put(i.valeurCarte, true);
                    joueur.nombreCarteCouleurDansMain.put(i.typeCarte, c++);
                    break;
                case "Carreau":
                    joueur.carreauxDansMain.put(i.valeurCarte, true);
                    joueur.nombreCarteCouleurDansMain.put(i.typeCarte, c2++);
                    break;
                default:
                    break;
            }
        }
    }
    //Méthode permettant de choisir une liste de carte à défausser suite à la prise du chien
    public List<Carte> defausserCartes(Joueur attaquant) {
        //Obtient une liste ordonnée des couleurs dans la main de l'attaquant (à l'exception des atouts)
        List<String> typeCartesOrdonne = attaquant.nombreCarteCouleurDansMain.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("Atout"))
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<Carte> mainAttaquant = attaquant.mainJoueur; //Récupère la main de l'attaquant
        int i = 0;  //Indique si la couleur a déjà été traitée
        boolean couleurPassee = false; //Indique si la couleur a déjà été traitée
        int nombrePassages = 0; //Compteur de passages pour changer la couleur si nécessaire
        String couleurActuelle = mainAttaquant.get(i).getType();
        //Continue la défausse tant que le pli de l'attaquant n'est pas complet
        while (pliAttaque.size() != packetChien.size()) {
            if (i < mainAttaquant.size()) {
                couleurActuelle = mainAttaquant.get(i).getType(); // Met à jour la couleur actuelle
            }
            //condition pour changer la couleur si la précédente a été passée
            if (couleurPassee && !mainAttaquant.get(i - 1).getType().equals(typeCartesOrdonne.get(0))) {
                //Reorganise la liste des couleurs pour passer à la suivante
                typeCartesOrdonne.set(0, typeCartesOrdonne.get(1));
                typeCartesOrdonne.set(1, typeCartesOrdonne.get(2));
                typeCartesOrdonne.set(2, typeCartesOrdonne.get(3));
                couleurPassee = false;
                nombrePassages++;
                i = 0;
                continue;
            }
            //Condition pour défausser une carte si elle correspond aux critères
            if (i < mainAttaquant.size() && !couleurActuelle.equals("Atout")
                    && mainAttaquant.get(i).valeurCarte != 14
                    && couleurActuelle.equals(typeCartesOrdonne.get(0))
                    && pliAttaque.size() < packetChien.size()) {
                couleurPassee = true;
                //Vérifie si la carte est une tête et si elle peut être défaussée
                if (mainAttaquant.get(i).valeurCarte > 10) {
                    //Switch pour gérer les différents cas des têtes
                    switch (mainAttaquant.get(i).valeurCarte) {
                        case 11:
                            switch (couleurActuelle) {
                                case "Trefle":
                                    if (!(attaquant.treflesDansMain.get(12) && attaquant.treflesDansMain.get(13)
                                            && attaquant.treflesDansMain.get(14))) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                case "Pique":
                                    if (!(attaquant.piquesDansMain.get(12) && attaquant.piquesDansMain.get(13)
                                            && attaquant.piquesDansMain.get(14))) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                case "Coeur":
                                    if (!(attaquant.coeursDansMain.get(12) && attaquant.coeursDansMain.get(13)
                                            && attaquant.coeursDansMain.get(14))) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                case "Carreau":
                                    if (!(attaquant.carreauxDansMain.get(12) && attaquant.carreauxDansMain.get(13)
                                            && attaquant.carreauxDansMain.get(14))) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 12:
                            switch (couleurActuelle) {
                                case "Trefle":
                                    if (!(attaquant.treflesDansMain.get(13)
                                            && attaquant.treflesDansMain.get(14))) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                case "Pique":
                                    if (!(attaquant.piquesDansMain.get(13)
                                            && attaquant.piquesDansMain.get(14))) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                case "Coeur":
                                    if (!(attaquant.coeursDansMain.get(13)
                                            && attaquant.coeursDansMain.get(14))) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                case "Carreau":
                                    if (!(attaquant.carreauxDansMain.get(13)
                                            && attaquant.carreauxDansMain.get(14))) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 13:
                            switch (couleurActuelle) {
                                case "Trefle":
                                    if (!attaquant.treflesDansMain.get(14)) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                case "Pique":
                                    if (!attaquant.piquesDansMain.get(14)) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                case "Coeur":
                                    if (!attaquant.coeursDansMain.get(14)) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                case "Carreau":
                                    if (!attaquant.carreauxDansMain.get(14)) {
                                        pliAttaque.add(mainAttaquant.get(i));
                                        mainAttaquant.remove(i);
                                        continue;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    pliAttaque.add(mainAttaquant.get(i));
                    mainAttaquant.remove(i);
                    continue;
                }

            }
            //Quand toutes les couleurs sont passées on passse aux atouts
            if (nombrePassages == 4) {
                i = 0;
                for (;;) {
                    if (!mainAttaquant.get(i).typeCarte.equals("Atout")
                            && !mainAttaquant.get(i).typeCarte.equals("Excuse")
                            && mainAttaquant.get(i).valeurCarte < 14) {
                        pliAttaque.add(mainAttaquant.get(i));
                        mainAttaquant.remove(i);
                        if (packetChien.size() == pliAttaque.size()) {
                            break;
                        }
                        continue;
                    }
                    i++;
                }
            }
            i++;    //Passe à la carte suivante dans la main
        }
        return mainAttaquant;
    }

    //Méthode pour gérer le don d'une carte suite au placement de l'excuse
    public void gestionDuDon() {
        Carte carteDonnee = null;
        //Si le don est à l'attaque et qu'il n'y a pas d'Excuse à la fin du pli
        if (donALAttaque && !excuseALaFin) {
            System.out.println("Dont à l'attaque de: ");
            //Sélectionne une carte de la défense de façon aléatoire jusqu'à obtenir une carte de valeur 1
            do {
                carteDonnee = pliDefense.get(rand.nextInt(pliDefense.size()));
            } while (carteDonnee.pointsCarte != 1);
            System.out.println(carteDonnee.nomCarte);

            //Retire la carte donnée du pli de l'attaque et l'ajoute au pli de l'attaque
            pliAttaque.remove(carteDonnee);
            pliAttaque.add(carteDonnee);

        //Si le don est à la defense
        } else if (donALaDefense) {
            System.out.println("Dont à la defense de: ");
            do {
                carteDonnee = pliAttaque.get(rand.nextInt(pliAttaque.size()));
            } while (carteDonnee.pointsCarte != 1);
            System.out.println(carteDonnee.nomCarte);
            pliAttaque.remove(carteDonnee);
            pliDefense.add(carteDonnee);
        }
    }

    public void calculPointsDeManche(List<Carte> pliAttaque) {
        List<Carte> pliComptagePoints = new ArrayList<>();
        int taillePliAttaque = pliAttaque.size();
        while (pliComptagePoints.size() < taillePliAttaque) {
            int nombreDe1 = 0;
            int nombreDeSpeciales = 0;
            //On compte le nombre de carte de valeur 1 et les cartes spéciales
            for (Carte i : pliAttaque) {
                if (i.getPoints() == 1) {
                    nombreDe1++;
                } else {
                    nombreDeSpeciales++;
                }
            }
            //Si le pli ne contient que des cartes avec 1 point ou que des cartes spéciales, on ajoute toutes les cartes au comptage
            if (nombreDe1 == 0 || nombreDeSpeciales == 0) {
                for (Carte i : pliAttaque) {
                    pliComptagePoints.add(i);
                }
            }
            //On ajoute les cartes spéciales avec 1 point au comptage
            for (int i = 0; i < pliAttaque.size(); i++) {
                if (i > 0 && (pliAttaque.get(i - 1).getPoints() == 1 || pliAttaque.get(i).getPoints() == 1)
                        && pliAttaque.get(i - 1).getPoints() != pliAttaque.get(i).getPoints()) {
                    pliComptagePoints.add(pliAttaque.get(i));
                }
            }
            for (Carte i : pliComptagePoints) {
                for (Carte j : pliAttaque) {
                    if (i == j) {
                        pliAttaque.remove(i);
                        break;
                    }
                }
            }
        }
        // Appel de la méthode pour le comptage final des points
        comptageDesPoints(pliComptagePoints);
    }

    public void comptageDesPoints(List<Carte> pliComptagePoints) {
        int difference = 0; //Différence de points entre l'attaquant et le score à faire
        int mise = 0;
        int victoireDefense = 1;    //variable de victoire/défaite de la défense (-1 si l'attaque gagne)
        int nombrePointsPliAttaquant = 0;
        switch (attaquant.miseJoueur) {
            case "Garde Contre":
                System.out.println("garde contre");
                mise = 6;
                break;
            case "Garde Sans":
                System.out.println("garde sans");
                mise = 4;
                break;
            case "Garde":
                System.out.println("garde");
                mise = 2;
                break;
            case "Petite":
                System.out.println("petite");
                mise = 1;
                break;
            default:
                mise = 1;
                break;
        }
        // Calcul du nombre total de points dans le pli de l'attaque
        for (int i = 0; i < pliComptagePoints.size(); i += 2) {
            int max = pliComptagePoints.get(i).getPoints();
            if (i < pliComptagePoints.size() - 1 && (max < pliComptagePoints.get(i + 1).getPoints())) {
                max = pliComptagePoints.get(i + 1).getPoints();
            }
            nombrePointsPliAttaquant += max;
        }
        System.out.println("nombre de points dans le pli attaquant: " + nombrePointsPliAttaquant);
        System.out.println("Score à réaliser: " + scoreARealiser);

         //Calcul de la différence de points par rapport au score à réaliser
        difference = Math.abs(nombrePointsPliAttaquant - scoreARealiser);
        System.out.println("Différence de score: " + difference);
        
        if (nombrePointsPliAttaquant >= scoreARealiser) {
            victoireDefense = -1;
            System.out.println("L'attaquant, qui est le " + attaquant.nomJoueur + ", gagne!");
        } else {
            System.out.println("L'attaquant, qui est le " + attaquant.nomJoueur + ", chute!");
        }
        //Calcul du nombre total de points gagner suite à la fin de la manche
        nombrePointsTotal = ((25 + difference) * mise);
        System.out.println("nombre de points total: " + nombrePointsTotal);
        //Attribution des points aux joueurs en fonction du résultat
        for (Joueur i : joueurs) {
            if (i != attaquant) {
                i.pointsJoueur += victoireDefense * nombrePointsTotal;
                if (bonusPetitAuBoutDefenseur) {
                    System.out.println("bonus petit au bout defenseur");
                    i.pointsJoueur += 10 * mise;
                }
                if (bonusPetitAuBoutAttaquant) {
                    i.pointsJoueur -= 10 * mise;
                }

            } else {
                attaquant.pointsJoueur += -1 * victoireDefense * nombrePointsTotal * (joueurs.size() - 1);
                if (bonusPetitAuBoutAttaquant) {
                    System.out.println("bonus petit au bout attaquant");
                    attaquant.pointsJoueur += 10 * mise * (joueurs.size() - 1);
                }
                if (bonusPetitAuBoutDefenseur) {
                    attaquant.pointsJoueur -= 10 * mise * (joueurs.size() - 1);
                }

            }
        }

    }
}