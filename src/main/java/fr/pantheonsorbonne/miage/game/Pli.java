
package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Pli {
    public List<Carte> cartesJouees;
    public String couleurDemandee;
    public Carte carteGagnante;
    public Joueur joueurGagnant;
    public List<Joueur> joueurs;
    public List<Carte> pliAttaque;
    public List<Carte> pliDefense;
    public Boolean excusePassee;
    Random rand = new Random();

    public Pli(List<Joueur> joueurs, Boolean excusePasseeManche) {
        this.joueurs = joueurs;
        excusePassee = excusePasseeManche;
        jouerPli();
    }

    public void jouerPli() {
        this.couleurDemandee = null;
        this.carteGagnante = null;
        this.joueurGagnant = null;
        this.cartesJouees = new ArrayList<>();
        for (Joueur joueur : joueurs) {
            // for (Carte d : joueur.mainJoueur) {
            // System.out.println(d.nomCarte);
            // }
            Carte carteJouee = joueur.jouerCarte(this.couleurDemandee, this.carteGagnante, excusePassee);
            excusePassee = joueur.excusePassee;
            cartesJouees.add(carteJouee);
            // Déterminer la couleur demandée s'il s'agit de la première carte du pli
            if (couleurDemandee == null) {
                couleurDemandee = carteJouee.getType();
                if (couleurDemandee.equals("Excuse")) {
                    couleurDemandee = null;
                }
            }

            // Déterminer la carte gagnante du pli
            if (estCarteGagnante(carteJouee)) {
                carteGagnante = carteJouee;
                joueurGagnant = joueur;
                System.out.println(
                        "Le " + joueur.nomJoueur + " qui est " + joueur.roleJoueur + ", est gagnant.");
            }
        }
        Boolean queDesAtouts = true;
        for (Carte i : cartesJouees) {
            System.out.println("cartes jouees: " + i.getNom());
            if (i.typeCarte != "Atout") {
                queDesAtouts = false;
            }
        }
        if (queDesAtouts) {
            for (int i = 0; i < joueurs.size(); i++) {
                List<Carte> mainDuJoueur = joueurs.get(i).mainJoueur;
                Carte carte1 = mainDuJoueur.get(rand.nextInt(mainDuJoueur.size()));
                mainDuJoueur.remove(carte1);
                Carte carte2 = mainDuJoueur.get(rand.nextInt(mainDuJoueur.size()));
                mainDuJoueur.remove(carte2);

            }

        }
        // À la fin du pli, attribuer le pli à l'équipe gagnante
        if ("Attaquant".equals(joueurGagnant.roleJoueur)) {
            Manche.pliAttaque.addAll(cartesJouees);
            System.out.println("ajout des cartes au pli attaquant");
        } else {
            Manche.pliDefense.addAll(cartesJouees);
            System.out.println("ajout des cartes au pli défenseur");
        }
    }

    private boolean estCarteGagnante(Carte carte) {
        // Si la première carte du pli est jouée, elle définit la couleur demandée et
        // gagne par défaut
        if (carte.nomCarte.equals("Excuse")) {
            return false;
        }
        if (carteGagnante == null) {
            return true;
        }

        // Si la carte jouée est un atout et que la carte gagnante actuelle n'en est pas
        // un, ou si elle est d'une valeur supérieure
        if (!excusePassee) {
            if (carte.getType().equals("Atout")) {
                return !carteGagnante.getType().equals("Atout") || carte.getValeur() > carteGagnante.getValeur();
            }
        } else {
            if (carte.getType().equals("Atout")) {
                return !carteGagnante.getType().equals("Atout") || carte.getValeur() < carteGagnante.getValeur();
            }
        }

        // Si la carte jouée suit la couleur demandée et a une valeur plus élevée que la
        // carte gagnante actuelle
        if (carte.getType().equals(couleurDemandee)) {
            return carte.getValeur() > carteGagnante.getValeur();
        }

        // La carte gagnante actuelle reste gagnante
        return false;
    }

    // Accesseurs et autres méthodes utiles
    public Joueur getJoueurGagnant() {
        return joueurGagnant;
    }

    public Carte getCarteGagnante() {
        return carteGagnante;
    }

    public List<Carte> getCartesJouees() {
        return cartesJouees;
    }

    public void setCartesJouees(List<Carte> cartesJouees) {
        this.cartesJouees = cartesJouees;
    }

    public String getCouleurDemandee() {
        return couleurDemandee;
    }

    public void setCouleurDemandee(String couleurDemandee) {
        this.couleurDemandee = couleurDemandee;
    }

    public void setCarteGagnante(Carte carteGagnante) {
        this.carteGagnante = carteGagnante;
    }

    public void setJoueurGagnant(Joueur joueurGagnant) {
        this.joueurGagnant = joueurGagnant;
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

}
