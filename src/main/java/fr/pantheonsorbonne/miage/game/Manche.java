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
    List<Carte> deckMelange;
    int typeChien;
    List<Carte> packetChien = new ArrayList<>(); // Cartes du chien

    // Différents types de mises
    String GC = "Garde Contre";
    String GS = "Garde Sans";
    String G = "Garde";
    String P2 = "Petite";
    String P1 = "Passer";

    // Liste des joueurs
    List<Joueur> joueurs = new ArrayList<>();

    // Conteur pour chaque type de mise
    Map<String, Integer> conteurDeMises = new HashMap<String, Integer>() {
        {
            put(GC, 0);
            put(GS, 0);
            put(G, 0);
            put(P2, 0);
            put(P1, 0);
        }
    };

    // Listes des mises de chaque joueurs
    Map<Joueur, Integer> misesPartie = new HashMap<>();
    // Definit le joueur remportant la mise
    Joueur attaquant;
    public static List<Carte> pliDefense = new ArrayList<>(); // Plis des defenseurs
    public static List<Carte> pliAttaque = new ArrayList<>(); // Plis de l'attaquant

    int nombrePointsTotal;
    int scoreARealiser; // Nombre de points à faire (en fonction du nombre de bouts)
    int countBoutAttaquant; // Nombre de bouts dans la main de l'attaquant
    Boolean excusePassee = false; // Indique si l'Excuse a été jouée

    // Les attributs suivants permettent de gérer les différents cas de l'excuse
    // (don d'une carte si le pli est perdu)
    public static Boolean donALAttaque = false; // Indique si le don va être fait à l'attaquant
    public static Boolean donALaDefense = false; // Indique si le don va être fait à la défense
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
        // joueurs ajoutés dans l'ordre de jeu (le joueur 2 joue en premier, le joueur
        // 1, le donneur, en dernier)
        joueurs.add(joueur2);
        joueurs.add(joueur3);
        if (joueur4 != null) {
            joueurs.add(joueur4);
        }
        joueurs.add(joueur1);
        deckMelange = dM;
        typeChien = tC;

        // Distribution des cartes en fonction du nombre de joueurs
        if (joueur4 != null) {
            distributionQuatreJoueurs();
        } else {
            distributionTroisJoueurs();
        }
        misesJoueurs();
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
            // On récupère la mise d'un joueur
            i.miseJoueur = joueurMise(i, i.mainJoueur);
            // On l'ajoute dans le conteur
            conteurDeMises.put(i.miseJoueur, 1);
            // on assigne une valeur à chaque joueur, pour pouvoir désigner un vainqueur
            switch (i.miseJoueur) {
                case "Garde Contre":
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
        // Tableau stockant le nombre de cartes spéciales
        int[] cartesSpeciales = nombreCartesSpeciales(joueur, main);
        int countBout = cartesSpeciales[0];
        int countRoi = cartesSpeciales[1];
        int countAtout = cartesSpeciales[2];
        // Si une mise a déjà été annoncée, on ne peut que miser plus haut. D'où la
        // condition !=1.
        // Si la mise est Garde Contre, plus personne ne peut miser
        if (conteurDeMises.get(GC) != 1) {
            // Condition pour la garde contre : avoir 16 atouts (pas une règle officielle
            // mais définit par nous même grace à notre expérience du jeu)
            if (countAtout > 16) {
                return GC;
            }
            if (conteurDeMises.get(GS) != 1) {
                // Pareil ici mais pour la Garde Sans etc...
                if (countAtout >= 14) {
                    return GS;
                }
                if (conteurDeMises.get(G) != 1) {
                    if (countAtout >= 12) {
                        return G;
                    }
                    if (countAtout >= 10 && conteurDeMises.get(P2) != 1) {
                        return P2;
                    }
                }
            }
            // Conditions du type de prise basées sur le nombre de bouts, le nombre de rois
            // et le nombre d'atouts
            // Meme logique jusqu'a la ligne 457 nous avons adapté les prises en fonction du
            // nombre de bouts de rois et d'atouts qui sont les cartes les plus importantes
            // pour gagner une partie
            if (countBout == 3) {
                if (countRoi >= 1) {
                    if (countAtout >= 6) {
                        // Si on a les 3 bouts, 1 roi ou plus et 6 atouts ou plus on fait une Garde
                        // Contre,
                        // ainsi de suite...
                        return GC;
                    } else if (conteurDeMises.get(GS) != 1) {
                        return GS;
                    }
                }
                // Toujours vérifier qu'une mise plus ou aussi grande n'a pas encore été
                // faite...
                if (conteurDeMises.get(GS) != 1) {
                    if (countAtout >= 6) {
                        return GS;
                    } else if (conteurDeMises.get(G) != 1) {
                        return G;
                    }
                }
            }
            if (countBout == 2) {
                if (countRoi == 4) {
                    if (countAtout >= 9) {
                        // Si on a 2 bouts, les 4 rois et 9 atouts ou plus on fait une Garde Contre
                        return GC;
                    }
                    if (conteurDeMises.get(GS) != 1) {
                        if (countAtout >= 6) {
                            return GS;
                        }
                        if (conteurDeMises.get(G) != 1) {
                            if (countAtout >= 4) {
                                return G;
                            }
                            if (conteurDeMises.get(P2) != 1) {
                                return P2;
                            }
                        }
                    }
                }
                if (countRoi == 3) {
                    if (countAtout >= 12) {
                        return GC;
                    }
                    if (conteurDeMises.get(GS) != 1) {
                        if (countAtout >= 8) {
                            return GS;
                        }
                        if (conteurDeMises.get(G) != 1) {
                            if (countAtout >= 6) {
                                return G;
                            }
                            if (countAtout >= 4 && conteurDeMises.get(P2) != 1) {
                                return P2;
                            }
                        }
                    }
                }
                if (conteurDeMises.get(GS) != 1) {
                    if (countRoi == 2) {
                        if (countAtout >= 10) {
                            return GS;
                        }
                        if (conteurDeMises.get(G) != 1) {
                            if (countAtout >= 6) {
                                return G;
                            }
                            if (countAtout >= 4 && conteurDeMises.get(P2) != 1) {
                                return P2;
                            }
                        }
                    }
                    if (conteurDeMises.get(G) != 1) {
                        if (countRoi == 1) {
                            if (countAtout >= 8) {
                                return G;
                            } else if (countAtout >= 4 && conteurDeMises.get(P2) != 1) {
                                return P2;
                            }
                        }
                        if (countRoi == 0) {
                            if (countAtout >= 6 && conteurDeMises.get(P2) != 1) {
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
                            return G;
                        }
                        if (countAtout >= 4 && conteurDeMises.get(P2) != 1) {
                            return P2;
                        }
                    }
                    if (countRoi == 3) {
                        if (countAtout >= 8) {
                            return G;
                        } else if (countAtout >= 6 && conteurDeMises.get(P2) != 1) {
                            return P2;
                        }
                    }
                    if (countRoi == 2) {
                        if (countAtout >= 8 && conteurDeMises.get(P2) != 1) {
                            return P2;
                        }
                    }
                }
                if (countBout == 0 && countRoi == 4) {
                    if (countAtout >= 10) {
                        return G;
                    }
                    if (countAtout >= 8 && conteurDeMises.get(P2) != 1) {
                        return P2;
                    }
                }
            }
        }
        return P1;
    }

    // Méthode pour compter le nombre de cartes spéciales
    // Une carte "spéciale" est soit un bout, soit un roi, soit un atout, dans cet
    // ordre ici
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
        return cartesSpeciales;
    }

    // Distribution si 4 joueurs
    public void distributionQuatreJoueurs() {
        // Rotation aléatoire du deck pour le couper. Aléatoire pour avoir de la
        // diversité de jeu
        // et surtout éviter des boucles infinies ou les joueurs ne font que passer
        Collections.rotate(deckMelange, deckMelange.size() / (rand.nextInt(4) + 1));
        // conteur du nombre de cartes de chaque joueurs
        int countJ1 = 0;
        int countJ2 = 0;
        int countJ3 = 0;
        int countJ4 = 0;
        // indices pour parcourir le deck
        int i = 0;
        // indice pour compter le nombre d'itération de la boucle infinie
        int count = 0;
        // Boucle infinie (break pour en sortir)
        for (;;) {
            // Distribution des cartes 3 par 3
            // On distribue le J2 en premier
            if (countJ1 == countJ2) {
                for (int j = 0; j < 3; j++) {
                    // Ajoute la carte du deck mélangé à la main du deuxième joueur et incrémente
                    // l'indice pour la prochaine carte
                    joueur2.mainJoueur.add(deckMelange.get(i++));
                }
                countJ2++;
            }
            // Si le J2 a été distributé, on passe au J3
            else if (countJ2 - 1 == countJ3) {
                for (int j = 0; j < 3; j++) {
                    joueur3.mainJoueur.add(deckMelange.get(i++));
                }
                // On met des cartes dans le chien aléatoirement
                if (typeChien == 10 && count != 1) {
                    packetChien.add(deckMelange.get(i++));
                }
                countJ3++;
            }
            // Idem
            else if (countJ3 - 1 == countJ4) {
                for (int j = 0; j < 3; j++) {
                    joueur4.mainJoueur.add(deckMelange.get(i++));
                }
                countJ4++;
            }
            // Le joueur 1 en dernier
            else if (countJ4 - 1 == countJ1) {
                for (int j = 0; j < 3; j++) {
                    joueur1.mainJoueur.add(deckMelange.get(i++));
                }
                countJ1++;
            }
            count++;
            // Distribution du chien au bout d'un certain nombre de cartes jouees
            if (count % 2 == 0 && count <= 12) {
                // Si chien de 6 ou 10
                if (typeChien == 6 || typeChien == 10) {
                    packetChien.add(deckMelange.get(i++));
                }
                // Si chien de 2 (condition à ajouter car peu de cartes dans le chien)
                if (typeChien == 2 && count % 6 == 0) {
                    packetChien.add(deckMelange.get(i++));
                }
            }
            // Condition de sortie de la boucle, 20 tours de boucle
            // avec 15 cartes par joueurs (donc 60 cartes au total)
            if (count == 20)
                break;
        }

        // Le chien a été entièrement distribué, mais la distribution des dernières
        // cartes
        // ne peut pas se faire 3 par 3 si le chien ne vaut pas 6 (règles bonus)
        // En fonction du type de chien:
        if (typeChien == 10) {
            // 2 par 2, 17 cartes par joueurs
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
            // 3 par 3 (classique), 18 cartes par joueurs
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
            // 3 par 3
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
            // puis 1 par 1, 19 cartes par joueurs
            joueur2.mainJoueur.add(deckMelange.get(i++));
            joueur3.mainJoueur.add(deckMelange.get(i++));
            joueur4.mainJoueur.add(deckMelange.get(i++));
            joueur1.mainJoueur.add(deckMelange.get(i));
        }
    }

    // Meme logique que pour la distributionQuatreJoueurs
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
            // Arrêt au bout de 21 tours de boucles, les joueurs ont 21 cartes.
            if (count == 21)
                break;
        }
        if (typeChien == 9) {
            // 2 par 2, 23 cartes total
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
            // 3 par 3, 24 total
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
            // 3 par 3
            while (i < 69) {
                joueur2.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 72) {
                joueur3.mainJoueur.add(deckMelange.get(i++));
            }
            while (i < 75) {
                joueur1.mainJoueur.add(deckMelange.get(i++));
            }
            // puis 1 par 1, 25 cartes total
            joueur2.mainJoueur.add(deckMelange.get(i++));
            joueur3.mainJoueur.add(deckMelange.get(i++));
            joueur1.mainJoueur.add(deckMelange.get(i));
        }
    }

    // méthode de gestion du chien
    public void gestionDuChien() {

        // Vérifie la mise du joueur attaquant pour déterminer le traitement du chien
        // car on ne regarde pas le chien si GC ou GS
        if (attaquant.miseJoueur.equals("Petite") || attaquant.miseJoueur.equals("Garde")) {
            for (Joueur i : joueurs) {
                if (i != attaquant) {
                    // Trie les mains des défenseurs par valeur et type de carte
                    Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getValeur));
                    Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getType));
                }
                // Trie la main du joueur attaquant pour choisir quelles cartes mettrent dans le
                // chien
                if (i == attaquant) {
                    // On change le rôle à Attaquant
                    i.roleJoueur = "Attaquant";
                    // On ajoute le chien à sa main puis on trie
                    i.mainJoueur.addAll(packetChien);
                    Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getValeur));
                    Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getType));
                    getCouleursDansMain(i);
                    i.mainJoueur = defausserCartes(i);
                } else {
                    i.roleJoueur = "Defenseur";
                }
            }
        } else if (attaquant.miseJoueur.equals("Garde Sans")) {
            pliAttaque.addAll(packetChien);
        } else if (attaquant.miseJoueur.equals("Garde Contre")) {
            pliDefense.addAll(packetChien);
        }
    }

    public void getCouleursDansMain(Joueur joueur) {
        // Initialise les compteurs pour chaque couleur
        int t = 0; // trefle
        int p = 0; // pique
        int c = 0; // coeur
        int c2 = 0; // carreau
        for (Carte i : joueur.mainJoueur) {
            // Switch pour traiter chaque type de carte
            switch (i.typeCarte) {
                case "Trefle":
                    // Enregistre la valeur de la carte dans la HashMap pour savoir si telle carte
                    // est dans la main
                    joueur.treflesDansMain.put(i.valeurCarte, true);
                    // met à jour le compteur de couleur
                    joueur.nombreCarteCouleurDansMain.put(i.typeCarte, t++);
                    break;
                // meme logique pour pique et ainsi de suite
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

    // Méthode permettant de choisir une liste de carte à défausser suite à la prise
    // du chien
    public List<Carte> defausserCartes(Joueur attaquant) {
        // Obtient une liste ordonnée des couleurs dans la main de l'attaquant, en
        // fonction du nombre
        // d'occurence (dans l'ordre corissant, à l'exception des atouts).
        // Il est stratégiquement intéressant de se débarasser d'abord des couleurs
        // qu'on a le moins
        List<String> typeCartesOrdonne = attaquant.nombreCarteCouleurDansMain.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("Atout"))
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<Carte> mainAttaquant = attaquant.mainJoueur; // Récupère la main de l'attaquant
        int i = 0;
        int nombrePassages = 0; // Compteur de passages si aucune autre cas de défausse n'est vérifié
        String couleurActuelle = mainAttaquant.get(i).getType();
        // Continue la défausse tant que l'attaquant n'a pas défaussé autant de cartes
        // qu'il n'en a prises
        while (pliAttaque.size() != packetChien.size()) {
            if (i < mainAttaquant.size()) {
                couleurActuelle = mainAttaquant.get(i).getType(); // Mets à jour la couleur actuelle
            }
            // on change la couleur avec le moins d'élements après avoir parcouru la main
            // une première fois
            if (i - 1 == mainAttaquant.size()) {
                // Reorganise la liste des couleurs pour passer à la suivante
                typeCartesOrdonne.set(0, typeCartesOrdonne.get(1));
                typeCartesOrdonne.set(1, typeCartesOrdonne.get(2));
                typeCartesOrdonne.set(2, typeCartesOrdonne.get(3));
                // On incrémente le nombre de parcours de la main
                nombrePassages++;
                // On réinitialise pour reparcourir
                i = 0;
                continue;
            }
            // Condition pour défausser une carte si elle correspond aux critères
            if (i < mainAttaquant.size() && !couleurActuelle.equals("Atout") // pas un atout
                    && mainAttaquant.get(i).valeurCarte != 14 // pas un roi
                    && couleurActuelle.equals(typeCartesOrdonne.get(0))
                    && pliAttaque.size() < packetChien.size()) {
                // Vérifie si la carte est une tête et si elle peut être défaussée
                if (mainAttaquant.get(i).valeurCarte > 10) {
                    // Switch pour gérer les différents cas des têtes
                    // On conserve les têtes uniquement si on a une suite (Valet Cavalier Dame Roi,
                    // Dame Roi..)
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
            // //Si on a parcouru la main 4 fois, on passe à une autre méthode de défausse
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
                    if (i > mainAttaquant.size()) {
                        if (!mainAttaquant.get(i).typeCarte.equals("Excuse")
                                && mainAttaquant.get(i).valeurCarte != 21 && mainAttaquant.get(i).valeurCarte != 1) {
                            pliAttaque.add(mainAttaquant.get(i));
                            mainAttaquant.remove(i);
                            if (packetChien.size() == pliAttaque.size()) {
                                break;
                            }
                            continue;
                        }
                    }
                }
            }
            i++; // Passe à la carte suivante dans la main
        }
        return mainAttaquant;
    }

    // Méthode pour gérer le don d'une carte suite au placement de l'excuse
    public void gestionDuDon() {
        Carte carteDonnee = null;
        // Si le don est à l'attaque et qu'il n'y a pas d'Excuse à la fin du pli
        if (donALAttaque && !excuseALaFin) {
            // Sélectionne une carte de la défense de façon aléatoire jusqu'à obtenir une
            // carte de valeur 1
            do {
                carteDonnee = pliDefense.get(rand.nextInt(pliDefense.size()));
            } while (carteDonnee.pointsCarte != 1);

            // Retire la carte donnée du pli de l'attaque et l'ajoute au pli de l'attaque
            pliAttaque.remove(carteDonnee);
            pliAttaque.add(carteDonnee);

            // Si le don est à la defense
        } else if (donALaDefense) {
            do {
                carteDonnee = pliAttaque.get(rand.nextInt(pliAttaque.size()));
            } while (carteDonnee.pointsCarte != 1);
            pliAttaque.remove(carteDonnee);
            pliDefense.add(carteDonnee);
        }
    }

    // methode pour calculer les points de manche
    public void calculPointsDeManche(List<Carte> pliAttaque) {
        List<Carte> pliComptagePoints = new ArrayList<>();
        int taillePliAttaque = pliAttaque.size();
        while (pliComptagePoints.size() < taillePliAttaque) {
            int nombreDe1 = 0;
            int nombreDeSpeciales = 0;
            // On compte le nombre de carte de valeur 1 et les cartes spéciales
            for (Carte i : pliAttaque) {
                if (i.getPoints() == 1) {
                    nombreDe1++;
                } else {
                    nombreDeSpeciales++;
                }
            }
            // Si le pli ne contient plus de cartes avec 1 point ou de cartes
            // spéciales, on ajoute les cartes au comptage sans conditions
            if (nombreDe1 == 0 || nombreDeSpeciales == 0) {
                for (Carte i : pliAttaque) {
                    pliComptagePoints.add(i);
                }
            }
            // On ajoute une carte avec 1 point au comptage suivie d'une carte spéciale
            // valant plus d'un point
            // C'est la manière naturelle de compter au Tarot, pour obtenir le maximum de
            // points
            // On ajoute une carte au pliComptage si la carte précedente dans le pliAttaque
            // est de valeur différent
            // (1 si spéciale, spéciale si 1)
            for (int i = 0; i < pliAttaque.size(); i++) {
                if (i > 0 && (pliAttaque.get(i - 1).getPoints() == 1 || pliAttaque.get(i).getPoints() == 1)
                        && pliAttaque.get(i - 1).getPoints() != pliAttaque.get(i).getPoints()) {
                    pliComptagePoints.add(pliAttaque.get(i));
                }
            }
            // On retire la carte ajoutée du pliAttaque pour permettre une nouvelle
            // succession de cartes
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
        int difference = 0; // Différence de points entre l'attaquant et le score à faire
        int mise = 0;
        int victoireDefense = 1; // variable de victoire/défaite de la défense (-1 si l'attaque gagne)
        int nombrePointsPliAttaquant = 0;
        // Multiplicateur de score en fonction de la mise
        System.out.print("Le " + attaquant.nomJoueur + " est attaquant. Il remporte la mise avec une ");
        switch (attaquant.miseJoueur) {
            case "Garde Contre":
                System.out.println("Garde Contre");
                mise = 6;
                break;
            case "Garde Sans":
                System.out.println("Garde Sans");
                mise = 4;
                break;
            case "Garde":
                System.out.println("Garde");
                mise = 2;
                break;
            case "Petite":
                System.out.println("Petite");
                mise = 1;
                break;
            default:
                mise = 1;
                break;
        }
        System.out.println("Il doit faire un score de " + scoreARealiser);
        // Calcul du nombre total de points dans le pli de l'attaque
        // On compte le cartes 2 par 2, en prenant en compte uniquement la meilleure
        // carte.
        // D'où la nécessité de la méthode précedente.
        for (int i = 0; i < pliComptagePoints.size(); i += 2) {
            int max = pliComptagePoints.get(i).getPoints();
            if (i < pliComptagePoints.size() - 1 && (max < pliComptagePoints.get(i + 1).getPoints())) {
                max = pliComptagePoints.get(i + 1).getPoints();
            }
            nombrePointsPliAttaquant += max;
        }
        System.out.println("L'attaquant réalise un score de " + nombrePointsPliAttaquant);
        // Calcul de la différence de points par rapport au score à réaliser
        difference = Math.abs(nombrePointsPliAttaquant - scoreARealiser);
        System.out.println("Différence de score: " + difference);

        if (nombrePointsPliAttaquant >= scoreARealiser) {
            victoireDefense = -1;
            System.out.println("L'attaquant gagne!");
        } else {
            System.out.println("L'attaquant chute!");
        }
        // Calcul du nombre total de points gagner suite à la fin de la manche
        nombrePointsTotal = ((25 + difference) * mise);
        System.out.println("Nombre de points total: " + nombrePointsTotal);
        // Attribution des points aux joueurs en fonction du résultat
        for (Joueur i : joueurs) {
            if (i != attaquant) {
                //Les point de la défense est négatif si l'attaquant gagne
                i.pointsJoueur += victoireDefense * nombrePointsTotal;
                if (bonusPetitAuBoutDefenseur) {
                    System.out.println("Bonus petit au bout defenseur");
                    i.pointsJoueur += 10 * mise;
                }
                if (bonusPetitAuBoutAttaquant) {
                    i.pointsJoueur -= 10 * mise;
                }

            } else {
                //Le score de l'attaquant vaut le score de la défense fois -1 fois le nombre de défenseurs
                attaquant.pointsJoueur += -1 * victoireDefense * nombrePointsTotal * (joueurs.size() - 1);
                if (bonusPetitAuBoutAttaquant) {
                    System.out.println("Bonus petit au bout attaquant");
                    attaquant.pointsJoueur += 10 * mise * (joueurs.size() - 1);
                }
                if (bonusPetitAuBoutDefenseur) {
                    attaquant.pointsJoueur -= 10 * mise * (joueurs.size() - 1);
                }

            }
        }

    }
}