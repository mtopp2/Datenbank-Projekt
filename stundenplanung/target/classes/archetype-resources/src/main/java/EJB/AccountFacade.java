package EJB;

import javax.ejb.LocalBean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Account;

/**
 * Session Bean implementation class AccountFacade
 */
@Stateless
@LocalBean
public class AccountFacade extends AbstractFacade<Account> implements AccountFacadeLocal {

	@PersistenceContext(unitName = "stundenplanung")
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	public AccountFacade() {
		super(Account.class);
	}

}
