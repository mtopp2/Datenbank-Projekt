package EJB;

import java.util.List;

import javax.ejb.Local;

import model.Location;

@Local
public interface LocationFacadeLocal {
	
	void create(Location location);

	void edit(Location location);
	
	void remove(Location location);
	
	Location find(Object id);
	
	List<Location> findAll();
	
	List<Location> findRange(int[] range);
	
	int count();

}
