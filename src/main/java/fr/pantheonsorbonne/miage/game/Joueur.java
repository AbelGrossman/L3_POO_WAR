package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;

public class Joueur {
    public String nomJoueur;
    public List<Carte> mainJoueur;
    public int pointsJoueur; // Score d'un joueur
    public String miseJoueur;
    public int niveauJoueur; // Niveau d'intelligence du bot: 1 pour "dumb", 2 pour "smart"
    public List<Carte> pliJoueur;
    // Rôle d'un joueur pendant une manche, celui qui remporte la mise
    // attaque face aux autre joueurs qui défendent en équipe
    public String roleJoueur = "Defenseur";
    // HashMap stockant le nombre de cartes d'une couleur dans la main
    public Map<String, Integer> nombreCarteCouleurDansMain = new HashMap<>();
    // Nombre d'atout supérieurs au plus grand atout du pli en cours
    public int nombreAtoutsSups;
    // Nombre d'atout inférieurs au plus petit atout du pli en cours
    // afin de respecter les regles bonus
    public int nombreAtoutsInfs;
    // Nombre de cartes de chaque couleur dans la main, utilisé lors de la défausse
    // du chien
    Map<Integer, Boolean> treflesDansMain = new HashMap<>();
    Map<Integer, Boolean> carreauxDansMain = new HashMap<>();
    Map<Integer, Boolean> piquesDansMain = new HashMap<>();
    Map<Integer, Boolean> coeursDansMain = new HashMap<>();
    // Bolleen vérifiant si oui ou non l'excuse a été jouée, pour modifier les
    // règles en fonction (règles bonus)
    Boolean excusePassee;
    Random rand = new Random();

    public Joueur(String nom, List<Carte> main, int points, int niveau) {
        nomJoueur = nom;
        mainJoueur = main;
        pointsJoueur = points;
        niveauJoueur = niveau;
        // Remplissage des HashMap des couleur dans la main du joueur
        // La valeur passera à true si et quand la carte sera détéctée dans la main
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
        // Sélectionne la stratégie en fonction du niveau du joueur
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

        // Continue à choisir une carte au hasard jusqu'à ce qu'une carte valide soit
        // sélectionnée
        // La validité se fait en respect des règles du tarot.
        // Le processus de validation est modifié si l'excuse a été joué, en accord avec
        // les règles bonus
        for (;;) {
            carteJouee = mainJoueur.get(rand.nextInt(mainJoueur.size()));
            // Vérifie la validité de la carte en fonction du contexte du pli
            if (!excusePassee) {
                if (isValidCard(carteJouee, couleurDemandee, carteGagnante)) {
                    break;
                }
            } else {
                if (isValidCard2(carteJouee, couleurDemandee, carteGagnante)) {
                    break;
                }
            }
        }
        // Verifie si l'excuse est passée
        if (carteJouee.getNom().equals("Excuse")) {
            excusePassee = true;
        }
        // Retire la carte jouée de la main du joueur maintenant qu'elle a été validée
        mainJoueur.remove(carteJouee);
        return carteJouee;
    }

    // Stratégie avancée pour le bot "smart"
    private Carte jouerStrategieAvancee(String couleurDemandee, Carte carteGagnante) {
        // Implémenter une stratégie avancée ici
        // Par exemple, utiliser un algorithme Minimax pour anticiper les coups
        // Pour l'exemple, on retourne simplement la carte la plus basse
        Carte carteJouee = null;
        for (Carte c : mainJoueur) {
            carteJouee = c;
            if (c.getNom().equals("9 de Coeur")) {
                break;
            }
        }
        mainJoueur.remove(carteJouee);
        return carteJouee;
    }

