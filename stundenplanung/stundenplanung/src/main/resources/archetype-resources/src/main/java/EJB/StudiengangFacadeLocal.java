package EJB;

import java.util.List;

import javax.ejb.Local;

import model.Studiengang;

@Local
public interface StudiengangFacadeLocal {
	
void create(Studiengang studiengang);
	
	void edit(Studiengang studiengang);
	
	void remove(Studiengang studiengang);
	
	Studiengang find(Object id);
	
	List<Studiengang> findAll();
	
	List<Studiengang> findRange(int[] range);
	
	int count();

}
