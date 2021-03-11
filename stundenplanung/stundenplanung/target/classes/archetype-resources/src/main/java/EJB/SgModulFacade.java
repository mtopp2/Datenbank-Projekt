package EJB;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import model.Sgmodul;

/**
 * Session Bean implementation class SgModulFacade
 */
@Stateless
@LocalBean
public class SgModulFacade extends AbstractFacade<Sgmodul> implements SgModulFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
    public SgModulFacade() {
    	super(Sgmodul.class);
	}

}