    // Méthode vérifiant la validité d'une carte
    private boolean isValidCard(Carte carteJouee, String couleurDemandee, Carte carteGagnante) {
        // Verifie si une couleur est demandée. La couleur demandée est fixée dans la
        // classe Pli,
        // Elle vaut null pour le premier joueur (qui joue la couleur qu'il souhaite)
        if (couleurDemandee != null) {
            // On peut jouer l'excuse quand on veut, sans conditions
            if (carteJouee.typeCarte.equals("Excuse")) {
                return true;
            }
            // Si la carte est de la couleur demandée et n'est pas un atout alors elle est
            // valide
            if (carteJouee.typeCarte.equals(couleurDemandee) && !carteJouee.typeCarte.equals("Atout")) {
                return true;
            }
            // Si la carte est un atout mais pas de la couleur demandée et si le joueur n'a
            // pas de cartes de la couleur demandée alors elle est valide
            if (carteJouee.typeCarte.equals("Atout") && !carteJouee.typeCarte.equals(couleurDemandee)
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0) {
                return true;
            }
            // Si la carte est de la couleur demandée, est un atout et a une valeur
            // supérieure à la carte gagnante alors elle est valide
            if (carteJouee.typeCarte.equals(couleurDemandee) && carteJouee.typeCarte.equals("Atout")
                    && (carteJouee.valeurCarte > carteGagnante.valeurCarte
                            || nombreAtoutsSups == 0)) {
                return true;
            }
            // Si la carte n'est pas de la couleur demandée, que la couleur demandée est un
            // atout et que le joueur n'a pas d'atouts alors elle est valide
            if (!carteJouee.typeCarte.equals(couleurDemandee) && couleurDemandee.equals("Atout")
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0) {
                return true;
            }
            // Si la couleur demandée n'est pas un atout, que la carte n'est pas de la
            // couleur demandée et que le joueur n'a ni atouts, ni cartes de la couleur
            // demandée, alors elle est valide
            if (!couleurDemandee.equals("Atout") && (!carteJouee.typeCarte.equals(couleurDemandee)
                    && nombreCarteCouleurDansMain.get("Atout") == 0
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0)) {
                return true;

            }
        } // Si la coleur demandée est null (cad si c'est au tout du premier joueur)
        else {
            return true;
        }

        return false;
    }

    // Règles modifiée quaund l'excuse est jouée
    private boolean isValidCard2(Carte carteJouee, String couleurDemandee, Carte carteGagnante) {
        if (couleurDemandee != null) {
            // Si l'excuse est la carte gagnante, cad qu'aucune autre carte n'a été jouée,
            // on peut jouer ce qu'on veut.
            if (carteGagnante.typeCarte.equals("Excuse")) {
                return true;
            }
            if (carteJouee.typeCarte.equals(couleurDemandee) && !carteJouee.typeCarte.equals("Atout")) {
                return true;
            }
            if (carteJouee.typeCarte.equals("Atout") && !carteJouee.typeCarte.equals(couleurDemandee)
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0) {
                return true;
            }

            // Nouvelles regles: il faut maintenant que l'atout soit plus petit que l'atout
            // gagnant
            if (carteJouee.typeCarte.equals(couleurDemandee) && carteJouee.typeCarte.equals("Atout")
                    && (carteJouee.valeurCarte < carteGagnante.valeurCarte
                            || nombreAtoutsInfs == 0)) {
                return true;
            }

            if (!carteJouee.typeCarte.equals(couleurDemandee) && couleurDemandee.equals("Atout")
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0) {
                return true;
            }
            if (!couleurDemandee.equals("Atout") && (!carteJouee.typeCarte.equals(couleurDemandee)
                    && nombreCarteCouleurDansMain.get("Atout") == 0
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0)) {
                return true;

            }
        } else

        {

            return true;
        }

        return false;
    }

    // Le calculateur de type calcul à la fois le nombre de cartes de chaque
    // couleurs ainsi que le nombre
    // d'atouts supérieurs ou inférieurs à l'atout gagnant (ce qui change en
    // fonction des règles bonus)
    public void calculateurDeType(Carte carteGagnante) {
        nombreCarteCouleurDansMain.put("Trefle", 0);
        nombreCarteCouleurDansMain.put("Pique", 0);
        nombreCarteCouleurDansMain.put("Coeur", 0);
        nombreCarteCouleurDansMain.put("Carreau", 0);
        nombreCarteCouleurDansMain.put("Atout", 0);

        for (Carte i : mainJoueur) {
            // Sauf si la carte est l'Excuse
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
