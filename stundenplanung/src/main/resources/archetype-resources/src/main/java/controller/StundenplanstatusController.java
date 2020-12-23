package controller;


import model.Stundenplanstatus;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.primefaces.event.RowEditEvent;
import javax.faces.bean.ManagedBean;
import controller.MessageForPrimefaces;

/**
*
* @author Manuel
*/

@ManagedBean(name="StundenplanstatusController")
@SessionScoped
public class StundenplanstatusController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Stundenplanstatus stundenplanstatus;
	
	@PostConstruct
    public void init() {
        spslist = getStundenplanstatusList();
    }
 
	
	private String PColor;
	private String SPSTBezeichnung;
	private String SPSTHint;
	private boolean PColor_ok = false;
	private boolean SPSTBezeichnung_ok = false;
	private boolean SPSTHint_ok = false;
	
	List<Stundenplanstatus> spslist;
	
	private Stundenplanstatus selectedsps;

	public Stundenplanstatus getStundenplanstatus() {
		return stundenplanstatus;
	}

	public void setStundenplanstatus(Stundenplanstatus stundenplanstatus) {
		this.stundenplanstatus = stundenplanstatus;
	}

	public String getPColor() {
		return PColor;
	}

	public void setPColor(String PColor) {
		if(PColor!=null){
			this.PColor = PColor;
			PColor_ok = true;
		}
		/*else{
			FacesMessage message = new FacesMessage("Farbe bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("StundenplanstatusForm:PColor_reg", message);
	    }*/
	}

	public String getSPSTBezeichnung() {
		return SPSTBezeichnung;
	}

	public void setSPSTBezeichnung(String SPSTBezeichnung) {
		if(SPSTBezeichnung!=null){
			this.SPSTBezeichnung = SPSTBezeichnung;
			SPSTBezeichnung_ok = true;
		}
		/*else{
			FacesMessage message = new FacesMessage("Bezeichnung bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("StundenplanstatusForm:SPSTBezeichnung_reg", message);
	    }*/
	}

	public String getSPSTHint() {
		return SPSTHint;
	}

	public void setSPSTHint(String SPSTHint) {
		if(SPSTHint!=null){
			this.SPSTHint = SPSTHint;
			SPSTHint_ok = true;
		}
		/*else{
			FacesMessage message = new FacesMessage("Hinweis bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("StundenplanstatusForm:SPSTHint_reg", message);
	    }*/
	}

	public Stundenplanstatus getSelectedsps() {
		return selectedsps;
	}

	public void setSelectedsps(Stundenplanstatus selectedsps) {
		this.selectedsps = selectedsps;
	}

	public List<Stundenplanstatus> getSpslist() {
		return spslist;
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createStundenplanstatus() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Stundenplanstatus sps = new Stundenplanstatus();  
		sps.setSPSTBezeichnung(SPSTBezeichnung);
		sps.setSPSTHint(SPSTHint);
		sps.setPColor(PColor);
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(sps);  
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
		em.close();
	}
	
	public String createDoStundenplanstatus() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(SPSTBezeichnung_ok == true && SPSTHint_ok == true && PColor_ok == true) {
			createStundenplanstatus();
			return "showstundenplanstatus.xhtml";
		}
		else{
			return "createstundenplanstatus.xhtml";
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Stundenplanstatus> getStundenplanstatusList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Stundenplanstatus> query = em.createNamedQuery("Stundenplanstatus.findAll", Stundenplanstatus.class);
		spslist = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowEdit(RowEditEvent<Stundenplanstatus> event) {
        FacesMessage msg = new FacesMessage("Stundenplanstatus Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        Stundenplanstatus newsps = new Stundenplanstatus();
        newsps = event.getObject();
        
        try {
	        ut.begin();
	        EntityManager em = emf.createEntityManager();
	        em.find(Stundenplanstatus.class, newsps.getSpstid());
	        stundenplanstatus.setSpstid(newsps.getSpstid());
	        stundenplanstatus.setPColor(newsps.getPColor());
	        stundenplanstatus.setSPSTBezeichnung(newsps.getSPSTBezeichnung());
	        stundenplanstatus.setSPSTHint(newsps.getSPSTHint());
	        em.merge(stundenplanstatus);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
    }
     
    public void onRowCancel(RowEditEvent<Stundenplanstatus> event) {
    	FacesMessage msg = new FacesMessage("Stundenplanstatus Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteStundenplanstatus() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        spslist.remove(selectedsps);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Stundenplanstatus> q = em.createNamedQuery("Stundenplanstatus.findBySpsid",Stundenplanstatus.class);
        q.setParameter("spstid", selectedsps.getSpstid());
        stundenplanstatus = (Stundenplanstatus)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(stundenplanstatus);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
        selectedsps = null;
		em.close();
    }

}
