package EJB;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.Benutzergruppe;
import model.Dozenten;

/**
 * Session Bean implementation class DozentenFacade
 */
@Stateless
@LocalBean
public class DozentenFacade extends AbstractFacade<Dozenten> implements DozentenFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
    public DozentenFacade() {
    	super(Dozenten.class);
    }

}
