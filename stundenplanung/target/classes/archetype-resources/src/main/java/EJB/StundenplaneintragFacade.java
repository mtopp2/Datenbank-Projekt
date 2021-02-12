package EJB;

import javax.ejb.LocalBean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.Stundenplaneintrag;

/**
 * Session Bean implementation class StundenplaneintragFacade
 */
@Stateless
@LocalBean
public class StundenplaneintragFacade extends AbstractFacade<Stundenplaneintrag> implements StundenplaneintragFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	public StundenplaneintragFacade() {
		super(Stundenplaneintrag.class);
	}

}
