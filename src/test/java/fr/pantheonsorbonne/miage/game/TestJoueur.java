package fr.pantheonsorbonne.miage.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class TestJoueur {
    List<Carte> mainTest = new ArrayList<>();
    Deck deckTest = new Deck();
    Joueur joueur1Test;
    Joueur joueur2Test;

    // Vérification du bon respect des conditions selon lesquelles des cartes sont
    // jouees,
    // Aléatoirements ou stratégiquement
    @Test
    void createSampleHand() {
        Carte plusPetitTrefle = new Carte("Atout", 1, "1 d'Atout", true, 5);
        for (Carte c : deckTest.deckComplet) {
            mainTest.add(c);
            if (mainTest.size() == 8) {
                break;
            }
            if (c.nomCarte.equals("1 de Trefle")) {
                plusPetitTrefle = c;
            }
        }
        Carte carteATester = new Carte("Coeur", 9, "9 de Coeur", false, 1);
        mainTest.add(carteATester);
        Carte carteGagnanteTest = new Carte("Trefle", 10, "10 de Trefle", false, 1);
        joueur1Test = new Joueur("joueur1Test", mainTest, 0, 1);
        joueur2Test = new Joueur("joueur2Test", mainTest, 0, 2);
        joueur2Test.roleJoueur = "Attaquant";
        joueur1Test.jouerCarte("Trefle", carteGagnanteTest, false, joueur2Test);
        //Avec la première stratégie, une carte au hasard de la même couleur que celle demandée sera jouee
        //car les conditions sont vérifiées ici
        assertEquals(true, joueur1Test.mainJoueur.contains(carteATester));
        joueur2Test.roleJoueur = "Defenseur";
        joueur1Test.roleJoueur = "Attaquant";
        joueur2Test.jouerCarte("Trefle", carteGagnanteTest, false, joueur1Test);
        //Avecc la seconde stratégie, uniquement la plus petite des cartes de la même couleur sera jouee
        assertEquals(false, joueur2Test.mainJoueur.contains(plusPetitTrefle));
    }

}
