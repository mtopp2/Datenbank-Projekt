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
import org.primefaces.event.SelectEvent;

import javax.faces.bean.ManagedBean;
import controller.MessageForPrimefaces;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import EJB.StundenplanstatusFacadeLocal;

/**
*
* @author Anil
*/

//@ManagedBean(name="StundenplanstatusController")
@Named(value="stundenplanstatusController")
@SessionScoped
public class StundenplanstatusController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Stundenplanstatus stundenplanstatus;
	
	@EJB
	private StundenplanstatusFacadeLocal stundenplanstatusFacadeLocal;
	
	@PostConstruct
    public void init() {
		scheduleStatusList = getStundenplanstatusList();
    }
 
	
	private String statusColor;
	private String statusDescription;
	private String statusHint;
	private boolean statusColorOk = false;
	private boolean statusDescriptionOk = false;
	private boolean statusHintOk = false;
	
	List<Stundenplanstatus> scheduleStatusList;
	
	private Stundenplanstatus statusSelected;

	public Stundenplanstatus getStundenplanstatus() {
		return stundenplanstatus;
	}

	public void setStundenplanstatus(Stundenplanstatus stundenplanstatus) {
		this.stundenplanstatus = stundenplanstatus;
	}

	public String getStatusColor() {
		return statusColor;
	}

	public void setStatusColor(String statusColor) {
		if(statusColor!=null){
			this.statusColor = statusColor;
			statusColorOk = true;
		}
		/*else{
			FacesMessage message = new FacesMessage("Farbe bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("StundenplanstatusForm:PColor_reg", message);
	    }*/
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		if(statusDescription!=null){
			this.statusDescription = statusDescription;
			statusDescriptionOk = true;
		}
		/*else{
			FacesMessage message = new FacesMessage("Bezeichnung bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("StundenplanstatusForm:SPSTBezeichnung_reg", message);
	    }*/
	}

	public String getStatusHint() {
		return statusHint;
	}

	public void setStatusHint(String statusHint) {
		if(statusHint!=null){
			this.statusHint = statusHint;
			statusHintOk = true;
		}
		/*else{
			FacesMessage message = new FacesMessage("Hinweis bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("StundenplanstatusForm:SPSTHint_reg", message);
	    }*/
	}

	public Stundenplanstatus getStatusSelected() {
		return statusSelected;
	}

	public void setStatusSelected(Stundenplanstatus statusSelected) {
		this.statusSelected = statusSelected;
	}

	public List<Stundenplanstatus> getScheduleStatusList() {
		return scheduleStatusList;
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createStundenplanstatus() throws Exception  {
		EntityManager em = emf.createEntityManager();
		Stundenplanstatus sps = new Stundenplanstatus();  
		sps.setSPSTBezeichnung(statusDescription);
		sps.setSPSTHint(statusHint);
		sps.setPColor(statusColor);
		try {
			stundenplanstatusFacadeLocal.create(sps);
	    }
	    catch (Exception e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
		em.close();
	}
	
	public void createDoStundenplanstatus() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(statusDescriptionOk == true && statusHintOk == true && statusColorOk == true) {
			createStundenplanstatus();
			scheduleStatusList = getStundenplanstatusList();
		}
		
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Stundenplanstatus> getStundenplanstatusList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Stundenplanstatus> query = em.createNamedQuery("Stundenplanstatus.findAll", Stundenplanstatus.class);
		scheduleStatusList = query.getResultList();
		return query.getResultList();
	}
	
	
	
	 public void onRowSelect(SelectEvent<Stundenplanstatus> e) {
	    	FacesMessage msg = new FacesMessage("Stundenplanstatus ausgewählt");
	        FacesContext.getCurrentInstance().addMessage(null, msg);
	        
	        statusSelected = e.getObject();
	        
	    }
	    
	    public void addStundenplanstatus(){
	    	 try {
	 	        EntityManager em = emf.createEntityManager();
	 	        em.find(Stundenplanstatus.class, statusSelected.getSpstid());
	 	        stundenplanstatus.setSpstid(statusSelected.getSpstid());
	 	        stundenplanstatus.setPColor(statusSelected.getPColor());
	 	        stundenplanstatus.setSPSTBezeichnung(statusSelected.getSPSTBezeichnung());
	 	        stundenplanstatus.setSPSTHint(statusSelected.getSPSTHint());
	 	        stundenplanstatusFacadeLocal.edit(stundenplanstatus);
	 	    }
	 	    catch (Exception e) {
	 	        try {
	 	            ut.rollback();
	 	        } 
	 	        catch (IllegalStateException | SecurityException | SystemException ex) {
	 	        }
	 	    }
	    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteStundenplanstatus() throws Exception {
        scheduleStatusList.remove(statusSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Stundenplanstatus> q = em.createNamedQuery("Stundenplanstatus.findBySpsid",Stundenplanstatus.class);
        q.setParameter("spstid", statusSelected.getSpstid());
        stundenplanstatus = (Stundenplanstatus)q.getSingleResult();
        
        try {
        	stundenplanstatusFacadeLocal.remove(stundenplanstatus);
	    }
	    catch (Exception e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
        
		em.close();
    }

}
