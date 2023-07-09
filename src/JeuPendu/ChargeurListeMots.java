package JeuPendu;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

public class ChargeurListeMots {
	
	
	public static Vector<String> charge(BufferedReader f)
	{
	Vector<String> liste = new Vector<String>(); 

	String ligneLue;

	try
	    {
	    do                              // on boucle sur les lignes du fichier : on lit le fichier ligne par ligne
	        {
	        ligneLue = f.readLine();    // lecture de la prochaine ligne dans le fichier
	       
	        
	        if (ligneLue != null) {
				String mot = ligneLue.trim(); 
      
				liste.add(mot);                     
			}
	        
	        }
	    while(ligneLue != null);    // tant que le fichier n'est pas entièrement parcouru
	    }
	catch (IOException e)
	    {
	    }

	return liste;
	}
	
}