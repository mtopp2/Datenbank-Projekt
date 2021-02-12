package EJB;

import javax.ejb.LocalBean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Stundenplanstatus;

/**
 * Session Bean implementation class StundenplanstatusFacade
 */
@Stateless
@LocalBean
public class StundenplanstatusFacade extends AbstractFacade<Stundenplanstatus> implements StundenplanstatusFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	public StundenplanstatusFacade() {
		super(Stundenplanstatus.class);
	}

}
