package EJB;

import java.util.List;

import javax.ejb.Local;

import model.Modul;

@Local
public interface ModulFacadeLocal {
	
	void create(Modul modul);
	
	void edit(Modul modul);
	
	void remove(Modul modul);
	
	Modul find(Object id);
	
	List<Modul> findAll();
	
	List<Modul> findRange(int[] range);
	
	int count();

}
