package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.Map;
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
    public Map<String, Integer> nombreCarteCouleurDansMain = new HashMap<>();
    public int nombreAtoutsSups; 
    public int nombreAtoutsInfs;
    Map<Integer, Boolean> treflesDansMain = new HashMap<>();
    Map<Integer, Boolean> carreauxDansMain = new HashMap<>();
    Map<Integer, Boolean> piquesDansMain = new HashMap<>();
    Map<Integer, Boolean> coeursDansMain = new HashMap<>();
    Random rand = new Random();
    Boolean excusePassee;

    //Constructeur de la classe Joueur
    public Joueur(String nom, List<Carte> main, int points, int niveau) {
        nomJoueur = nom;
        mainJoueur = main;
        pointsJoueur = points;
        niveauJoueur = niveau;
        //Ajoute les cartes par couleur dans la main du joueur dans chacunes des hashmap
        for (int i = 1; i < 15; i++) {
            treflesDansMain.put(i, false);
            carreauxDansMain.put(i, false);
            piquesDansMain.put(i, false);
            coeursDansMain.put(i, false);
        }
    }

    // Le joueur joue une carte
    public Carte jouerCarte(String couleurDemandee, Carte carteGagnante, Boolean excusePasseePli) {
        nombreAtoutsSups = 0;
        nombreAtoutsInfs = 0;
        excusePassee = excusePasseePli;
        calculateurDeType(carteGagnante);
        //Sélectionne la stratégie en fonction du niveau du joueur
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
        Carte carteJouee = null;

        //Continue à choisir une carte au hasard jusqu'à ce qu'une carte valide soit sélectionnée
        for (;;) {
            carteJouee = mainJoueur.get(rand.nextInt(mainJoueur.size()));
            //Vérifie la validité de la carte en fonction du contexte du pli
            if (!excusePassee) {
                if (isValidCard(carteJouee, couleurDemandee, carteGagnante)) {
                    System.out.println("Le " + nomJoueur + " joue: " + carteJouee.nomCarte);
                    break;
                }
            } else {
                if (isValidCard2(carteJouee, couleurDemandee, carteGagnante)) {
                    System.out.println("Le " + nomJoueur + " joue: " + carteJouee.nomCarte);
                    break;
                }
            }
        }
        //Verifie si l'excuse est passée
        if (carteJouee.getNom().equals("Excuse")) {
            excusePassee = true;
        }
        //Retire la carte jouée de la main du joueur
        mainJoueur.remove(carteJouee);
        return carteJouee;
    }

    //Méthode vérifiant la validité d'une carte
    private boolean isValidCard(Carte carteJouee, String couleurDemandee, Carte carteGagnante) {
        if (couleurDemandee != null) {
            if (carteJouee.typeCarte.equals("Excuse")) {
                System.out.println("case 1");
                return true;
            }
            if (carteJouee.typeCarte.equals(couleurDemandee) && !carteJouee.typeCarte.equals("Atout")) {
                System.out.println("case 2");
                return true;
            }

            if (carteJouee.typeCarte.equals("Atout") && !carteJouee.typeCarte.equals(couleurDemandee)
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0) {
                System.out.println("case 3");
                return true;
            }

            if (carteJouee.typeCarte.equals(couleurDemandee) && carteJouee.typeCarte.equals("Atout")
                    && (carteJouee.valeurCarte > carteGagnante.valeurCarte
                            || nombreAtoutsSups == 0)) {
                System.out.println("case 4");
                return true;
            }

            if (!carteJouee.typeCarte.equals(couleurDemandee) && couleurDemandee.equals("Atout")
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0) {
                System.out.println("case 5");
                return true;
            }
            if (!couleurDemandee.equals("Atout") && (!carteJouee.typeCarte.equals(couleurDemandee)
                    && nombreCarteCouleurDansMain.get("Atout") == 0
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0)) {
                System.out.println("case 6");
                return true;

            }
        } else

        {
            // Handle the case where couleurDemandee is null
            return true; // Assuming any card is valid in this case
        }

        return false;
    }

    private boolean isValidCard2(Carte carteJouee, String couleurDemandee, Carte carteGagnante) {
        if (couleurDemandee != null) {
            if (carteGagnante.typeCarte.equals("Excuse")) {
                System.out.println("case 1");
                return true;
            }
            if (carteJouee.typeCarte.equals(couleurDemandee) && !carteJouee.typeCarte.equals("Atout")) {
                System.out.println("case 2");
                return true;
            }

            if (carteJouee.typeCarte.equals("Atout") && !carteJouee.typeCarte.equals(couleurDemandee)
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0) {
                System.out.println("case 3");
                return true;
            }

            if (carteJouee.typeCarte.equals(couleurDemandee) && carteJouee.typeCarte.equals("Atout")
                    && (carteJouee.valeurCarte < carteGagnante.valeurCarte
                            || nombreAtoutsInfs == 0)) {
                System.out.println("case 4");
                return true;
            }

            if (!carteJouee.typeCarte.equals(couleurDemandee) && couleurDemandee.equals("Atout")
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0) {
                System.out.println("case 5");
                return true;
            }
            if (!couleurDemandee.equals("Atout") && (!carteJouee.typeCarte.equals(couleurDemandee)
                    && nombreCarteCouleurDansMain.get("Atout") == 0
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0)) {
                System.out.println("case 6");
                return true;

            }
        } else

        {
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
        nombreCarteCouleurDansMain.put("Trefle", 0);
        nombreCarteCouleurDansMain.put("Pique", 0);
        nombreCarteCouleurDansMain.put("Coeur", 0);
        nombreCarteCouleurDansMain.put("Carreau", 0);
        nombreCarteCouleurDansMain.put("Atout", 0);

        for (Carte i : mainJoueur) {
            if (!i.typeCarte.equals("Excuse")) {
                nombreCarteCouleurDansMain.put(i.typeCarte, nombreCarteCouleurDansMain.get(i.typeCarte) + 1);
                if (carteGagnante != null && i.typeCarte.equals("Atout") && carteGagnante.typeCarte.equals("Atout")
                        && (i.valeurCarte > carteGagnante.valeurCarte)) {
                    nombreAtoutsSups++;
                } else if (carteGagnante != null && i.typeCarte.equals("Atout")
                        && carteGagnante.typeCarte.equals("Atout")
                        && (i.valeurCarte < carteGagnante.valeurCarte)) {
                    nombreAtoutsInfs++;
                }
            }
        }
    }
}
