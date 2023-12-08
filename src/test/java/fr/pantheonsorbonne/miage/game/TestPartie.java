package fr.pantheonsorbonne.miage.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestPartie {
    Deck deckTest = new Deck();
    Partie partieTest1 = new Partie(3, deckTest, 3);
    Partie partieTest2 = new Partie(4, deckTest, 4);
    Joueur joueurGagnantTest;

    @Test
    void checkVainqueur() {
        if (partieTest1.joueur1Partie.pointsJoueur > partieTest1.joueur2Partie.pointsJoueur
                && partieTest1.joueur1Partie.pointsJoueur > partieTest1.joueur3Partie.pointsJoueur) {
            assertEquals(partieTest1.joueurGagnant, partieTest1.joueur1Partie);
        } else if (partieTest1.joueur2Partie.pointsJoueur > partieTest1.joueur1Partie.pointsJoueur
                && partieTest1.joueur2Partie.pointsJoueur > partieTest1.joueur3Partie.pointsJoueur) {
            assertEquals(partieTest1.joueurGagnant, partieTest1.joueur2Partie);
        } else if (partieTest1.joueur3Partie.pointsJoueur > partieTest1.joueur1Partie.pointsJoueur
                && partieTest1.joueur3Partie.pointsJoueur > partieTest1.joueur2Partie.pointsJoueur) {
            assertEquals(partieTest1.joueurGagnant, partieTest1.joueur3Partie);
        }
        if (partieTest2.joueur1Partie.pointsJoueur > partieTest2.joueur2Partie.pointsJoueur
                && partieTest2.joueur1Partie.pointsJoueur > partieTest2.joueur3Partie.pointsJoueur
                && partieTest2.joueur1Partie.pointsJoueur > partieTest2.joueur4Partie.pointsJoueur) {
            assertEquals(partieTest2.joueurGagnant, partieTest2.joueur1Partie);
        } else if (partieTest2.joueur2Partie.pointsJoueur > partieTest2.joueur1Partie.pointsJoueur
                && partieTest2.joueur2Partie.pointsJoueur > partieTest2.joueur3Partie.pointsJoueur
                && partieTest2.joueur2Partie.pointsJoueur > partieTest2.joueur4Partie.pointsJoueur) {
            assertEquals(partieTest2.joueurGagnant, partieTest2.joueur2Partie);
        } else if (partieTest2.joueur3Partie.pointsJoueur > partieTest2.joueur1Partie.pointsJoueur
                && partieTest2.joueur3Partie.pointsJoueur > partieTest2.joueur2Partie.pointsJoueur
                && partieTest2.joueur3Partie.pointsJoueur > partieTest2.joueur4Partie.pointsJoueur) {
            assertEquals(partieTest2.joueurGagnant, partieTest2.joueur3Partie);
        } else if (partieTest2.joueur4Partie.pointsJoueur > partieTest2.joueur1Partie.pointsJoueur
                && partieTest2.joueur4Partie.pointsJoueur > partieTest2.joueur2Partie.pointsJoueur
                && partieTest2.joueur4Partie.pointsJoueur > partieTest2.joueur3Partie.pointsJoueur) {
            assertEquals(partieTest2.joueurGagnant, partieTest2.joueur4Partie);
        }

    }

}
