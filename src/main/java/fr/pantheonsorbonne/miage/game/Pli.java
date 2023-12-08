
package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import org.checkerframework.checker.units.qual.s;

public class Pli {
    public List<Carte> cartesJouees;
    public String couleurDemandee;
    public Carte carteGagnante;
    public Joueur joueurGagnant;
    public List<Joueur> joueurs;
    public List<Carte> pliDefense;
    public Boolean excusePassee;
    Random rand = new Random();
    public Map<String, Joueur> petitMap = new HashMap<>();

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
        Map<String, Joueur> excuseMap = new HashMap<>();
        Carte carteExcuse = null;
        for (Joueur joueur : joueurs) {
            // for (Carte d : joueur.mainJoueur) {
            // System.out.println(d.nomCarte);
            // }
            Carte carteJouee = joueur.jouerCarte(this.couleurDemandee, this.carteGagnante, excusePassee);
            excusePassee = joueur.excusePassee;
            if (carteJouee.nomCarte.equals("Excuse")) {
                excuseMap.put("Excuse", joueur);
                carteExcuse = carteJouee;
            }
            if(carteJouee.nomCarte.equals("1 d'Atout")){
                petitMap.put("1 d'Atout", joueur);
            }
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
        List<Carte> cartesPermutees = new ArrayList<>();
        if (queDesAtouts && joueurs.get(0).mainJoueur.size() >= 2) {
            for (int i = 0; i < joueurs.size(); i++) {
                List<Carte> mainDuJoueur = joueurs.get(i).mainJoueur;
                Carte carte1 = mainDuJoueur.get(rand.nextInt(mainDuJoueur.size()));
                cartesPermutees.add(carte1);
                mainDuJoueur.remove(carte1);
                Carte carte2 = mainDuJoueur.get(rand.nextInt(mainDuJoueur.size()));
                cartesPermutees.add(carte2);
                mainDuJoueur.remove(carte2);
                System.out.println("Le " + joueurs.get(i).nomJoueur + " echange les cartes " + carte1.getNom() + " et "
                        + carte2.getNom());
            }
            joueurs.get(0).mainJoueur.add(cartesPermutees.get(4));
            joueurs.get(0).mainJoueur.add(cartesPermutees.get(5));
            joueurs.get(1).mainJoueur.add(cartesPermutees.get(0));
            joueurs.get(1).mainJoueur.add(cartesPermutees.get(1));
            joueurs.get(2).mainJoueur.add(cartesPermutees.get(2));
            joueurs.get(2).mainJoueur.add(cartesPermutees.get(3));
            for (Joueur j : joueurs) {
                Collections.sort(j.mainJoueur, Comparator.comparing(Carte::getValeur));
                Collections.sort(j.mainJoueur, Comparator.comparing(Carte::getType));
            }
        }

        if (carteExcuse != null && !joueurGagnant.roleJoueur.equals(excuseMap.get("Excuse").roleJoueur)) {
            if (excuseMap.get("Excuse").roleJoueur.equals("Attaquant")) {
                Manche.pliAttaque.add(carteExcuse);
                Manche.donALaDefense = true;
            } else if (excuseMap.get("Excuse").roleJoueur.equals("Attaquant")) {
                Manche.pliDefense.add(carteExcuse);
                Manche.donALAttaque = true;
            }
            cartesJouees.remove(carteExcuse);
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
