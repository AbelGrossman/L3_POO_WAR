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
    Carte carteTreflesInfs = null;
    Carte cartePiquesInfs = null;
    Carte carteCarreauxInfs = null;
    Carte carteCoeursInfs = null;
    Carte plusPetitAtoutSup = null;
    Carte plusGrandAtoutInf = null;
    Carte plusPetitAtout = null;
    Carte plusGrandAtout = null;
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
    public Carte jouerCarte(String couleurDemandee, Carte carteGagnante, Boolean excusePasseePli,
            Joueur joueurGagnant) {
        nombreAtoutsSups = 0;
        nombreAtoutsInfs = 0;
        excusePassee = excusePasseePli;
        carteTreflesInfs = null;
        cartePiquesInfs = null;
        carteCarreauxInfs = null;
        carteCoeursInfs = null;
        plusPetitAtoutSup = null;
        plusGrandAtoutInf = null;
        plusPetitAtout = null;
        plusGrandAtout = null;
        calculateurDeType(carteGagnante);
        // Sélectionne la stratégie en fonction du niveau du joueur
        switch (niveauJoueur) {
            case 1:
                return jouerCarteAuHasard(couleurDemandee, carteGagnante);
            case 2:
                return jouerCarteAuHasardStratAvancee(couleurDemandee, carteGagnante, joueurGagnant);
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
    private Carte jouerCarteAuHasardStratAvancee(String couleurDemandee, Carte carteGagnante, Joueur joueurGagnant) {
        Carte carteJouee = null;
        // Implémenter une stratégie avancée ici
        // Par exemple, utiliser un algorithme Minimax pour anticiper les coups
        // Pour l'exemple, on retourne simplement la carte la plus basse
        for (;;) {
            carteJouee = mainJoueur.get(rand.nextInt(mainJoueur.size()));
            // Vérifie la validité de la carte en fonction du contexte du pli
            if (!excusePassee) {
                if (isValidCardStrat1(carteJouee, couleurDemandee, carteGagnante, joueurGagnant)) {

                    break;
                }
            } else {
                if (isValidCardStrat2(carteJouee, couleurDemandee, carteGagnante, joueurGagnant)) {

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
            // pas de cartes de la couleur demandée alors elle est valide si aucun autre
            // atout n'a été joué
            // ou si l'atout est de valeur supèrieure à l'atout gagnant
            if (carteJouee.typeCarte.equals("Atout") && !carteJouee.typeCarte.equals(couleurDemandee)
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0 && (!carteGagnante.typeCarte.equals("Atout")
                            || carteGagnante.valeurCarte < carteJouee.valeurCarte
                            || (carteGagnante.typeCarte.equals("Atout") && nombreAtoutsSups == 0))) {
                return true;
            }
            // Si la carte est de la couleur demandée, est un atout et a une valeur
            // supérieure à la carte gagnante, ou que le joueur n'a pas de cartes de valeur
            // supérieure
            // alors elle est valide
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
                    && nombreCarteCouleurDansMain.get(couleurDemandee) == 0 && (!carteGagnante.typeCarte.equals("Atout")
                            || carteGagnante.valeurCarte > carteJouee.valeurCarte
                            || (carteGagnante.typeCarte.equals("Atout") && nombreAtoutsInfs == 0))) {
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

    private Boolean isValidCardStrat1(Carte carteJouee, String couleurDemandee, Carte carteGagnante,
            Joueur joueurGagnant) {
        if (couleurDemandee != null) {
            // La stratégie avancée consiste a prendre en compte plus de conditions
            // pour les défenseurs quand le joueur qui gagne est l'attaquant.
            // Elle n'est donc que prise en compte si le joueur intéligent différe du joueur
            // attaquant lors d'une manche
            if (joueurGagnant.roleJoueur.equals("Attaquant")) {
                if (carteJouee.typeCarte.equals("Excuse")) {
                    return true;
                }
                // Si la carte est de la couleur demandée et n'est pas un atout alors, on
                // s'assure avec
                // la stratégie avancée de jouer la plus petite carte possible quand on sait que
                // le gagnant
                // est l'attaquant, à moins que notre carte soit plus grande que la sienne
                if (carteJouee.typeCarte.equals(couleurDemandee) && !carteJouee.typeCarte.equals("Atout")) {
                    if (!carteGagnante.typeCarte.equals("Atout")
                            && carteGagnante.valeurCarte < carteJouee.valeurCarte) {
                        return true;
                    }
                    if (couleurDemandee.equals("Trefle") && carteTreflesInfs.valeurCarte == carteJouee.valeurCarte) {
                      
                        return true;
                    }
                    if (couleurDemandee.equals("Pique") && cartePiquesInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (couleurDemandee.equals("Coeur") && carteCoeursInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (couleurDemandee.equals("Carreau") && carteCarreauxInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                }
                // Maintenant, on veut que le joueur ne se débarasse que de son atout le plus
                // petit possible
                if (carteJouee.typeCarte.equals("Atout") && !carteJouee.typeCarte.equals(couleurDemandee)
                        && nombreCarteCouleurDansMain.get(couleurDemandee) == 0
                        && ((!carteGagnante.typeCarte.equals("Atout")
                                && carteJouee.valeurCarte == plusPetitAtout.valeurCarte)
                                || (carteGagnante.typeCarte.equals("Atout")
                                        && ((carteJouee.valeurCarte == plusPetitAtoutSup.valeurCarte)
                                                || (nombreAtoutsSups == 0
                                                        && carteJouee.valeurCarte == plusPetitAtout.valeurCarte))))) {
                    return true;
                }
                // Maintenant, on veut que le joueur ne se débarasse que de son atout le plus
                // petit possible
                if (carteJouee.typeCarte.equals(couleurDemandee) && carteJouee.typeCarte.equals("Atout")
                        && (carteJouee.valeurCarte == plusPetitAtoutSup.valeurCarte
                                || (nombreAtoutsSups == 0
                                        && carteJouee.valeurCarte == plusPetitAtout.valeurCarte))) {
                    return true;
                }
                // Maintenant, on veut que le joueur se débarasse de la plus petite carte
                // possible
                if (!carteJouee.typeCarte.equals(couleurDemandee) && couleurDemandee.equals("Atout")
                        && nombreCarteCouleurDansMain.get(couleurDemandee) == 0) {
                    if (carteJouee.typeCarte.equals("Trefle")
                            && carteTreflesInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Pique") && cartePiquesInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Coeur") && carteCoeursInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Carreau")
                            && carteCarreauxInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                }
                // Même chose ici
                if (!couleurDemandee.equals("Atout") && (!carteJouee.typeCarte.equals(couleurDemandee)
                        && nombreCarteCouleurDansMain.get("Atout") == 0
                        && nombreCarteCouleurDansMain.get(couleurDemandee) == 0)) {
                    if (carteJouee.typeCarte.equals("Trefle")
                            && carteTreflesInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Pique") && cartePiquesInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Coeur") && carteCoeursInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Carreau")
                            && carteCarreauxInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                }

            } else {
                return isValidCard(carteJouee, couleurDemandee, carteGagnante);
            }
        } // Si la coleur demandée est null (cad si c'est au tout du premier joueur)
        else {
            return true;
        }
        return false;
    }

    private Boolean isValidCardStrat2(Carte carteJouee, String couleurDemandee, Carte carteGagnante,
            Joueur joueurGagnant) {
        if (couleurDemandee != null) {
            if (joueurGagnant.roleJoueur.equals("Attaquant")) {
                if (carteGagnante.typeCarte.equals("Excuse")) {
                    return true;
                }
                if (carteJouee.typeCarte.equals(couleurDemandee) && !carteJouee.typeCarte.equals("Atout")) {
                    if (!carteGagnante.typeCarte.equals("Atout")
                            && carteGagnante.valeurCarte < carteJouee.valeurCarte) {
                        return true;
                    }
                    if (couleurDemandee.equals("Trefle") && carteTreflesInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (couleurDemandee.equals("Pique") && cartePiquesInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (couleurDemandee.equals("Coeur") && carteCoeursInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (couleurDemandee.equals("Carreau") && carteCarreauxInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                }
                // Maintenant, on veut que le joueur ne se débarasse que de son atout le plus
                // grand possible (regles bonus)
                if (carteJouee.typeCarte.equals("Atout") && !carteJouee.typeCarte.equals(couleurDemandee)
                        && nombreCarteCouleurDansMain.get(couleurDemandee) == 0
                        && ((!carteGagnante.typeCarte.equals("Atout")
                                && carteJouee.valeurCarte == plusGrandAtout.valeurCarte)
                                || (carteGagnante.typeCarte.equals("Atout")
                                        && ((carteJouee.valeurCarte == plusGrandAtoutInf.valeurCarte)
                                                || (nombreAtoutsInfs == 0
                                                        && carteJouee.valeurCarte == plusGrandAtout.valeurCarte))))) {
                    return true;
                }
                // Maintenant, on veut que le joeur ne se débarasse que de son atout le plus
                // grand possible
                if (carteJouee.typeCarte.equals(couleurDemandee) && carteJouee.typeCarte.equals("Atout")
                        && (carteJouee.valeurCarte == plusGrandAtoutInf.valeurCarte
                                || (nombreAtoutsInfs == 0
                                        && carteJouee.valeurCarte == plusGrandAtout.valeurCarte))) {
                    return true;
                }
                // Maintenant, on veut que le joueur se débarasse de la plus petite carte
                // possible
                if (!carteJouee.typeCarte.equals(couleurDemandee) && couleurDemandee.equals("Atout")
                        && nombreCarteCouleurDansMain.get(couleurDemandee) == 0) {
                    if (carteJouee.typeCarte.equals("Trefle")
                            && carteTreflesInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Pique") && cartePiquesInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Coeur") && carteCoeursInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Carreau")
                            && carteCarreauxInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                }
                // Même chose ici
                if (!couleurDemandee.equals("Atout") && (!carteJouee.typeCarte.equals(couleurDemandee)
                        && nombreCarteCouleurDansMain.get("Atout") == 0
                        && nombreCarteCouleurDansMain.get(couleurDemandee) == 0)) {
                    if (carteJouee.typeCarte.equals("Trefle")
                            && carteTreflesInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Pique") && cartePiquesInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Coeur") && carteCoeursInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                    if (carteJouee.typeCarte.equals("Carreau")
                            && carteCarreauxInfs.valeurCarte == carteJouee.valeurCarte) {
                        return true;
                    }
                }

            } else {
                return isValidCard2(carteJouee, couleurDemandee, carteGagnante);
            }
        } // Si la coleur demandée est null (cad si c'est au tout du premier joueur)
        else {
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

        plusPetitAtoutSup = new Carte("Atout", 21, "21 d'atout", true, 5);
        plusGrandAtoutInf = new Carte("Atout", 1, "1 d'atout", true, 5);
        plusGrandAtout = new Carte("Atout", 1, "1 d'atout", true, 5);
        plusPetitAtout = new Carte("Atout", 21, "21 d'atout", true, 5);
        carteTreflesInfs = new Carte("Trefle", 14, "Roi de Trefle", false, 5);
        cartePiquesInfs = new Carte("Pique", 14, "Roi de Pique", false, 5);
        carteCoeursInfs = new Carte("Coeur", 14, "Roi de Coeur", false, 5);
        carteCarreauxInfs = new Carte("Carreau", 14, "Roi de Carreau", false, 5);
        for (Carte i : mainJoueur) {
            // Sauf si la carte est l'Excuse
            if (!i.typeCarte.equals("Excuse")) {
                nombreCarteCouleurDansMain.put(i.typeCarte, nombreCarteCouleurDansMain.get(i.typeCarte) + 1);
                if (carteGagnante != null) {
                    if (i.typeCarte.equals("Atout")) {
                        if (i.valeurCarte > plusGrandAtout.valeurCarte) {
                            plusGrandAtout = i;
                        }
                        if (i.valeurCarte < plusPetitAtout.valeurCarte) {
                            plusPetitAtout = i;
                        }
                    }
                    if (i.typeCarte.equals("Atout") && carteGagnante.typeCarte.equals("Atout")
                            && (i.valeurCarte > carteGagnante.valeurCarte)) {
                        nombreAtoutsSups++;
                        if (i.valeurCarte < plusPetitAtoutSup.valeurCarte) {
                            plusPetitAtoutSup = i;
                        }
                    }
                    if (i.typeCarte.equals("Atout")
                            && carteGagnante.typeCarte.equals("Atout")
                            && (i.valeurCarte < carteGagnante.valeurCarte)) {
                        nombreAtoutsInfs++;
                        if (i.valeurCarte > plusGrandAtoutInf.valeurCarte) {
                            plusGrandAtoutInf = i;
                        }
                        if (i.valeurCarte < plusPetitAtout.valeurCarte) {
                            plusGrandAtoutInf = i;
                        }
                    }
                    if (i.typeCarte.equals("Trefle") && i.valeurCarte < carteTreflesInfs.valeurCarte) {
                        carteTreflesInfs = i;
                    }
                    if (i.typeCarte.equals("Pique")
                            && i.valeurCarte < cartePiquesInfs.valeurCarte) {
                        cartePiquesInfs = i;
                    }
                    if (i.typeCarte.equals("Carreau") && i.valeurCarte < carteCarreauxInfs.valeurCarte) {
                        carteCarreauxInfs = i;
                    }
                    if (i.typeCarte.equals("Coeur")
                            && i.valeurCarte < carteCoeursInfs.valeurCarte) {
                        carteCoeursInfs = i;
                    }
                }
            }
        }
    }
}
