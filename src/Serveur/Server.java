package Serveur;

import java.io.*;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


import JeuPendu.JeuPendu;
import JeuPendu.NiveauDifficulte;
import JeuPendu.PartiesJeuxPendus;

import java.net.*;
  
// Server class
public class Server 
{	
    public static void main(String[] args) throws IOException 
    {
    	ServerSocket ss = new ServerSocket(5056);
        String recu;
        PartiesJeuxPendus jeuxPendus = new PartiesJeuxPendus();
        //Partie de pendue publique
        jeuxPendus.ajouterPartie(new JeuPendu());
        PartiesJeuxPendus.initialiserJeuMultijoueurs(jeuxPendus.getPartie(0));
        
       //Boucle infinie pour récupérer les requêtes clients
       while (true) 
        {
            Socket s = null;            
            try 
            {
                // object socket pour recevoir les requêtes clients
            	s = ss.accept();
                  
               System.out.println("Un nouveau client est connecté : " + s);
               DataInputStream dis = new DataInputStream(s.getInputStream());
               DataOutputStream dos = new DataOutputStream(s.getOutputStream());
               
               dos.writeUTF("Voulez-vous jouer en solo ou en multijoueurs ? [SOLO | MULTI]");
               recu = dis.readUTF().trim().toUpperCase();
               
               if(recu.equals("SOLO")) {
                   dos.writeUTF("Partie choisie : solo !");
            	   Thread t = new ClientHandler(s, dis, dos); 
                   t.start();   
               } else if(recu.equals("MULTI")) {
                   dos.writeUTF("Partie choisie : multijoueurs !");
                   dos.writeUTF("Voulez-vous jouer une partie publique ou privée ? [PUBLIQUE | PRIVEE]");
                   recu = dis.readUTF().trim().toUpperCase();                   
                   if(recu.equals("PUBLIQUE")) {
                       dos.writeUTF("Partie choisie : publique !");
                	   Thread t = new ClientHandlerMultiplayer(s, dis, dos, jeuxPendus.getPartie(0)); 
                       t.start();  
                   } else {
                	   dos.writeUTF("Partie choisie : privée !");
                	   dos.writeUTF("Voulez-vous créer une nouvelle partie ou en rejoindre une ? [NOUVELLE | REJOINDRE]");
                       recu = dis.readUTF().trim().toUpperCase();                   
                	   if(recu.equals("NOUVELLE")) {
                		   jeuxPendus.ajouterPartie(new JeuPendu());
                    	   dos.writeUTF("Numéro de la partie : " + jeuxPendus.getListeParties().size());
                           PartiesJeuxPendus.initialiserJeuMultijoueurs(jeuxPendus.getListeParties().lastElement());
                    	   Thread t = new ClientHandlerMultiplayer(s, dis, dos, jeuxPendus.getListeParties().lastElement()); 
                           t.start();
                	   } else {
                    	   dos.writeUTF("Connexion à une partie en cours !");
                    	   dos.writeUTF("Entrez le numéro de la partie : ");
                           recu = dis.readUTF().trim().toUpperCase(); 
                    	   dos.writeUTF("Numéro de partie choisie : " + recu);
                    	   while(Integer.parseInt(recu) <= 0 || Integer.parseInt(recu) > jeuxPendus.getListeParties().size()) {
                        	   dos.writeUTF("Cette partie n'existe pas ! Entrez le numéro de la partie : ");
                               recu = dis.readUTF().trim().toUpperCase(); 
                        	   dos.writeUTF("Tentative de connexion échoué");
                    	   } 
                    	   Thread t = new ClientHandlerMultiplayer(s, dis, dos, jeuxPendus.getPartie(Integer.parseInt(recu)-1)); 
                           t.start(); 
                    	   }       
                	   }	     
                   }
            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
                ss.close();
                 }
            }
    }
}

  
// ClientHandler class
class ClientHandler extends Thread 
{
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    //ScheduleExecutor pour décrémenter le chrono
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    JeuPendu pendu = new JeuPendu();
    
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) throws FileNotFoundException 
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.pendu.ajouterDifficulte(new NiveauDifficulte("Très facile",7, 8, 9));
        this.pendu.ajouterDifficulte(new NiveauDifficulte("Facile",6,7,8));
        this.pendu.ajouterDifficulte(new NiveauDifficulte("Moyen",5,6,7));
        this.pendu.ajouterDifficulte(new NiveauDifficulte("Difficile",4,5,6));
        this.pendu.ajouterDifficulte(new NiveauDifficulte("Très difficile",3,4,5));
    }
    
    public void quitter() throws IOException {
    	System.out.println("Client " + this.s + " souhaite stopper la connexion...");
        System.out.println("Fermeture de la connexion.");
        this.s.close();
        System.out.println("Connexion interommpue.");
    }
    
    public void choisirDifficulte() throws IOException {
    	String recu="";
    	while(!pendu.verifierLibelle(recu)) {
    	    dos.writeUTF("Choisir un niveau de difficulté : " + pendu.libellesNiveaux());
            recu = dis.readUTF().trim().toUpperCase();
            if(pendu.verifierLibelle(recu)) {
                pendu.setNiveau(recu);
                pendu.setNbVies(pendu.getNiveau().jugerDifficulte(recu));
                dos.writeUTF("Niveau choisi : " + pendu.getNiveau().getLibelle());
            } else {
                dos.writeUTF("Ce niveau de difficulté n'existe pas.");
            }
    	}
    }
    
    public void rejouerPartie() {
  		this.pendu.choisirMot();
	    this.pendu.setAlphabet(new LinkedList<String>(Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")));  
    }
    
    public void commencerChrono() {
    	if(pendu.getNiveau() != null) {
    		pendu.setChrono(180);
            final Runnable runnable = new Runnable() {
                public void run() {
                	pendu.setChrono(pendu.getChrono()-1);
                    if (pendu.getChrono() < 0) {
                        pendu.setNbVies(0);
                    }
                }         
            };
            scheduler.scheduleAtFixedRate(runnable, 0, 1, SECONDS);
            
        }  	
    }
  
    @Override
    public void run() 
    {   
        String recu; 
        String recu2;
        //Booléens pour définir le mode de jeu
        boolean mode1, mode2;
        
        try {
            while(true) {
            	
            dos.writeUTF("A quel mode de jeu voulez-vous jouer ? Deviner un mot secret (1) ou bien faire deviner un mot secret (2) ? [1 | 2]");
            recu = dis.readUTF().trim().toUpperCase();
			
			mode1 = true;
        
            if(recu.equals("1")) {
    			dos.writeUTF("Mode de jeu choisi : deviner le mot secret !");

		        choisirDifficulte();
		        
		        this.pendu.chargerListeMots();
		        this.pendu.choisirMot();
        
                commencerChrono();
        
                while (mode1) 
                {        
            	    if(pendu.verifierVictoire() && pendu.getNbVies() > 0) {
                        dos.writeUTF("Mot à deviner : " + pendu.afficherMotCrypte() + "\nLettres déjà proposées : " + pendu.afficherLettresProposees() +"\nEtat du pendu : \n" + pendu.construirePendu() + "\nTemps restant : " + pendu.getChrono() + " secondes\nProposez une lettre ou un mot, tapez '!QUITTER' pour quitter le jeu :");
                        recu = dis.readUTF().trim().toUpperCase();
                        if(recu.equals("!QUITTER"))
                        { 
                            quitter();
                            break;
                        } else if(recu.length()>1) {
                	        if(!pendu.verifierMot(recu)) {
                		        dos.writeUTF("Mauvaise réponse !");
                		        pendu.enleverVie();
                	        } else {
                		        dos.writeUTF("Bonne réponse !");
                	        }
                        } else if(recu.length()== 1) {
                	        if(pendu.verifierLettresProposees(recu)) {
                	    	    dos.writeUTF("Lettre déjà proposée !");
                	        } else if(!pendu.verifieLettre(recu)) {
                	    	    dos.writeUTF("Mauvaise réponse !");
                	    	    pendu.ajouterLettre(recu);
                	    	    pendu.enleverVie();
                	        }else {
                	    	    dos.writeUTF("Bonne réponse !");
                		        pendu.changerMotSecret(recu.charAt(0));
                		        pendu.ajouterLettre(recu);
                	        }
                        } 
            	        } else {
            		        if(pendu.getNbVies() != 0 && pendu.getChrono()> 0 ) {
            			        dos.writeUTF(pendu.getMotSecret() + "\nBravo, vous avez gagné !\nVoulez-vous rejouer ou quitter ? [!REJOUER | !QUITTER]");
            					recu = dis.readUTF().trim().toUpperCase();
                    			dos.writeUTF("Vous avez choisi de " + recu.toLowerCase() + " !");
                                mode1 = false;
            		        } else if(pendu.getChrono()<0) {
            			        dos.writeUTF(pendu.construirePendu() + "\nLe temps limite est écoulé ! Le mot était '" + pendu.getMotSecret() + "'\nVoulez-vous rejouer ou quitter ? [!REJOUER | !QUITTER]");
            					recu = dis.readUTF().trim().toUpperCase();
            			        dos.writeUTF("Vous avez choisi de " + recu.toLowerCase() + " !");
            			        mode1 = false;
            		        } else {
            			        dos.writeUTF(pendu.construirePendu() + "\nDommage, vous avez perdu ! Le mot était '" + pendu.getMotSecret() + "'\nVoulez-vous rejouer ou quitter ? [!REJOUER | !QUITTER]");
            			        recu = dis.readUTF().trim().toUpperCase();
            			        dos.writeUTF("Vous avez choisi de " + recu.toLowerCase() + " !");
            			        mode1 = false;
            		        }
            	        } 
                }
            } else {   		
            	dos.writeUTF("Mode de jeu choisi : faire deviner le mot !");
    			pendu.setNbVies(9);
    			dos.writeUTF("Indiquez la longueur du mot secret : ");
    			
    			recu = dis.readUTF().trim().toUpperCase();
    			
    			pendu.setMotSecret2(Integer.parseInt(recu));
    			
    			dos.writeUTF("Début du jeu !");
    			
    			mode2 = true;
    			
    			while (mode2) 
                {  
    				String lettre = pendu.choisirLettre();
    				
        			dos.writeUTF("Etat du pendu : \n" + pendu.construirePendu() + "\nMot à deviner : " + pendu.afficherMotCrypte() + "\nEst ce que la lettre '" + lettre + "' se trouve dans le mot ? [OUI | NON | !QUITTER]");

        			recu = dis.readUTF().trim().toUpperCase();
       
        			if(recu.equals("OUI")) {
        				dos.writeUTF("Bonne réponse du serveur !");
            			dos.writeUTF("Combien de fois la lettre se trouve dans le mot ?");
            			recu = dis.readUTF().trim().toUpperCase();
            			dos.writeUTF("La lettre '" + lettre +"' se trouve " + recu + " fois dans le mot.");
            			for(int i = 0; i < Integer.parseInt(recu);i++) {
                			dos.writeUTF("Indiquez l'indice de la lettre dans le mot : " + pendu.afficherMotCrypte());
                			recu2 = dis.readUTF().trim().toUpperCase();
                	        pendu.setMotCrypte(pendu.getMotCrypte().substring(0, Integer.parseInt(recu2)-1) + lettre + pendu.getMotCrypte().substring(Integer.parseInt(recu2)));
                			dos.writeUTF("Mot modifié :" + pendu.afficherMotCrypte());
            			}

        			} else if(recu.equals("NON")) {
            			dos.writeUTF("Erreur de ma part !");
            			pendu.enleverVie();
        			} else {
        				quitter();
        				break;
        			}
        			
        			if(!pendu.verifierVictoire()) {
        				dos.writeUTF("Votre mot secret est : " + pendu.afficherMotCrypte() +" ! Partie terminée.\nVoulez-vous rejouer ou quitter ? [!REJOUER | !QUITTER]");
        				recu = dis.readUTF().trim().toUpperCase();
    			        dos.writeUTF("Vous avez choisi de " + recu.toLowerCase() + " !");
    			        mode2 = false;
        			} else if(pendu.getNbVies() == 0) {
            			dos.writeUTF("Je n'ai pas pas réussi à deviner votre mot secret ! Partie terminée. \nVoulez-vous rejouer ou quitter ? [!REJOUER | !QUITTER]");
            			recu = dis.readUTF().trim().toUpperCase();
    			        dos.writeUTF("Vous avez choisi de " + recu.toLowerCase() + " !");
            			mode2 = false;
        			}       			
                }
            }
            if(recu.equals("!REJOUER")) {
            	rejouerPartie();
            } else {
            	quitter();
            }
       	     
          }
        } catch (IOException e) {
            e.printStackTrace();
        }
          
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();
              
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

class ClientHandlerMultiplayer extends Thread 
{
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    JeuPendu pendu ;
    
    // Constructor
    public ClientHandlerMultiplayer(Socket s, DataInputStream dis, DataOutputStream dos, JeuPendu pendu) throws FileNotFoundException 
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.pendu = pendu;
    }
    
    public void quitter() throws IOException {
    	System.out.println("Client " + this.s + " souhaite stopper la connexion...");
        System.out.println("Fermeture de la connexion.");
        this.s.close();
        System.out.println("Connexion interommpue.");
    }
    
    public void rejouer(String message) throws IOException {
    	    if(message.equals("!REJOUER")) {
 		        this.pendu.setNiveau("MOYEN");
 		        this.pendu.setNbVies(pendu.getNiveau().jugerDifficulte("MOYEN"));
 		        this.pendu.choisirMot();
 		        this.pendu.setChrono(180);
 	        } else {
 		        quitter();
 	        }
    }
     
    public void commencerChrono() {
    	if(pendu.getNiveau() != null) {
    		pendu.setChrono(180);
            final Runnable runnable = new Runnable() {
                public void run() {
                	pendu.setChrono(pendu.getChrono()-1);
                    if (pendu.getChrono() < 0) {
                        pendu.setNbVies(0);
                        //scheduler.shutdown();
                    }
                }         
            };
            scheduler.scheduleAtFixedRate(runnable, 0, 1, SECONDS);
            
        }  	
    }
  
    @Override
    public void run() 
    {   
        String recu; 
        
        try {
        
                commencerChrono();
        
                while (true) 
                {        
            	    if(pendu.verifierVictoire() && pendu.getNbVies() > 0) {
                        dos.writeUTF("Mot à deviner : " + pendu.afficherMotCrypte() + "\nLettres déjà proposées : " + pendu.afficherLettresProposees() +"\nEtat du pendu : \n" + pendu.construirePendu() + "\nTemps restant : " + pendu.getChrono() + " secondes\nProposez une lettre ou un mot, tapez '!QUITTER' pour quitter le jeu :");
                        recu = dis.readUTF().trim().toUpperCase();
                        if(recu.equals("!QUITTER"))
                        { 
                            quitter();
                            break;
                        } else if(recu.length()>1) {
                	        if(!pendu.verifierMot(recu)) {
                		        dos.writeUTF("Mauvaise réponse !");
                		        pendu.enleverVie();
                	        } else {
                		        dos.writeUTF("Bonne réponse !");
                	        }
                        } else if(recu.length()== 1) {
                	        if(pendu.verifierLettresProposees(recu)) {
                	    	    dos.writeUTF("Lettre déjà proposée !");
                	        } else if(!pendu.verifieLettre(recu)) {
                	    	    dos.writeUTF("Mauvaise réponse !");
                	    	    pendu.ajouterLettre(recu);
                	    	    pendu.enleverVie();
                	        }else {
                	    	    dos.writeUTF("Bonne réponse !");
                		        pendu.changerMotSecret(recu.charAt(0));
                		        pendu.ajouterLettre(recu);
                	        }
                        } 
            	        } else {
            		        if(pendu.getNbVies() != 0 && pendu.getChrono()> 0 ) {
            			        dos.writeUTF(pendu.getMotSecret() + "\nBravo, vous avez gagné ! ");
                                rejouer("!REJOUER");
            		        } else if(pendu.getChrono()<0) {
            			        dos.writeUTF(pendu.construirePendu() + "\nLe temps limite est écoulé ! Le mot était '" + pendu.getMotSecret());
            		            rejouer("!REJOUER");
            		        } else {
            			        dos.writeUTF(pendu.construirePendu() + "\nDommage, vous avez perdu ! Le mot était '" + pendu.getMotSecret());
            		            rejouer("!REJOUER");
            		        }
            	        } 
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
          
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();
              
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

