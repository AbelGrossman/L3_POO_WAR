package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.Map;
import java.util.Random;
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

    public Joueur(String nom, List<Carte> main, int points, int niveau) {
        nomJoueur = nom;
        mainJoueur = main;
        pointsJoueur = points;
        niveauJoueur = niveau;
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
        switch (niveauJoueur) {
            case 1:
                return jouerCarteAuHasard(couleurDemandee, carteGagnante);
            case 2:
                return jouerStrategieAvancee(couleurDemandee, carteGagnante);
            default:
                return jouerCarteAuHasard(couleurDemandee, carteGagnante);
        }
    }

    // Stratégie aléatoire pour le bot "dumb"
    private Carte jouerCarteAuHasard(String couleurDemandee, Carte carteGagnante) {
        Carte carteJouee = null;

        for (;;) {
            carteJouee = mainJoueur.get(rand.nextInt(mainJoueur.size()));
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
        if (carteJouee.getNom().equals("Excuse")) {
            excusePassee = true;
        }

        mainJoueur.remove(carteJouee);
        return carteJouee;
    }

    // Stratégie avancée pour le bot "smart"
    // Ca jvais finir mais ca va etre chaud de faire un truc hyper intelligent
    private Carte jouerStrategieAvancee(String couleurDemandee, Carte carteGagnante) {
        // Implémenter une stratégie avancée ici
        // Par exemple, utiliser un algorithme Minimax pour anticiper les coups
        // Pour l'exemple, on retourne simplement la carte la plus basse
        Carte carteJouee=null;
        for(Carte c:mainJoueur){
            carteJouee=c;
            if(c.getNom().equals("9 de Coeur")){
                break;
            }
        }
        mainJoueur.remove(carteJouee);
        return carteJouee;
    }

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
