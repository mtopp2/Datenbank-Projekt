package EJB;

import javax.ejb.LocalBean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Studiengang;

/**
 * Session Bean implementation class StudiengangFacade
 */
@Stateless
@LocalBean
public class StudiengangFacade extends AbstractFacade<Studiengang> implements StudiengangFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	public StudiengangFacade() {
		super(Studiengang.class);
	}

}
