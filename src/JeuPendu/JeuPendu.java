package JeuPendu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;




public class JeuPendu {
	
	String motSecret ="";
	String motCrypte="";
	Vector<String> lettresProposees = new Vector<String>();
	Vector<String> listeMots = new Vector<String>();
	ChargeurListeMots chargeur = new ChargeurListeMots();
	int nbVies;
	public Vector<NiveauDifficulte> niveaux = new Vector<NiveauDifficulte>();
	NiveauDifficulte niveau;
	int chrono;
	List<String> alphabet = new LinkedList<String>(Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"));
	
    public String getMotSecret() {
		return motSecret;
	}

	public String getMotCrypte() {
		return motCrypte;
	}
	
	public int getNbVies() {
		return nbVies;
	}
	
	public NiveauDifficulte getNiveau() {
		return niveau;
	}
	
	public int getChrono() {
		return chrono;
	}
	
	public void setMotSecret(String _motSecret) {
		this.motSecret = _motSecret;
        this.motCrypte = "";
    	for(int i =0; i<_motSecret.length();i++) {
    		this.motCrypte += "_";
    	}	
    	this.lettresProposees.clear();
	}
	
	public void setMotSecret2(int longueur) {
		this.motSecret = "";
        this.motCrypte = "";
    	for(int i =0; i<longueur;i++) {
    		this.motCrypte += "_";
    	}	
    	this.lettresProposees.clear();
	}

	public void setMotCrypte(String _motCrypte) {
		this.motCrypte = _motCrypte;
	}
	
	public void setNbVies(int _nbVies) {
		this.nbVies = _nbVies;
	}
	
	public void setNiveau(String libelle) {
		for(NiveauDifficulte niveau : niveaux) {
			if(niveau.getLibelle().trim().toUpperCase().equals(libelle)) {
				this.niveau = niveau.clone();
			}
		}	
	}
	
	public void setChrono(int _chrono) {
		this.chrono = _chrono;
	}
	
	public void setAlphabet(List<String> alphabet) {
		this.alphabet = alphabet;
	}
	
	public void enleverVie() {
		this.nbVies --;
	}
	
	public void chargerListeMots() throws FileNotFoundException {
		 File chemin = new File("");             // racine du projet : endroit d'où est lancée la JVM
		 File cheminDonnées = new File(chemin.getAbsoluteFile(),  File.separatorChar + "donnees"); // chemin du répertoire des données
		 File cheminG1 = new File(cheminDonnées.getAbsoluteFile(),"listemots" + this.getNiveau().getLibelle().replaceAll(" ", "") + ".txt");
		 BufferedReader f1 = new BufferedReader( new InputStreamReader( new FileInputStream(cheminG1.getAbsolutePath())));  // on ouvre le fichier de données en lecture de texte
		 this.listeMots = chargeur.charge(f1); // on charge la liste de verbes du 1er groupe	    
	}
	
	public void choisirMot() {
		this.setMotSecret(this.listeMots.get((int) (Math.random() * ( this.listeMots.size() - 1 ))));
	}
	
	public void changerMotSecret(char c) {
    	for(int i =0; i<this.motSecret.length();i++) {
    		if(this.motSecret.charAt(i)==c) {
    			this.motCrypte = this.motCrypte.substring(0, i) + c + this.motCrypte.substring(i + 1);
    		}
    	}
    }

	public boolean verifieLettre(String c) {
    	return this.motSecret.contains(c);	
    }

    public boolean verifierMot(String mot) {
    	if(this.motSecret.equals(mot)) {
    		this.motCrypte = this.motSecret;
    		return true;
    	}
    	return false;
    }
    
    public boolean verifierLettresProposees(String lettre) {
    	return this.lettresProposees.contains(lettre);
    }
    
    public boolean verifierVictoire() {
    	return this.motCrypte.contains("_"); 	
    }
    
    public String afficherMotCrypte() {
    	String vueMotCrypte = "";
    	for(int i=0;i<this.motCrypte.length();i++) {
    		if(this.motCrypte.charAt(i) == '_') {
    		vueMotCrypte += "_ ";
    	    } else {
    		    vueMotCrypte += this.motCrypte.charAt(i);
    	    }
    	}
    	return vueMotCrypte;
    }
    
    public void ajouterLettre(String lettre) {
    	this.lettresProposees.add(lettre);
    }
    
    public void ajouterDifficulte(NiveauDifficulte niveau) {
    	this.niveaux.add(niveau);
    }
      
    public String afficherLettresProposees() {
    	if(this.lettresProposees.size() != 0) {
    		return this.lettresProposees.toString();
    	} else {
    		return "";
    	}
    }
    
    public String libellesNiveaux() {
    	String libelles ="";
    	for(NiveauDifficulte niveau : niveaux) {
    		libelles += niveau.getLibelle() + ", ";
    	}
    	return libelles.substring(0,libelles.length()-2);
    }
    
    public boolean verifierLibelle(String libelle) {
    	for(NiveauDifficulte niveau : niveaux) {
    		if(niveau.getLibelle().trim().toUpperCase().equals(libelle)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public String construirePendu() {
    	
    		  if(this.nbVies == 0) {
    	    		return    "  +---+\r\n"
    	    				+ "  |   |\r\n"
    	    				+ "  O   |\r\n"
    	    				+ " /|\\  |\r\n"
    	    				+ " / \\  |\r\n"
    	    				+ "      |";	 
    	    	} else if(this.nbVies == 1) {
    	    		return    "  +---+\r\n"
    	    				+ "  |   |\r\n"
    	    				+ "  O   |\r\n"
    	    				+ " /|\\  |\r\n"
    	    				+ " /    |\r\n"
    	    				+ "      |";
    	    	}else if(this.nbVies == 2) {
    	    		return    "  +---+\r\n"
    	    				+ "  |   |\r\n"
    	    				+ "  O   |\r\n"
    	    				+ " /|\\  |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |";
    	    	}else if(this.nbVies == 3) {
    	    		return    "  +---+\r\n"
    	    				+ "  |   |\r\n"
    	    				+ "  O   |\r\n"
    	    				+ " /|   |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |";
    	    	}else if(this.nbVies == 4) {
    	    		return    "  +---+\r\n"
    	    				+ "  |   |\r\n"
    	    				+ "  O   |\r\n"
    	    				+ "  |   |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |";
    	    	}else if(this.nbVies == 5) {
    	    		return    "  +---+\r\n"
    	    				+ "  |   |\r\n"
    	    				+ "  O   |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |";
    	    	}else if(this.nbVies == 6){
    	    		return    "  +---+\r\n"
    	    				+ "  |   |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |";
    	    	} else if(this.nbVies == 7){
    	    		return    "  +---+\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |";
    	    	} else if(this.nbVies == 8){
    	    		return    "       \r\n"
    	    				+ "      |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |\r\n"
    	    				+ "      |";
    	    	} else {
    	    		return "";
    	    	}
    	}
    
    public String choisirLettre() {
    	Random rand = new Random();
        String lettre = this.alphabet.get(rand.nextInt(this.alphabet.size()));
    	this.alphabet.remove(lettre);
        return lettre;
    }

}
