package controller;


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
import javax.faces.bean.ManagedBean;
import controller.MessageForPrimefaces;

/**
*
* @author Manuel
*/

@ManagedBean(name="LehrveranstaltungsartController")
@SessionScoped
public class LehrveranstaltungsartController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Lehrveranstaltungsart lehrveranstaltungsart;
	
	@PostConstruct
    public void init() {
        lvalist = getLehrveranstaltungsartList();
    }
 
	private String lvdauer;
	private String lvkurz;
	private String lvname;
	private boolean lvdauer_ok = false;
	private boolean lvkurz_ok = false;
	private boolean lvname_ok = false;
	
	List<Lehrveranstaltungsart> lvalist;
	
	private Lehrveranstaltungsart selectedlva;

	public Lehrveranstaltungsart getLehrveranstaltungsart() {
		return lehrveranstaltungsart;
	}

	public void setLehrveranstaltungsart(Lehrveranstaltungsart lehrveranstaltungsart) {
		this.lehrveranstaltungsart = lehrveranstaltungsart;
	}

	public String getLvdauer() {
		return lvdauer;
	}

	public void setLvdauer(String lvdauer) {
		if(lvdauer!=null){
			this.lvdauer = lvdauer;
	        lvdauer_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Lehrveranstaltungsdauer konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("LehrveranstaltungsartForm:lvdauer_reg", message);
	    }
	}

	public String getLvkurz() {
		return lvkurz;
	}

	public void setLvkurz(String lvkurz) {
		if(lvkurz!=null){
			this.lvkurz = lvkurz;
	        lvkurz_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Lehrveranstaltungskurzform konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("LehrveranstaltungsartForm:lvkurz_reg", message);
	    }
	}

	public String getLvname() {
		return lvname;
	}

	public void setLvname(String lvname) {
		if(lvname!=null){
			this.lvname = lvname;
	        lvname_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Lehrveranstaltungsname konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("LehrveranstaltungsartForm:lvname_reg", message);
	    }
	}

	public List<Lehrveranstaltungsart> getLvalist() {
		return lvalist;
	}

	public void setLvalist(List<Lehrveranstaltungsart> lvalist) {
		this.lvalist = lvalist;
	}

	public Lehrveranstaltungsart getSelectedlva() {
		return selectedlva;
	}

	public void setSelectedlva(Lehrveranstaltungsart selectedlva) {
		this.selectedlva = selectedlva;
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
		lva.setLvname(lvname);
		lva.setLvdauer(lvdauer);     
		lva.setLvkurz(lvkurz);
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
	
	public String createDoLehrveranstaltungsart() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(lvname_ok == true && lvdauer_ok == true && lvkurz_ok == true) {
			createLehrveranstaltungsart();
			return "showlehrveranstaltungsart.xhtml";
		}
		else{
			return "createlehrveranstaltungsart.xhtml";
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Lehrveranstaltungsart> getLehrveranstaltungsartList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Lehrveranstaltungsart> query = em.createNamedQuery("Lehrveranstaltungsart.findAll", Lehrveranstaltungsart.class);
		lvalist = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowEdit(RowEditEvent<Lehrveranstaltungsart> event) {
        FacesMessage msg = new FacesMessage("Lehrveranstaltungsart Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        Lehrveranstaltungsart newlva = new Lehrveranstaltungsart();
        newlva = event.getObject();
        
        try {
	        ut.begin();
	        EntityManager em = emf.createEntityManager();
	        em.find(Lehrveranstaltungsart.class, newlva.getLvid());	        
	        lehrveranstaltungsart.setLvid(newlva.getLvid());
	        lehrveranstaltungsart.setLvname(newlva.getLvname());
	        lehrveranstaltungsart.setLvdauer(newlva.getLvdauer());
	        lehrveranstaltungsart.setLvkurz(newlva.getLvkurz());
	        em.merge(lehrveranstaltungsart);
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
     
    public void onRowCancel(RowEditEvent<Lehrveranstaltungsart> event) {
    	FacesMessage msg = new FacesMessage("Lehrveranstaltungsart Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteLehrveranstaltungsart() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        lvalist.remove(selectedlva);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Lehrveranstaltungsart> q = em.createNamedQuery("Lehrveranstaltungsart.findByLvid",Lehrveranstaltungsart.class);
        q.setParameter("lvid", selectedlva.getLvid());
        lehrveranstaltungsart = (Lehrveranstaltungsart)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(lehrveranstaltungsart);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
        selectedlva = null;
		em.close();
    }
	
	
}
