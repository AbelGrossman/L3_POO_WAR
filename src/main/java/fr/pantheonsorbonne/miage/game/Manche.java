package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Manche {

    Random rand = new Random();
    Joueur joueur1;
    Joueur joueur2;
    Joueur joueur3;
    Joueur joueur4;
    List<Carte> deckMelange;
    int typeChien;
    List<Carte> packetChien = new ArrayList<>();
    String GC = "Garde Contre";
    String GS = "Garde Sans";
    String G = "Garde";
    String P2 = "Petite";
    String P1 = "Passer";
    List<Joueur> joueurs = new ArrayList<>();
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
    public static List<Carte> pliDefense = new ArrayList<>();
    public static List<Carte> pliAttaque = new ArrayList<>();
    Pli pli;

    public Manche(Joueur j1, Joueur j2, Joueur j3, Joueur j4, List<Carte> dM, int tC) {
        joueur1 = j1;
        joueur2 = j2;
        joueur3 = j3;
        joueur4 = j4;
        misesPartie.put(joueur1, 0);
        misesPartie.put(joueur2, 0);
        misesPartie.put(joueur3, 0);
        joueurs.add(joueur2);
        joueurs.add(joueur3);
        joueurs.add(joueur1);
        deckMelange = dM;
        typeChien = tC;
        if (joueur4 != null) {
            misesPartie.put(joueur4, 0);
            joueurs.add(joueur4);
            while (misesPartie.get(joueur1) == 0 && misesPartie.get(joueur2) == 0 && misesPartie.get(joueur3) == 0
                    && misesPartie.get(joueur4) == 0) {
                joueur1.mainJoueur.clear();
                joueur2.mainJoueur.clear();
                joueur3.mainJoueur.clear();
                joueur4.mainJoueur.clear();
                distributionQuatreJoueurs();
                for (int i = 0; i < joueur1.mainJoueur.size(); i++) {
                    System.out.println(joueur1.mainJoueur.get(i).getNom());
                }
                System.out.println("fin de distribution");
                System.out.println();
                misesJoueurs();
                System.out.println(misesPartie);
            }
        } else {
            while (misesPartie.get(joueur1) == 0 && misesPartie.get(joueur2) == 0 && misesPartie.get(joueur3) == 0) {
                joueur1.mainJoueur.clear();
                joueur2.mainJoueur.clear();
                joueur3.mainJoueur.clear();
                distributionTroisJoueurs();
                for (int i = 0; i < joueur1.mainJoueur.size(); i++) {
                    System.out.println(joueur1.mainJoueur.get(i).getNom());
                }
                System.out.println("fin de distribution");
                System.out.println();
                misesJoueurs();
                System.out.println(misesPartie);
            }
        }
        int maxValue = 0;
        for (Joueur i : joueurs) {
            if (misesPartie.get(i) > maxValue) {
                maxValue = misesPartie.get(i);
                attaquant = i;
            }
        }
        gestionDuChien();
        // lancementDesPlis();
        // calculPointsDeManche();
    }

    public void lancementDesPlis() {
        while (!joueur1.mainJoueur.isEmpty()) {
            System.out.println(joueur1.mainJoueur.size());
            new Pli(joueurs);
            System.out.println("check");
        }
        System.out.println("finished?");
    }

    public void misesJoueurs() {
        for (Joueur i : joueurs) {
            i.miseJoueur = joueurMise(i, i.mainJoueur);
            conteurDeMises.put(i.miseJoueur, 1);
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

    public String joueurMise(Joueur joueur, List<Carte> main) {
        int[] cartesSpeciales = nombreCartesSpeciales(joueur, main);
        int countBout = cartesSpeciales[0];
        int countRoi = cartesSpeciales[1];
        int countAtout = cartesSpeciales[2];
        if (conteurDeMises.get(GC) != 1) {
            if (countAtout > 16) {
                System.out.println("case 1!");
                return GC;
            }
            if (conteurDeMises.get(GS) != 1) {
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
            if (countBout == 3) {
                if (countRoi >= 1) {
                    if (countAtout >= 6) {
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
        Collections.rotate(deckMelange, deckMelange.size() / (rand.nextInt(4) + 1));
        int countJ1 = 0;
        int countJ2 = 0;
        int countJ3 = 0;
        int countJ4 = 0;
        int i = 0;
        int count = 0;
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
            if (count % 2 == 0 && count <= 12) {
                if (typeChien == 6 || typeChien == 10) {
                    packetChien.add(deckMelange.get(i++));
                }
                if (typeChien == 2 && count % 8 == 0) {
                    packetChien.add(deckMelange.get(i++));
                }
            }
            if (count == 20)
                break;
        }

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

    public void calculPointsDeManche() {

    }

    public void gestionDuChien() {
        for (Joueur i : joueurs) {
            if (i != attaquant) {
                Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getType));
                Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getValeur));
            }
            if (i == attaquant) {
                i.roleJoueur = "Attaquant";
                i.mainJoueur.addAll(packetChien);
                Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getType));
                Collections.sort(i.mainJoueur, Comparator.comparing(Carte::getValeur));
                getCouleursDansMain(i);
                defausserCartes(i);
            }
        }
    }

    public void getCouleursDansMain(Joueur joueur) {
        int k = 0;
        for (Carte i : joueur.mainJoueur) {
            joueur.nombreCarteCouleurDansMain.put(i.typeCarte, k++);
            switch (i.typeCarte) {
                case "Trefle":
                    joueur.treflesDansMain.put(i.valeurCarte, true);
                    break;
                case "Pique":
                    joueur.piquesDansMain.put(i.valeurCarte, true);
                    break;
                case "Coeur":
                    joueur.coeurDansMain.put(i.valeurCarte, true);
                    break;
                case "Carreau":
                    joueur.carreauxDansMain.put(i.valeurCarte, true);
                    break;
                default:
                    break;
            }
        }
    }

    public void defausserCartes(Joueur joueur) {
        List<Map.Entry<String, Integer>> listeDeTri = new ArrayList<>(joueur.nombreCarteCouleurDansMain.entrySet());
        // Sort the list based on values (quantities)
        listeDeTri.sort(Map.Entry.comparingByValue());
        // Create an array of card types ordered by occurrences
        String[] typeCartesOrdonne = listeDeTri.stream()
                .map((Map.Entry<String, Integer> entry) -> entry.getKey())
                .toArray(String[]::new);
        List<Carte> main = joueur.mainJoueur;
        for (int i = 0; i < main.size(); i++) {
            if (!main.get(i).typeCarte.equals("Atout") && !main.get(i).typeCarte.equals("Excuse")
                    && main.get(i).valeurCarte != 14) {
                if (main.get(i).typeCarte.equals(typeCartesOrdonne[0])
                        && joueur.nombreCarteCouleurDansMain.get(main.get(i).typeCarte) < packetChien.size()) {
                    if (main.get(i).valeurCarte <= 10) {
                        pliAttaque.add(main.get(i));
                        joueur.mainJoueur.remove(i);
                    } else {
                        switch (main.get(i).valeurCarte) {
                            case 11:
                                switch (main.get(i).typeCarte) {
                                    case "Trefle":
                                        break;
                                    case "Pique":
                                        break;
                                    case "Ceur":
                                        break;
                                    case "Carreau":
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case 12:
                                switch (main.get(i).typeCarte) {
                                    case "Trefle":
                                        break;
                                    case "Pique":
                                        break;
                                    case "Ceur":
                                        break;
                                    case "Carreau":
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case 13:
                                switch (main.get(i).typeCarte) {
                                    case "Trefle":
                                        break;
                                    case "Pique":
                                        break;
                                    case "Ceur":
                                        break;
                                    case "Carreau":
                                        break;
                                    default:
                                        break;
                                }
                                break;

                        }

                    }

                }
            }
            if (i >= 1 && main.get(i).typeCarte.equals(main.get(i - 1).typeCarte)) {
                typeCartesOrdonne[0] = typeCartesOrdonne[1];
                typeCartesOrdonne[1] = typeCartesOrdonne[2];
                typeCartesOrdonne[2] = typeCartesOrdonne[3];
            }
            if (pliAttaque.size() == packetChien.size()) {
                break;
            }
        }
    }
}