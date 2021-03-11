package EJB;

import javax.ejb.LocalBean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.Faculty;
import model.Raum;

/**
 * Session Bean implementation class RaumFacade
 */
@Stateless
@LocalBean
public class RaumFacade extends AbstractFacade<Raum> implements RaumFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	public RaumFacade() {
		super(Raum.class);
	}

}
