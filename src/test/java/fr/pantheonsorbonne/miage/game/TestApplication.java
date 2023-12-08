package fr.pantheonsorbonne.miage.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestApplication {
    Deck deckTest = new Deck();
    Partie partieTest = new Partie(4, deckTest, 3);

    @Test
    void checkPartie() {
        assertEquals(4, partieTest.joueurs.size());
    }
}
