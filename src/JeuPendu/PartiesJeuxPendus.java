package JeuPendu;

import java.io.FileNotFoundException;
import java.util.Vector;

public class PartiesJeuxPendus {
	
    Vector<JeuPendu> jeuxPrives = new Vector<JeuPendu>();
    
  //Fonction pour initialiser une partie par défaut
  	public static void initialiserJeuMultijoueurs(JeuPendu jeu) throws FileNotFoundException {
  	      jeu.ajouterDifficulte(new NiveauDifficulte("Moyen",5,6,7));
  	      jeu.setNiveau("MOYEN");
  		  jeu.chargerListeMots();
  	      jeu.choisirMot();
  	      jeu.setNbVies(jeu.getNiveau().jugerDifficulte("MOYEN"));		
  	}
  	
  	public void ajouterPartie(JeuPendu pendu) {
  		this.jeuxPrives.add(pendu);
  	}
  	
  	public JeuPendu getPartie(int indice) {
  		return jeuxPrives.get(indice);
  	}
  	
  	public Vector<JeuPendu> getListeParties(){
  		return this.jeuxPrives;
  	}


}
