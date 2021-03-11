package EJB;

import javax.ejb.Local;
import java.util.List;
import model.Faculty;

@Local
public interface FacultyFacadeLocal {
	
	void create(Faculty faculty);
	
	void edit(Faculty faculty);
	
	void remove(Faculty faculty);
	
	Faculty find(Object id);
	
	List<Faculty> findAll();
	
	List<Faculty> findRange(int[] range);
	
	int count();
}
