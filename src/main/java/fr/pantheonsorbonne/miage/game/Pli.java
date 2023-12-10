
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

    //Création d'un pli avec les joueurs et un indicateur pour savoir si l'excuse a été jouée
    public Pli(List<Joueur> joueurs, Boolean excusePasseeManche) {
        this.joueurs = joueurs;
        excusePassee = excusePasseeManche;
        jouerPli();
    }

    //méthode pour jouer un pli
    public void jouerPli() {
        this.couleurDemandee = null;
        this.carteGagnante = null;
        this.joueurGagnant = null;
        this.cartesJouees = new ArrayList<>();
        Map<String, Joueur> excuseMap = new HashMap<>();
        Carte carteExcuse = null;
        //parcours des joueurs pour jouer une carte chacun
        for (Joueur joueur : joueurs) {
            Carte carteJouee = joueur.jouerCarte(this.couleurDemandee, this.carteGagnante, excusePassee);
            excusePassee = joueur.excusePassee;
            //voir si l'excuse est jouée
            if (carteJouee.nomCarte.equals("Excuse")) {
                excuseMap.put("Excuse", joueur);
                carteExcuse = carteJouee;
            }
            if (carteJouee.nomCarte.equals("1 d'Atout")) {
                petitMap.put("1 d'Atout", joueur);
            }
            // Ajout de la carte jouée à la liste des cartes du pli
            cartesJouees.add(carteJouee);
            //On détermine la couleur demandée
            if (couleurDemandee == null) {
                couleurDemandee = carteJouee.getType();
                //exception pour l'excuse
                if (couleurDemandee.equals("Excuse")) {
                    couleurDemandee = null;
                }
            }
            //On détermine la carte gagnante et donc le joueur gagnant 
            if (estCarteGagnante(carteJouee)) {
                carteGagnante = carteJouee;
                joueurGagnant = joueur;
                System.out.println(
                        "Le " + joueur.nomJoueur + " qui est " + joueur.roleJoueur + ", est gagnant.");
            }
        }
        //voir si seulement des atouts sont joués dans un pli, sert pour plus tard
        Boolean queDesAtouts = true;
        for (Carte i : cartesJouees) {
            System.out.println("cartes jouees: " + i.getNom());
            if (i.typeCarte != "Atout") {
                queDesAtouts = false;
            }
        }

        //Règle spéciale : permutation des cartes
        List<Carte> cartesPermutees = new ArrayList<>();

        //si tous les joueurs ont joué des Atouts et qu'ils ont au moins 2 cartes dans leur main
        if (queDesAtouts && joueurs.get(0).mainJoueur.size() >= 2) {
            //Echange de deux cartes Atouts entre chaque joueur
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
            //Permutation des cartes
            if (joueurs.size() == 3) {
                joueurs.get(0).mainJoueur.add(cartesPermutees.get(4));
                joueurs.get(0).mainJoueur.add(cartesPermutees.get(5));
                joueurs.get(1).mainJoueur.add(cartesPermutees.get(0));
                joueurs.get(1).mainJoueur.add(cartesPermutees.get(1));
                joueurs.get(2).mainJoueur.add(cartesPermutees.get(2));
                joueurs.get(2).mainJoueur.add(cartesPermutees.get(3));
            } else if (joueurs.size() == 4) {
                joueurs.get(0).mainJoueur.add(cartesPermutees.get(6));
                joueurs.get(0).mainJoueur.add(cartesPermutees.get(7));
                joueurs.get(1).mainJoueur.add(cartesPermutees.get(4));
                joueurs.get(1).mainJoueur.add(cartesPermutees.get(5));
                joueurs.get(2).mainJoueur.add(cartesPermutees.get(0));
                joueurs.get(2).mainJoueur.add(cartesPermutees.get(1));
                joueurs.get(3).mainJoueur.add(cartesPermutees.get(2));
                joueurs.get(3).mainJoueur.add(cartesPermutees.get(3));
            }
            //Re-trie des mains des joueurs
            for (Joueur j : joueurs) {
                Collections.sort(j.mainJoueur, Comparator.comparing(Carte::getValeur));
                Collections.sort(j.mainJoueur, Comparator.comparing(Carte::getType));
            }
        }

        //Gestion de l'excuse
        //verifie si l'excuse est jouée et que le rôle du joueur gagnant du pli n'est pas le même que le rôle du joueur ayant joué l'excuse 
        if (carteExcuse != null && !joueurGagnant.roleJoueur.equals(excuseMap.get("Excuse").roleJoueur)) {

        //Determine si c'est l'attaque ou la defense qui a joué l'excuse
            if (excuseMap.get("Excuse").roleJoueur.equals("Attaquant")) {
                Manche.pliAttaque.add(carteExcuse);
                Manche.donALaDefense = true;
            } else if (excuseMap.get("Excuse").roleJoueur.equals("Attaquant")) {
                Manche.pliDefense.add(carteExcuse);
                Manche.donALAttaque = true;
            }
            //on l'enleve des cartes jouées pour que son traitement spécial soit correctement géré.
            cartesJouees.remove(carteExcuse);
        }
        //On attribue le pli à l'équipe gagnante
        if ("Attaquant".equals(joueurGagnant.roleJoueur)) {
            Manche.pliAttaque.addAll(cartesJouees);
            System.out.println("ajout des cartes au pli attaquant");
        } else {
            Manche.pliDefense.addAll(cartesJouees);
            System.out.println("ajout des cartes au pli défenseur");
        }
    }

    private boolean estCarteGagnante(Carte carte) {
        //La première carte du pli jouée définit la couleur demandée et gagne par défaut
        if (carte.nomCarte.equals("Excuse")) {
            return false;
        }
        if (carteGagnante == null) {
            return true;
        }

        //Si la carte jouée est un atout et que la carte gagnante actuelle n'en est pas un, ou si elle est d'une valeur supérieure
        if (!excusePassee) {
            if (carte.getType().equals("Atout")) {
                return !carteGagnante.getType().equals("Atout") || carte.getValeur() > carteGagnante.getValeur();
            }
        } else {
            if (carte.getType().equals("Atout")) {
                return !carteGagnante.getType().equals("Atout") || carte.getValeur() < carteGagnante.getValeur();
            }
        }

        //Si la carte jouée suit la couleur demandée et a une valeur plus élevée que la carte gagnante actuelle, elle devient carte gagnante
        if (carte.getType().equals(couleurDemandee)) {
            return carte.getValeur() > carteGagnante.getValeur();
        }
        return false;
    }

    //getters et setters utiles
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
