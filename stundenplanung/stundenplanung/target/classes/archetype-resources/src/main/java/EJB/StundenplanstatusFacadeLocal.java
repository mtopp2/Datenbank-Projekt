package EJB;

import java.util.List;

import javax.ejb.Local;

import model.Stundenplanstatus;

@Local
public interface StundenplanstatusFacadeLocal {

	void create(Stundenplanstatus stundenplanstatus);
	
	void edit(Stundenplanstatus stundenplanstatus);
	
	void remove(Stundenplanstatus stundenplanstatus);
	
	Stundenplanstatus find(Object id);
	
	List<Stundenplanstatus> findAll();
	
	List<Stundenplanstatus> findRange(int[] range);
	
	int count();
}
