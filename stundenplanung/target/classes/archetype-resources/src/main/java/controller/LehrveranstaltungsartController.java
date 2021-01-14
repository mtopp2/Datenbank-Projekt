package controller;


import model.Faculty;
import model.Lehrveranstaltungsart;
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

/**
*
* @author Anil
*/

//@ManagedBean(name="LehrveranstaltungsartController")
@Named(value="lehrveranstaltungsartController")
@SessionScoped
public class LehrveranstaltungsartController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Lehrveranstaltungsart teachingEvent;
	
	@PostConstruct
    public void init() {
        teachingEventList = getLehrveranstaltungsartList();
    }
 
	private String teachingEventLength;
	private String teachingEventShort;
	private String teachingEventName;
	private boolean teachingEventLengthOk = false;
	private boolean teachingEventShortOk = false;
	private boolean teachingEventNameOk = false;
	
	List<Lehrveranstaltungsart> teachingEventList;
	
	private Lehrveranstaltungsart teachingEventSelected;

	public Lehrveranstaltungsart getTeachingEvent() {
		return teachingEvent;
	}

	public void setTeachingEvent(Lehrveranstaltungsart teachingEvent) {
		this.teachingEvent = teachingEvent;
	}

	public String getTeachingEventLength() {
		return teachingEventLength;
	}

	public void setTeachingEventLength(String teachingEventLength) {
		if(teachingEventLength!=null){
			this.teachingEventLength = teachingEventLength;
			teachingEventLengthOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Lehrveranstaltungsdauer konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("LehrveranstaltungsartForm:lvdauer_reg", message);
	    }
	}

	public String getTeachingEventShort() {
		return teachingEventShort;
	}

	public void setTeachingEventShort(String teachingEventShort) {
		if(teachingEventShort!=null){
			this.teachingEventShort = teachingEventShort;
			teachingEventShortOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Lehrveranstaltungskurzform konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("LehrveranstaltungsartForm:lvkurz_reg", message);
	    }
	}

	public String getTeachingEventName() {
		return teachingEventName;
	}

	public void setTeachingEventName(String teachingEventName) {
		if(teachingEventName!=null){
			this.teachingEventName = teachingEventName;
			teachingEventNameOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Lehrveranstaltungsname konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("LehrveranstaltungsartForm:lvname_reg", message);
	    }
	}

	public List<Lehrveranstaltungsart> getTeachingEventList() {
		return teachingEventList;
	}

	public void setTeachingEventList(List<Lehrveranstaltungsart> teachingEventList) {
		this.teachingEventList = teachingEventList;
	}

	public Lehrveranstaltungsart getTeachingEventSelected() {
		return teachingEventSelected;
	}

	public void setTeachingEventSelected(Lehrveranstaltungsart teachingEventSelected) {
		this.teachingEventSelected = teachingEventSelected;
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createLehrveranstaltungsart() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Lehrveranstaltungsart lva = new Lehrveranstaltungsart();  
		lva.setLvname(teachingEventName);
		lva.setLvdauer(teachingEventLength);     
		lva.setLvkurz(teachingEventShort);
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(lva);  
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
	
	public void createDoLehrveranstaltungsart() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		//if(teachingEventNameOk == true && teachingEventLengthOk == true && teachingEventShortOk == true) {
			createLehrveranstaltungsart();
			teachingEventList = getLehrveranstaltungsartList();
		//}
		
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Lehrveranstaltungsart> getLehrveranstaltungsartList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Lehrveranstaltungsart> query = em.createNamedQuery("Lehrveranstaltungsart.findAll", Lehrveranstaltungsart.class);
		teachingEventList = query.getResultList();
		return query.getResultList();
	}
	
	
	public void onRowSelect(SelectEvent<Lehrveranstaltungsart> e) {
    	FacesMessage msg = new FacesMessage("Lehrveranstaltungsart ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        teachingEventSelected = e.getObject();
        
    }
    
    public void addLehrveranstaltungsart(){
    	 try {
 	        ut.begin();
 	        EntityManager em = emf.createEntityManager();
 	        em.find(Lehrveranstaltungsart.class, teachingEventSelected.getLvid());
 	        teachingEvent.setLvid(teachingEventSelected.getLvid());
 	        teachingEvent.setLvname(teachingEventSelected.getLvname());
 	        teachingEvent.setLvdauer(teachingEventSelected.getLvdauer());
 	        teachingEvent.setLvkurz(teachingEventSelected.getLvkurz());
 	        em.merge(teachingEvent);
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
	
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteLehrveranstaltungsart() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        teachingEventList.remove(teachingEventSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Lehrveranstaltungsart> q = em.createNamedQuery("Lehrveranstaltungsart.findByLvid",Lehrveranstaltungsart.class);
        q.setParameter("lvid", teachingEventSelected.getLvid());
        teachingEvent = (Lehrveranstaltungsart)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(teachingEvent);
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
	
	
}
