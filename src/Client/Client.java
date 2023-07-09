package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
  
public class Client 
{
    public static void main(String[] args) throws IOException 
    {
        try
        {
            Scanner scn = new Scanner(System.in);
              
            InetAddress ip = InetAddress.getLocalHost();
      
            Socket s = new Socket(ip, 5056);
      
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            String recu;
            String envoie;
            int longueur = 0;
      

            while (true) 
            {
                recu = dis.readUTF();
            	System.out.println(recu);
    
                envoie = scn.nextLine();
               
                if(recu.contains("rejouer ou quitter")) {
                	while(!envoie.trim().toUpperCase().equals("!REJOUER") && !envoie.trim().toUpperCase().equals("!QUITTER")) {
                		System.out.println("Réponse non valide ! \nEcrivez '!REJOUER' ou '!QUITTER' :");
                		envoie = scn.nextLine();
                	}
                } else if(recu.contains("Proposez une lettre ou un mot")) {
                	while(!envoie.matches("[a-zA-Z]+") && !envoie.trim().toUpperCase().equals("!QUITTER")) {
                		System.out.println("Réponse non valide ! \nEcrivez une lettre ou un mot :");
                		envoie = scn.nextLine();
                	}
                } else if(recu.contains("Indiquez la longueur")) {
                	while(!envoie.matches("[0-9]+")) {
                		System.out.println("Réponse non valide ! \nIndiquez un chiffre :");
                		envoie = scn.nextLine();
                	}
                	longueur = Integer.parseInt(envoie);
                }else if(recu.contains("Combien de fois") || recu.contains("Indiquez l'indice")) {
                	while(!envoie.matches("[0-9]+")) {
                		System.out.println("Réponse non valide ! \nIndiquez un chiffre :");
                		envoie = scn.nextLine();
                	}
                	
                	while(Integer.parseInt(envoie) < 1 ||  Integer.parseInt(envoie) > longueur ){
                		System.out.println("Réponse non valide avec la longueur du mot secret ! \nIndiquez un autre chiffre :");
                		envoie = scn.nextLine();
                	}
                } else if(recu.contains("se trouve")) {
                	while(!envoie.trim().toUpperCase().equals("OUI") && !envoie.trim().toUpperCase().equals("NON") && !envoie.trim().toUpperCase().equals("!QUITTER")) {
                		System.out.println("Réponse non valide !\nEcrivez 'OUI' ou 'NON' ou !QUITTER' :");
                		envoie = scn.nextLine();
                	}
                } else if(recu.contains("A quel mode de jeu voulez-vous jouer ?")) {
                	while(!envoie.trim().toUpperCase().equals("1") && !envoie.trim().toUpperCase().equals("2")) {
                		System.out.println("Réponse non valide !\nEcrivez '1' ou '2' :");
                		envoie = scn.nextLine();
                	} 
                } else if(recu.contains("multi")) {
                	while(!envoie.trim().toUpperCase().equals("SOLO") && !envoie.trim().toUpperCase().equals("MULTI")) {
                		System.out.println("Réponse non valide !\nEcrivez 'SOLO' ou 'MULTI' :");
                		envoie = scn.nextLine();
                	} 
                } else if (recu.contains("partie publique ou privée")) {
                	while(!envoie.trim().toUpperCase().equals("PUBLIQUE") && !envoie.trim().toUpperCase().equals("PRIVEE")) {
                		System.out.println("Réponse non valide !\nEcrivez 'PUBLIQUE' ou 'PRIVEE' :");
                		envoie = scn.nextLine();
                	} 
                } else if(recu.contains("nouvelle partie")){
                	while(!envoie.trim().toUpperCase().equals("NOUVELLE") && !envoie.trim().toUpperCase().equals("REJOINDRE")) {
                		System.out.println("Réponse non valide !\nEcrivez 'NOUVELLE' ou 'REJOINDRE' :");
                		envoie = scn.nextLine();
                	} 
                } else if(recu.contains("Entrez le numéro")) {
                	while(!envoie.matches("[0-9]+")) {
                		System.out.println("Réponse non valide ! \nIndiquez un chiffre :");
                		envoie = scn.nextLine();
                	}
                }
                
                dos.writeUTF(envoie);

                if(envoie.equals("Exit"))
                {
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }
                  
                String received = dis.readUTF();
                System.out.println(received);
            }
              
            scn.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}