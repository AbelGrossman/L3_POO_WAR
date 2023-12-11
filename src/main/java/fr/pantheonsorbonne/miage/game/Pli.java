
package fr.pantheonsorbonne.miage.game;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

public class Pli {
    //Liste des cartes jouees lors d'un pli (3 ou 4)
    public List<Carte> cartesJouees;
    //Couleur demandée lors du pli
    public String couleurDemandee;
    //Carte vainqueur du pli (s'actualise le long du pli)
    public Carte carteGagnante;
    public Joueur joueurGagnant;
    //Liste des joueurs
    public List<Joueur> joueurs;
    //Boolean pour voir si l'excuse a été jouée
    public Boolean excusePassee;
    //HashMap pour savoir si le petit a été jouée ce pli (bonus de fin)
    public Map<String, Joueur> petitMap = new HashMap<>();
    Random rand = new Random();

    //Création d'un pli avec les joueurs et un indicateur pour savoir si l'excuse a été jouée
    public Pli(List<Joueur> joueurs, Boolean excusePasseeManche) {
        this.joueurs = joueurs;
        excusePassee = excusePasseeManche;
        jouerPli();
    }

    //méthode pour jouer un pli
    public void jouerPli() {
        couleurDemandee = null;
        carteGagnante = null;
        joueurGagnant = null;
        cartesJouees = new ArrayList<>();
        //HashMap pour la gestion de l'excuse, assigne l'excuse au joueur qui l'a joué
        Map<String, Joueur> excuseMap = new HashMap<>();
        Carte carteExcuse = null;
        //Parcours des joueurs pour jouer une carte chacun
        for (Joueur joueur : joueurs) {
            //On appelle la méthode joueurCarte de la classe joueur, pour le joueur en cours, et
            //on récupère la carte jouée.
            Carte carteJouee = joueur.jouerCarte(couleurDemandee, carteGagnante, excusePassee, joueurGagnant);
            //On récupère l'info de si oui ou non l'excuse a été jouée, pour que ce soit pris en compte
            //pour le prochain joueur et les prochains plis.
            excusePassee = joueur.excusePassee;
            //Voir si l'excuse est jouée
            if (carteJouee.nomCarte.equals("Excuse")) {
                //On assigne l'excuse au joueur qui l'a joué
                excuseMap.put("Excuse", joueur);
                carteExcuse = carteJouee;
            }
            //Si le petit est joué
            if (carteJouee.nomCarte.equals("1 d'Atout")) {
                //De même
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
            }
        }

        //Voir si seulement des atouts sont joués dans un pli, pour appliquer la régle bonus
        Boolean queDesAtouts = true;

        for (Carte i : cartesJouees) {
            if (i.typeCarte != "Atout") {
                queDesAtouts = false;
            }
        }

        //Règle spéciale : permutation des cartes
        //Tableau pour collecter les cartes dont les joueurs se débarassent
        List<Carte> cartesPermutees = new ArrayList<>();

        //si tous les joueurs ont joué des Atouts et qu'ils ont au moins 2 cartes dans leur main
        if (queDesAtouts && joueurs.get(0).mainJoueur.size() >= 2) {
            //Echange de deux cartes aléatoires entre chaque joueur
            for (int i = 0; i < joueurs.size(); i++) {
                List<Carte> mainDuJoueur = joueurs.get(i).mainJoueur;
                Carte carte1 = mainDuJoueur.get(rand.nextInt(mainDuJoueur.size()));
                cartesPermutees.add(carte1);
                mainDuJoueur.remove(carte1);
                Carte carte2 = mainDuJoueur.get(rand.nextInt(mainDuJoueur.size()));
                cartesPermutees.add(carte2);
                mainDuJoueur.remove(carte2);
            }
            //Permutation des cartes
            //S'il y a 3 joueurs, les cartes sont collectées ainsi pour que les joueurs transmettent
            //les cartes au joueur à leur droite:
            if (joueurs.size() == 3) {
                joueurs.get(0).mainJoueur.add(cartesPermutees.get(4));
                joueurs.get(0).mainJoueur.add(cartesPermutees.get(5));
                joueurs.get(1).mainJoueur.add(cartesPermutees.get(0));
                joueurs.get(1).mainJoueur.add(cartesPermutees.get(1));
                joueurs.get(2).mainJoueur.add(cartesPermutees.get(2));
                joueurs.get(2).mainJoueur.add(cartesPermutees.get(3));
            } 
            //S'il y en a 4:
            else if (joueurs.size() == 4) {
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

        //Gestion de l'excuse:
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
            //On l'enleve des cartes jouées pour ne pas l'ajouter au pli du vainquer, afin que 
            //son traitement spécial soit correctement géré.
            cartesJouees.remove(carteExcuse);
        }
        //On attribue le pli à l'équipe gagnante
        if ("Attaquant".equals(joueurGagnant.roleJoueur)) {
            Manche.pliAttaque.addAll(cartesJouees);
        } else {
            Manche.pliDefense.addAll(cartesJouees);
        }
    }

    private boolean estCarteGagnante(Carte carte) {
        
        //L'excuse ne peut jamais remporter de pli
        if (carte.nomCarte.equals("Excuse")) {
            return false;
        }
        //La première carte du pli jouée définit la couleur demandée et gagne par défaut
        if (carteGagnante == null) {
            return true;
        }

        //Si la carte jouée est un atout et que la carte gagnante actuelle n'en est pas un, 
        //ou si elle est d'une valeur supérieure
        if (!excusePassee) {
            if (carte.getType().equals("Atout")) {
                return !carteGagnante.getType().equals("Atout") || carte.getValeur() > carteGagnante.getValeur();
            }
        } 
        //Si l'excuse est passée, il faut maintenant qu'elle soit d'une valeur inférieure
        else {
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

}
