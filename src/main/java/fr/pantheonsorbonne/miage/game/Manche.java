package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manche {

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
    Map<String, Integer> misesHM = new HashMap<String, Integer>() {
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
    public List<Carte> pliDefense;
    public List<Carte> pliAttaque;
    Pli pli;

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
        joueurs.add(joueur4);
        joueurs.add(joueur1);
        joueur1.miseJoueur = null;
        joueur2.miseJoueur = null;
        joueur3.miseJoueur = null;
        joueur4.miseJoueur = null;
        deckMelange = dM;
        typeChien = tC;
        while (misesPartie.get(joueur1) == 0 && misesPartie.get(joueur2) == 0 && misesPartie.get(joueur3) == 0
                && misesPartie.get(joueur4) == 0) {
            distributionQuatreJoueurs();
            misesJoueurs();
        }
        int maxValue = 0;
        for (Joueur i : joueurs) {
            if (misesPartie.get(i) > maxValue) {
                maxValue = misesPartie.get(i);
                attaquant = i;
            }
        }
        for (Joueur i : joueurs) {
            if (i == attaquant) {
                i.roleJoueur = "Attaquant";
            }
        }
        lancementDesPlis();
        calculPointsDeManche();
    }

    public Manche(Joueur j1, Joueur j2, Joueur j3, List<Carte> dM, int tC) {
        joueur1 = j1;
        joueur2 = j2;
        joueur3 = j3;
        misesPartie.put(joueur1, 0);
        misesPartie.put(joueur2, 0);
        misesPartie.put(joueur3, 0);
        joueurs.add(joueur2);
        joueurs.add(joueur3);
        joueurs.add(joueur1);
        deckMelange = dM;
        typeChien = tC;
        while (misesPartie.get(joueur1) == 0 && misesPartie.get(joueur2) == 0 && misesPartie.get(joueur3) == 0) {
            distributionTroisJoueurs();
            misesJoueurs();
        }
        int maxValue = 0;
        for (Joueur i : joueurs) {
            if (misesPartie.get(i) > maxValue) {
                maxValue = misesPartie.get(i);
                attaquant = i;
            }
        }
        for (Joueur i : joueurs) {
            if (i == attaquant) {
                i.roleJoueur = "Attaquant";
            }
        }

        lancementDesPlis();
        calculPointsDeManche();
    }

    public void lancementDesPlis() {
        while (!joueur1.mainJoueur.isEmpty()) {
            pli = new Pli(joueurs, pliAttaque, pliDefense);
        }
    }

    public void misesJoueurs() {
        for (Joueur i : joueurs) {
            i.miseJoueur = joueurMise(i, i.mainJoueur);
            misesHM.put(i.miseJoueur, 1);
            if (i.miseJoueur == GC) {
                misesPartie.put(i, 4);
            } else if (i.miseJoueur.equals(GS)) {
                misesPartie.put(i, 3);
            } else if (i.miseJoueur.equals(G)) {
                misesPartie.put(i, 2);
            } else if (i.miseJoueur.equals(P2)) {
                misesPartie.put(i, 1);
            }
        }
    }

    public String joueurMise(Joueur joueur, List<Carte> main) {
        int[] cartesSpeciales = nombreCartesSpeciales(joueur, main);
        int countBout = cartesSpeciales[0];
        int countRoi = cartesSpeciales[1];
        int countAtout = cartesSpeciales[2];
        if (countAtout >= 15) {
            return GC;
        }
        if (countBout == 3) {
            if (countRoi >= 2) {
                if (countAtout >= 6) {
                    return GC;
                } else if (misesHM.get(GS) != 1) {
                    return GS;
                }
            }
            if (countAtout >= 6 && misesHM.get(GS) != 1) {
                return GS;
            } else if (misesHM.get(GS) != 1 && misesHM.get(G) != 1) {
                return G;
            }
        }
        if (countBout == 2) {
            if (countRoi == 4) {
                if (countAtout >= 9) {
                    return GC;
                }
                if (countAtout >= 6 && misesHM.get(GS) != 1) {
                    return GS;
                }
                if (countAtout >= 4 && misesHM.get(GS) != 1 && misesHM.get(G) != 1) {
                    return G;
                } else if (misesHM.get(G) != 1 && misesHM.get(P2) != 1) {
                    return P2;
                }
            }
            if (countRoi == 3) {
                if (countAtout >= 13) {
                    return GC;
                }
                if (countAtout >= 9 && misesHM.get(GS) != 1) {
                    return GS;
                }
                if (countAtout >= 6 && misesHM.get(GS) != 1 && misesHM.get(G) != 1) {
                    return G;
                }
                if (countAtout >= 4 && misesHM.get(G) != 1 && misesHM.get(P2) != 1) {
                    return P2;
                } else {
                    return P1;
                }
            }
            if (countRoi == 2) {
                if (countAtout >= 12 && misesHM.get(GS) != 1) {
                    return GS;
                }
                if (countAtout >= 6 && misesHM.get(GS) != 1 && misesHM.get(G) != 1) {
                    return G;
                }
                if (countAtout >= 4 && misesHM.get(G) != 1 && misesHM.get(P2) != 1) {
                    return P2;
                } else {
                    return P1;
                }
            }
            if (countRoi == 1) {
                if (countAtout >= 12 && misesHM.get(GS) != 1 && misesHM.get(G) != 1) {
                    return G;
                } else if (countAtout >= 6 && misesHM.get(G) != 1 && misesHM.get(P2) != 1) {
                    return P2;
                } else {
                    return P1;
                }
            }
            if (countRoi == 0) {
                if (countAtout >= 12 && misesHM.get(G) != 1 && misesHM.get(P2) != 1) {
                    return P2;
                } else {
                    return P1;
                }
            }
        } else if (countBout == 1) {
            if (countRoi == 4) {
                if (countAtout >= 10 && misesHM.get(GS) != 1 && misesHM.get(G) != 1) {
                    return G;
                }
                if (countAtout >= 6 && misesHM.get(G) != 1 && misesHM.get(P2) != 1) {
                    return P2;
                } else {
                    return P1;
                }
            }
            if (countRoi == 3) {
                if (countAtout >= 12 && misesHM.get(GS) != 1 && misesHM.get(G) != 1) {
                    return G;
                } else if (countAtout >= 8 && misesHM.get(G) != 1 && misesHM.get(P2) != 1) {
                    return P2;
                } else {
                    return P1;
                }
            }
            if (countRoi == 2) {
                if (countAtout >= 10 && misesHM.get(G) != 1 && misesHM.get(P2) != 1) {
                    return P2;
                } else {
                    return P1;
                }
            }
            if (countRoi == 1) {
                if (countAtout >= 13 && misesHM.get(G) != 1 && misesHM.get(P2) != 1) {
                    return P2;
                } else {
                    return P1;
                }
            }
            if (countRoi == 0) {
                return P1;
            }
        }
        if (countBout == 0) {
            if (countRoi == 4) {
                if (countAtout >= 12 && misesHM.get(GS) != 1 && misesHM.get(G) != 1) {
                    return G;
                }
                if (countAtout >= 9 && misesHM.get(G) != 1 && misesHM.get(P2) != 1) {
                    return P2;
                } else {
                    return P1;
                }
            }
            if (countRoi == 3) {
                if (countAtout >= 12 && misesHM.get(G) != 1 && misesHM.get(P2) != 1) {
                    return P2;
                } else {
                    return P1;
                }
            }
            if (countRoi <= 2) {
                return P1;
            }
        }
        return P1;
    }

    public int[] nombreCartesSpeciales(Joueur joueur, List<Carte> main) {
        int[] cartesSpeciales = new int[3];
        for (int i = 0; i < main.size(); i++) {
            if (joueur.mainJoueur.get(i).carteBout == true) {
                cartesSpeciales[0]++;
            }
            if (joueur.mainJoueur.get(i).typeCarte == "Roi") {
                cartesSpeciales[1]++;
            }
            if (joueur.mainJoueur.get(i).typeCarte == "Atout") {
                cartesSpeciales[2]++;
            }
        }
        return cartesSpeciales;
    }

    public void distributionQuatreJoueurs() {
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
}
// Test git push etc
// 2 eme test
// test abel
// 2eme test abel
// 2eme test noam
// 3eme test abel