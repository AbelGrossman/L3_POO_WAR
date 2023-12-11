package fr.pantheonsorbonne.miage.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestDeck {
    Deck deckTest = new Deck();
    int carreauTest = 0;
    int coeurTest = 0;
    int piqueTest = 0;
    int trefleTest = 0;
    int atoutTest = 0;
    int excuseTest = 0;
    int test1 = 0;
    int test2 = 0;
    int test3 = 0;
    int test4 = 0;
    int test5 = 0;
    int test6 = 0;
    int test7 = 0;
    int test8 = 0;
    int test9 = 0;
    int test10 = 0;
    int test11 = 0;
    int test12 = 0;
    int test13 = 0;
    int test14 = 0;
    int test15 = 0;
    int test16 = 0;
    int test17 = 0;
    int test18 = 0;
    int test19 = 0;
    int test20 = 0;
    int test21 = 0;
    int test22 = 0;

    @Test
    void checkDeck() {
        assertEquals(78, deckTest.deckComplet.size());
    }

    //Vérfication du nombre d'elements (Atout compris) de chaque couleur dans le deck, 
    //et du nombre d'élements de chaque valeur dans le deck (1,4,Roi,Dame,21...)
    @Test
    void checkQuantiteCouleursEtValeurs() {
        for (Carte c : deckTest.deckComplet) {
            switch (c.getType()) {
                case "Atout":
                    atoutTest++;
                    break;
                case "Trefle":
                    trefleTest++;
                    break;
                case "Carreau":
                    carreauTest++;
                    break;
                case "Pique":
                    piqueTest++;
                    break;
                case "Coeur":
                    coeurTest++;
                    break;
                case "Excuse":
                    excuseTest++;
                    break;
            }
            switch (c.getValeur()) {
                case 1:
                    test1++;
                    break;
                case 2:
                    test2++;
                    break;
                case 3:
                    test3++;
                    break;
                case 4:
                    test4++;
                    break;
                case 5:
                    test5++;
                    break;
                case 6:
                    test6++;
                    break;
                case 7:
                    test7++;
                    break;
                case 8:
                    test8++;
                    break;
                case 9:
                    test9++;
                    break;
                case 10:
                    test10++;
                    break;
                case 11:
                    test11++;
                    break;
                case 12:
                    test12++;
                    break;
                case 13:
                    test13++;
                    break;
                case 14:
                    test14++;
                    break;
                case 15:
                    test15++;
                    break;
                case 16:
                    test16++;
                    break;
                case 17:
                    test17++;
                    break;
                case 18:
                    test18++;
                    break;
                case 19:
                    test19++;
                    break;
                case 20:
                    test20++;
                    break;
                case 21:
                    test21++;
                    break;
                case 22:
                    test22++;
                    break;
            }
        }
        assertEquals(14, trefleTest);
        assertEquals(14, carreauTest);
        assertEquals(14, piqueTest);
        assertEquals(14, coeurTest);
        assertEquals(1, excuseTest);
        assertEquals(21, atoutTest);
        assertEquals(5, test1);
        assertEquals(5, test2);
        assertEquals(5, test3);
        assertEquals(5, test4);
        assertEquals(5, test5);
        assertEquals(5, test6);
        assertEquals(5, test7);
        assertEquals(5, test8);
        assertEquals(5, test9);
        assertEquals(5, test10);
        assertEquals(5, test11);
        assertEquals(5, test12);
        assertEquals(5, test13);
        assertEquals(5, test14);
        assertEquals(1, test15);
        assertEquals(1, test16);
        assertEquals(1, test17);
        assertEquals(1, test18);
        assertEquals(1, test19);
        assertEquals(1, test20);
        assertEquals(1, test21);
        assertEquals(1, test22);
    }
}
