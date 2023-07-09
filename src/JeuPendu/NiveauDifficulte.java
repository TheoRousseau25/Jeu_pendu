package JeuPendu;

public class NiveauDifficulte {
	String libelle;
	int viesMotCourt;
	int viesMotMoyen;
	int viesMotLong;
	
	public NiveauDifficulte(String _libelle, int _viesMotCourt, int _viesMotMoyen, int _viesMotLong) {
		this.libelle = _libelle;
		this.viesMotCourt = _viesMotCourt;
		this.viesMotMoyen = _viesMotMoyen;
		this.viesMotLong = _viesMotLong;
	}


	public String getLibelle() {
		return libelle;
	}


	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}


	public int getViesMotCourt() {
		return viesMotCourt;
	}


	public void setViesMotCourt(int viesMotCourt) {
		this.viesMotCourt = viesMotCourt;
	}


	public int getViesMotMoyen() {
		return viesMotMoyen;
	}


	public void setViesMotMoyen(int viesMotMoyen) {
		this.viesMotMoyen = viesMotMoyen;
	}


	public int getViesMotLong() {
		return viesMotLong;
	}


	public void setViesMotLong(int viesMotLong) {
		this.viesMotLong = viesMotLong;
	}
	
	public NiveauDifficulte clone() {
		return new NiveauDifficulte(this.getLibelle(), this.getViesMotCourt(), this.getViesMotMoyen(), this.getViesMotLong());
	}


	public int jugerDifficulte(String motSecret) {
	    	if(motSecret.length() <= 3) {
	    		return this.getViesMotCourt();
	    	} else if(motSecret.length() <= 6) {
	    		return this.getViesMotMoyen();
	    	} else {
	    		return this.getViesMotLong();
	    	}
	    }
}
