package EJB;

import java.util.List;

import javax.ejb.Local;

import model.Pruefcode;

@Local
public interface PruefcodeFacadeLocal {
	
	void create(Pruefcode pruefcode);
	
	void edit(Pruefcode pruefcode);
	
	void remove(Pruefcode pruefcode);
	
	Pruefcode find(Object id);
	
	List<Pruefcode> findAll();
	
	List<Pruefcode> findRange(int[] range);
	
	int count();

}
