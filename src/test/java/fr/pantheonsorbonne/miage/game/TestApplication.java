package fr.pantheonsorbonne.miage.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//Test de l'exactitude du nombre de joueurs pour une partie à 4 et 3 joueurs
class TestApplication {
    Deck deckTest = new Deck();
    Partie partieTest = new Partie(4, deckTest, 3);
    Partie partieTest2 = new Partie(3, deckTest, 50);

    @Test
    void checkPartie() {
        assertEquals(4, partieTest.joueurs.size());
        assertEquals(3, partieTest2.joueurs.size());
    }
}
