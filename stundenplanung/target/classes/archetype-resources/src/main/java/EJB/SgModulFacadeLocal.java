package EJB;

import javax.ejb.Local;

import model.Sgmodul;

import java.util.List;

@Local
public interface SgModulFacadeLocal {
	
	void create(Sgmodul sgmodul);
	
	void edit(Sgmodul sgmodul);
	
	void remove(Sgmodul sgmodul);
	
	Sgmodul find(Object id);
	
	List<Sgmodul> findAll();
	
	List<Sgmodul> findRange(int[] range);
	
	int count();

}
