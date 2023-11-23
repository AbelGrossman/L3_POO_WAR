
package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.ArrayList;

public class Pli {
    public List<Carte> cartesJouees;
    public String couleurDemandee;
    public Carte carteGagnante;
    public Joueur joueurGagnant;
    public List<Joueur> joueurs;
    public List<Carte> pliAttaque;
    public List<Carte> pliDefense;

    public Pli(List<Joueur> joueurs, List<Carte> pliAttaque, List<Carte> pliDefense) {
        this.joueurs = joueurs;
        this.pliAttaque=pliAttaque;
        this.pliDefense=pliDefense;
        jouerPli();
    }

    public void jouerPli() {
        this.couleurDemandee = null;
        this.carteGagnante = null;
        this.joueurGagnant = null;
        this.cartesJouees = new ArrayList<>();
        for (Joueur joueur : joueurs) {
            Carte carteJouee = joueur.jouerCarte(this.couleurDemandee, this.carteGagnante);
            cartesJouees.add(carteJouee);

            // Déterminer la couleur demandée s'il s'agit de la première carte du pli
            if (couleurDemandee == null) {
                couleurDemandee = carteJouee.getType();
            }

            // Déterminer la carte gagnante du pli
            if (estCarteGagnante(carteJouee)) {
                carteGagnante = carteJouee;
                joueurGagnant = joueur;
            }
        }

        // À la fin du pli, attribuer le pli à l'équipe gagnante
        if (joueurGagnant.roleJoueur == "Attaquant") {
            pliAttaque.addAll(cartesJouees);
        } else {
            pliDefense.addAll(cartesJouees);
        }
    }

    private boolean estCarteGagnante(Carte carte) {
        // Si la première carte du pli est jouée, elle définit la couleur demandée et
        // gagne par défaut
        if (carteGagnante == null) {
            return true;
        }

        // Si la carte jouée est un atout et que la carte gagnante actuelle n'en est pas
        // un, ou si elle est d'une valeur supérieure
        if (carte.getType().equals("Atout")) {
            return !carteGagnante.getType().equals("Atout") || carte.getValeur() > carteGagnante.getValeur();
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
