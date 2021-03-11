package EJB;

import javax.ejb.LocalBean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Faculty;

/**
 * Session Bean implementation class FacultyFacade
 */
@Stateless
@LocalBean
public class FacultyFacade extends AbstractFacade<Faculty> implements
FacultyFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	public FacultyFacade() {
		super(Faculty.class);
	}

}
