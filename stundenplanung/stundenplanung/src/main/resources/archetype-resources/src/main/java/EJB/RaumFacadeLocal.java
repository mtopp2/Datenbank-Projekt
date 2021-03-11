package EJB;

import java.util.List;
import javax.ejb.Local;
import model.Raum;

@Local
public interface RaumFacadeLocal {
	
void create(Raum raum);
	
	void edit(Raum raum);
	
	void remove(Raum raum);
	
	Raum find(Object id);
	
	List<Raum> findAll();
	
	List<Raum> findRange(int[] range);
	
	int count();

}
