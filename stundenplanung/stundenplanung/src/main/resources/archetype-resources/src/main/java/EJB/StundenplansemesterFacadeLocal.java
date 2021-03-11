package EJB;

import java.util.List;

import javax.ejb.Local;

import model.Stundenplansemester;

@Local
public interface StundenplansemesterFacadeLocal {

	void create(Stundenplansemester stundenplansemester);
	
	void edit(Stundenplansemester stundenplansemester);
	
	void remove(Stundenplansemester stundenplansemester);
	
	Stundenplansemester find(Object id);
	
	List<Stundenplansemester> findAll();
	
	List<Stundenplansemester> findRange(int[] range);
	
	int count();
}
