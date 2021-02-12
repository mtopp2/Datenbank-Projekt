package EJB;

import java.util.List;

import javax.ejb.Local;

import model.Benutzergruppe;

@Local
public interface BenutzergruppeFacadeLocal {
	
	void create(Benutzergruppe benutzergruppe);
	
	void edit(Benutzergruppe benutzergruppe);
	
	void remove(Benutzergruppe benutzergruppe);
	
	Benutzergruppe find(Object id);
	
	List<Benutzergruppe> findAll();
	
	List<Benutzergruppe> findRange(int[] range);
	
	int count();

}
