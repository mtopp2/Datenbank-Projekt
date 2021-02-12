package EJB;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.Stundenplansemester;

/**
 * Session Bean implementation class StundenplansemesterFacade
 */
@Stateless
@LocalBean
public class StundenplansemesterFacade extends AbstractFacade<Stundenplansemester> implements StundenplansemesterFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	public StundenplansemesterFacade() {
		super(Stundenplansemester.class);
	}

}
