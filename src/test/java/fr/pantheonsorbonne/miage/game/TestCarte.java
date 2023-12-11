package fr.pantheonsorbonne.miage.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestCarte {
    Deck deckTest = new Deck();

    //Vérification du nombre de points que les cartes spécialent au Tarot valent
    @Test
    void checkValue() {
        for (Carte c : deckTest.deckComplet) {
            if (c.getNom().contains("Roi")) {
                assertEquals(5, c.getPoints());
            }
            if (c.getNom().contains("Dame")) {
                assertEquals(4, c.getPoints());
            }
            if (c.getNom().contains("Cavalier")) {
                assertEquals(3, c.getPoints());
            }
            if (c.getNom().contains("Valet")) {
                assertEquals(2, c.getPoints());
            }
            if (c.getNom().equals("Excuse")) {
                assertEquals(5, c.getPoints());
            }
            if (c.getNom().equals("21 d'Atout")) {
                assertEquals(5, c.getPoints());
            }
            if (c.getNom().equals("1 d'Atout")) {
                assertEquals(5, c.getPoints());
            }
        }
    }
}
