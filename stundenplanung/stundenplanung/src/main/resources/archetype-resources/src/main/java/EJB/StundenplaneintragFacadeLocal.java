package EJB;

import javax.ejb.Local;
import java.util.List;
import model.Stundenplaneintrag;

@Local
public interface StundenplaneintragFacadeLocal {
	
	void create(Stundenplaneintrag stundenplaneintrag);
	
	void edit(Stundenplaneintrag stundenplaneintrag);
	
	void remove(Stundenplaneintrag stundenplaneintrag);
	
	Stundenplaneintrag find(Object id);
	
	List<Stundenplaneintrag> findAll();
	
	List<Stundenplaneintrag> findRange(int[] range);
	
	int count();
}
