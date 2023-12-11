package fr.pantheonsorbonne.miage.game;

// Classe principale, c'est ici que le programme commence son exécution.
public class Application {
    public static void main(String... args) {
        //Création d'un deck
        Deck deckTarot = new Deck();
        //Création d'une nouvelle partie
        Partie partie = new Partie(3, deckTarot, 10); 
        //(nombre de joueurs, deck, nombre de manches)
    }
}
