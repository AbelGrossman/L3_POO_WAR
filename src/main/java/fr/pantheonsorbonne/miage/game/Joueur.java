package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

public class Joueur {
    public String nomJoueur;
    public List<Carte> mainJoueur;
    public int pointsJoueur;
    public String miseJoueur;
    public int niveauJoueur; // Niveau d'intelligence du bot: 1 pour "dumb", 2 pour "normal", 3 pour "smart".
    public List<Carte> pliJoueur;
    public String roleJoueur = "Defenseur";
    public HashMap<String, Integer> compteurMainJoueur = new HashMap<>() {
        {
            put("Trefle", 0);
            put("Pique", 0);
            put("Carreau", 0);
            put("Coeur", 0);
            put("Atout", 0);
        }
    };
    public int nombreAtoutsSups;

    public Joueur(String nom, List<Carte> main, int points, int niveau) {
        nomJoueur = nom;
        mainJoueur = main;
        pointsJoueur = points;
        niveauJoueur = niveau;
    }

    // Le joueur joue une carte
    public Carte jouerCarte(String couleurDemandee, Carte carteGagnante) {
        calculateurDeType(carteGagnante);
        switch (niveauJoueur) {
            case 1:
                return jouerCarteAuHasard(couleurDemandee, carteGagnante);
            case 2:
                return jouerStrategieDeBase(couleurDemandee, carteGagnante);
            case 3:
                return jouerStrategieAvancee(couleurDemandee, carteGagnante);
            default:
                return jouerCarteAuHasard(couleurDemandee, carteGagnante); // Fallback au hasard si le niveau n'est pas
                                                                           // défini
        }
    }

    // Stratégie aléatoire pour le bot "dumb"
    private Carte jouerCarteAuHasard(String couleurDemandee, Carte carteGagnante) {
        Random rand = new Random();
        Carte carteJouee = null;
    
        while (true) {
            carteJouee = mainJoueur.get(rand.nextInt(mainJoueur.size()));
    
            if (isValidCard(carteJouee, couleurDemandee, carteGagnante)) {
                break;
            }
        }
    
        mainJoueur.remove(carteJouee);
        return carteJouee;
    }
    
    private boolean isValidCard(Carte carteJouee, String couleurDemandee, Carte carteGagnante) {
        if (couleurDemandee != null) {
            if (carteJouee.typeCarte.equals(couleurDemandee) && !carteJouee.typeCarte.equals("Atout")) {
                return true;
            }
    
            if (carteJouee.typeCarte.equals(couleurDemandee) && carteJouee.typeCarte.equals("Atout")
                    && carteJouee.valeurCarte > carteGagnante.valeurCarte) {
                return true;
            }
    
            if (carteJouee.typeCarte.equals(couleurDemandee) && carteJouee.typeCarte.equals("Atout")
                    && nombreAtoutsSups == 0 && carteJouee.valeurCarte < carteGagnante.valeurCarte) {
                return true;
            }
    
            if (!carteJouee.typeCarte.equals(couleurDemandee) && couleurDemandee.equals("Atout")
                    && compteurMainJoueur.get("Atout") == 0) {
                return true;
            }
    
            if (!carteJouee.typeCarte.equals(couleurDemandee) && !couleurDemandee.equals("Atout")
                    && compteurMainJoueur.get("Atout") == 0
                    && compteurMainJoueur.get(couleurDemandee) == 0) {
                return true;
            }
        } else {
            // Handle the case where couleurDemandee is null
            return true; // Assuming any card is valid in this case
        }
    
        return false;
    }
    

    // Stratégie de base pour le bot "normal"
    private Carte jouerStrategieDeBase(String couleurDemandee, Carte carteGagnante) {
        // Implémenter une stratégie de base ici
        // Par exemple, jouer la carte de la plus haute valeur
        Carte carteJouee = null;
        mainJoueur.remove(carteJouee);
        return carteJouee;
    }

    // Stratégie avancée pour le bot "smart"
    // Ca jvais finir mais ca va etre chaud de faire un truc hyper intelligent
    private Carte jouerStrategieAvancee(String couleurDemandee, Carte carteGagnante) {
        // Implémenter une stratégie avancée ici
        // Par exemple, utiliser un algorithme Minimax pour anticiper les coups
        // Pour l'exemple, on retourne simplement la carte la plus basse
        Carte carteJouee = null;
        mainJoueur.remove(carteJouee);
        return carteJouee;
    }

    public void calculateurDeType(Carte carteGagnante) {
        int j=0;
        for (Carte i : mainJoueur) {
            compteurMainJoueur.put(i.typeCarte, j++);
            if (carteGagnante!=null && i.typeCarte.equals("Atout") && carteGagnante.typeCarte.equals("Atout")
                    && (i.valeurCarte > carteGagnante.valeurCarte)) {
                nombreAtoutsSups++;
            }
        }
    }

}
