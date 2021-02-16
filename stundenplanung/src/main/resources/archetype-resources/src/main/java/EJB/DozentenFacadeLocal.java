package EJB;

import javax.ejb.Local;

import java.util.List;


import model.Dozenten;

@Local
public interface DozentenFacadeLocal {
	
	void create(Dozenten dozenten);
	
	void edit(Dozenten dozenten);
	
	void remove(Dozenten dozenten);
	
	Dozenten find(Object id);
	
	List<Dozenten> findAll();
	
	List<Dozenten> findRange(int[] range);
	
	int count();

}
