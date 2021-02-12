package EJB;

import javax.ejb.LocalBean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Benutzergruppe;

/**
 * Session Bean implementation class BenutzergruppeFacade
 */
@Stateless
@LocalBean
public class BenutzergruppeFacade extends AbstractFacade<Benutzergruppe> implements BenutzergruppeFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	public BenutzergruppeFacade() {
		super(Benutzergruppe.class);
	}

}
