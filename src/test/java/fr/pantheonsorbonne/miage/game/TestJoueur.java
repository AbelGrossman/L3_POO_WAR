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

    @Test
    void createSampleHand() {
        for (Carte c : deckTest.deckComplet) {
            mainTest.add(c);
            if (mainTest.size() == 8) {
                break;
            }
        }
        Carte carteATester = new Carte("Coeur", 9, "9 de Coeur", false, 1);
        mainTest.add(carteATester);
        Carte carteGagnanteTest = new Carte("Trefle", 10, "10 de Trefle", false, 1);
        joueur1Test = new Joueur("joueur1Test", mainTest, 0, 1);
        joueur2Test = new Joueur("joueur2Test", mainTest, 0, 2);
        joueur1Test.jouerCarte("Trefle", carteGagnanteTest, false);
        assertEquals(true, joueur1Test.mainJoueur.contains(carteATester));
        joueur2Test.jouerCarte("Trefle", carteGagnanteTest, false);
        assertEquals(false, joueur2Test.mainJoueur.contains(carteATester));
    }

}
