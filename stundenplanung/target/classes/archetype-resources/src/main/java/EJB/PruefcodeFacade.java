package EJB;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.Pruefcode;

/**
 * Session Bean implementation class PruefcodeFacade
 */
@Stateless
@LocalBean
public class PruefcodeFacade extends AbstractFacade<Pruefcode> implements PruefcodeFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	
    public PruefcodeFacade() {
    	super(Pruefcode.class);
    }

}
