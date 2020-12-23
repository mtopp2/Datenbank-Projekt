package controller;

import model.Account;
import model.Faculty;
import model.Modul;
import model.Benutzergruppe;
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

import com.sun.javafx.logging.Logger;

import org.primefaces.event.CellEditEvent;
//import org.primefaces.event.


import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import controller.MessageForPrimefaces;

/**
*
* @author Manuel
*/

@ManagedBean(name="BenutzergruppeController")
//@Named(value = "ModulController")
//@SessionScoped
@SessionScoped
public class BenutzergruppeController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	//@SuppressWarnings("cdi-ambiguous-dependency")
	@Inject 
	private Benutzergruppe benutzergruppe;
	
	@PostConstruct
    public void init() {
        bglist = getBenutzergruppeList();
    }
 
	
	
	private String BGName;
	private String BGShortName;
	private Integer BGRechte;
	private boolean BGName_ok = false;
	private boolean BGShortName_ok = false;
	private boolean BGRechte_ok = false;
	
	List<Benutzergruppe> bglist;
	
	//modlist.add(getModulList());
	
	private Benutzergruppe selectedbenutzergruppe;
	
	public Benutzergruppe getSelectedbenutzergruppe() {
		return selectedbenutzergruppe;
	}
	  
	public void setSelectedbenutzergruppe(Benutzergruppe selectedbenutzergruppe) {
		this.selectedbenutzergruppe = selectedbenutzergruppe;
	}
	
	
	  
    public List<Benutzergruppe> getBglist() {
        return bglist;
    }
    
	public Benutzergruppe getBenutzergruppe() {
		return benutzergruppe;
	}
	  
	public void setBenutzergruppe(Benutzergruppe benutzergruppe) {
		this.benutzergruppe = benutzergruppe;
	}
	  
	public String getBGName() {
		return BGName;
	}
	  
	public void setBGName(String BGName) {
		if(BGName!=null){
			this.BGName = BGName;
			BGName_ok = true;
		}
		else{
			FacesMessage message = new FacesMessage("Benutzergruppekürzel bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("BenutzergruppeForm:BGName_reg", message);
	        //String msg = "Modulkürzel bereits vorhanden.";
	        //addMessage("modKuerzel_reg",msg);
	    }
	}
	  
	public String getBGShortName() {
		return BGShortName;
	}
	  
	public void setBGShortName(String BGShortName) {
	    if(BGShortName!=null){
	        this.BGShortName = BGShortName;
	        BGShortName_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Benutzergruppe bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("BenutzergruppeForm:BGShortName_reg", message);
	        //String msg = "Modulname bereits vorhanden.";
	        //addMessage("modName_reg",msg);
	    }
	}
	  
	public Integer getBGRechte() {
		return BGRechte;
	}
	  
	public void setBGRechte(Integer BGRechte) {
		if(BGRechte!=null){
	        this.BGRechte = BGRechte;
	        BGRechte_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("BGRechte bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("BenutzergruppeForm:BGRechte_reg", message);
	        //String msg = "Prüfcodeid bereits vorhanden.";
	        //addMessage("pcid_reg",msg);
	    }
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createBenutzergruppe() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Benutzergruppe bg = new Benutzergruppe();  
		bg.setBGName(BGName);    
		bg.setBGShortName(BGShortName);      
		bg.setBGRechte(BGRechte);
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(bg);  
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
	
	public String createDoBenutzergruppe() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(BGName_ok == true && BGShortName_ok == true && BGRechte_ok == true) {
			createBenutzergruppe();
			return "index.xhtml";
		}
		else{
			return "index.xhtml";
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Benutzergruppe> getBenutzergruppeList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Benutzergruppe> query = em.createNamedQuery("Benutzergruppe.findAll", Benutzergruppe.class);
		bglist = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowEdit(RowEditEvent<Benutzergruppe> event) {
        //MessageForPrimefaces msg = new MessageForPrimefaces("Modul Edited", event.getObject().getModID());
        //FacesMessage msg = new FacesMessage("Modul Edited", event.getObject().getModID());
        FacesMessage msg = new FacesMessage("Benutzergruppe Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        Benutzergruppe newbg = new Benutzergruppe();
        newbg = event.getObject();
        
        try {
	        ut.begin();
	        EntityManager em = emf.createEntityManager();
	        //em.joinTransaction();
	        em.find(Benutzergruppe.class, 279);
	        //em.persist(q)
	        
	        benutzergruppe.setGroupID(newbg.getGroupID());
	        benutzergruppe.setBGName(newbg.getBGName());
	        benutzergruppe.setBGShortName(newbg.getBGShortName());
	        benutzergruppe.setBGRechte(newbg.getBGRechte());
	        
	        
	        em.merge(benutzergruppe);
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
     
    public void onRowCancel(RowEditEvent<Benutzergruppe> event) {
        //MessageForPrimefaces msg = new MessageForPrimefaces("Modul Cancelled", event.getObject().getModID());
        //FacesMessage msg = new FacesMessage("Modul Cancelled", event.getObject().getModID());
    	FacesMessage msg = new FacesMessage("Benutzergruppe Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteBenutzergruppe() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        bglist.remove(selectedbenutzergruppe);
        //selectedmodul = null;
        //updateModul(modlist);
        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Benutzergruppe> q = em.createNamedQuery("Benutzergruppe.findByID",Benutzergruppe.class);
        q.setParameter("groupID", selectedbenutzergruppe.getGroupID());
        benutzergruppe = (Benutzergruppe)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        //em.persist(q);
	        em.remove(benutzergruppe);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
        selectedbenutzergruppe = null;
		em.close();
    }
    
   // ---------------------------------------------------------------------------------------------------------------------
    
	
	  
		//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}