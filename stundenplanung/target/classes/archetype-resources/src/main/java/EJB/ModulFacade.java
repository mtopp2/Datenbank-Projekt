package EJB;

import javax.ejb.LocalBean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Modul;

/**
 * Session Bean implementation class ModulFacade
 */
@Stateless
@LocalBean
public class ModulFacade extends AbstractFacade<Modul> implements ModulFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	public ModulFacade() {
		super(Modul.class);
	}

}
