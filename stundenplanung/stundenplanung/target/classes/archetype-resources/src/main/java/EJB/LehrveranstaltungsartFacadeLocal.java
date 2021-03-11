package EJB;

import java.util.List;

import javax.ejb.Local;

import model.Lehrveranstaltungsart;

@Local
public interface LehrveranstaltungsartFacadeLocal {
	
	void create(Lehrveranstaltungsart lehrveranstaltungsart);
	
	void edit(Lehrveranstaltungsart lehrveranstaltungsart);
	
	void remove(Lehrveranstaltungsart lehrveranstaltungsart);
	
	Lehrveranstaltungsart find(Object id);
	
	List<Lehrveranstaltungsart> findAll();
	
	List<Lehrveranstaltungsart> findRange(int[] range);
	
	int count();

}
