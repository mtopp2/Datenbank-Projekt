package EJB;

import javax.ejb.LocalBean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Lehrveranstaltungsart;

/**
 * Session Bean implementation class LehrveranstaltungsartFacade
 */
@Stateless
@LocalBean
public class LehrveranstaltungsartFacade extends AbstractFacade<Lehrveranstaltungsart> implements LehrveranstaltungsartFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	public LehrveranstaltungsartFacade() {
		super(Lehrveranstaltungsart.class);
	}

}
