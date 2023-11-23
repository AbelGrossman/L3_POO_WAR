package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Deck {

    List<Carte> deckComplet = new ArrayList<>();

    public Deck() {
        ajouterAuDeck();
    }

    public void ajouterAuDeck() {
        String[] typesDeCartes = { "Trefle", "Pique", "Carreau", "Coeur" };
        String type = null;
        String nom = null;
        boolean bout = false;
        int points = 1;
        for (int i = 0; i < 4; i++) {
            type = typesDeCartes[i];
            points = 1;
            for (int j = 1; j < 15; j++) {
                switch (j) {
                    case 11:
                        nom = "Valet de " + type;
                        points = 2;
                        break;
                    case 12:
                        nom = "Cavalier de " + type;
                        points = 3;
                        break;
                    case 13:
                        nom = "Dame de " + type;
                        points = 4;
                        break;
                    case 14:
                        nom = "Roi de " + type;
                        points = 5;
                        break;
                    default:
                        nom = j + " de " + type;
                        break;
                }
                Carte nouvelleCarte = new Carte(type, j, nom, bout, points);
                deckComplet.add(nouvelleCarte);
            }
        }
        for (int i = 1; i < 22; i++) {
            bout = false;
            points = 1;
            if (i == 1 || i == 21) {
                bout = true;
                points = 5;
            }
            nom = i + " d'Atout";
            Carte nouvelleCarte = new Carte("Atout", i, nom, bout, points);
            deckComplet.add(nouvelleCarte);
        }
        Carte nouvelleCarte = new Carte("Excuse", 22, "Excuse", bout, 5);
        deckComplet.add(nouvelleCarte);
    }
}
