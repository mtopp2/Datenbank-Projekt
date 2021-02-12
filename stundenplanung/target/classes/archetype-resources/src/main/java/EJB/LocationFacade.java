package EJB;

import javax.ejb.LocalBean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Location;

/**
 * Session Bean implementation class LocationFacade
 */
@Stateless
@LocalBean
public class LocationFacade extends AbstractFacade<Location> implements LocationFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	public LocationFacade() {
		super(Location.class);
	}

}
