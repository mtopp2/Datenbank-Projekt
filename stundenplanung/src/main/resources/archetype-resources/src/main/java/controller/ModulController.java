package controller;

import model.Account;
import model.Faculty;
import model.Modul;
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

@ManagedBean(name="ModulController")
//@Named(value = "ModulController")
//@SessionScoped
@SessionScoped
public class ModulController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	//@SuppressWarnings("cdi-ambiguous-dependency")
	@Inject 
	private Modul modul;
	
	@PostConstruct
    public void init() {
        modlist = getModulList();
    }
 
	
	
	private String modKuerzel;
	private String modName;
	private Integer pcid;
	private boolean modKuerzel_ok = false;
	private boolean modName_ok = false;
	private boolean pcid_ok = false;
	
	List<Modul> modlist;
	
	//modlist.add(getModulList());
	
	private Modul selectedmodul;
	
	public Modul getSelectedmodul() {
		return selectedmodul;
	}
	  
	public void setSelectedmodul(Modul selectedmodul) {
		this.selectedmodul = selectedmodul;
	}
	
	
	  
    public List<Modul> getModlist() {
        return modlist;
    }
    
	public Modul getModul() {
		return modul;
	}
	  
	public void setModul(Modul modul) {
		this.modul = modul;
	}
	  
	public String getModKuerzel() {
		return modKuerzel;
	}
	  
	public void setModKuerzel(String modKuerzel) {
		if(modKuerzel!=null){
			this.modKuerzel = modKuerzel;
			modKuerzel_ok = true;
		}
		else{
			FacesMessage message = new FacesMessage("Modulkürzel bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("ModulForm:modKuerzel_reg", message);
	        //String msg = "Modulkürzel bereits vorhanden.";
	        //addMessage("modKuerzel_reg",msg);
	    }
	}
	  
	public String getModName() {
		return modName;
	}
	  
	public void setModName(String modName) {
	    if(modName!=null){
	        this.modName = modName;
	        modName_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Modulname bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("ModulForm:modName_reg", message);
	        //String msg = "Modulname bereits vorhanden.";
	        //addMessage("modName_reg",msg);
	    }
	}
	  
	public Integer getPcid() {
		return pcid;
	}
	  
	public void setPcid(Integer pcid) {
		if(pcid!=null){
	        this.pcid = pcid;
	        pcid_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Prüfcodeid bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("ModulForm:pcid_reg", message);
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
	public void createModul() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Modul mod = new Modul();  
		mod.setModName(modName);    
		mod.setModKuerzel(modKuerzel);      
		mod.setPcid(pcid);
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(mod);  
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
	
	public String createDoModul() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(modName_ok == true && modKuerzel_ok == true && pcid_ok == true) {
			createModul();
			return "index.xhtml";
		}
		else{
			return "index.xhtml";
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Modul> getModulList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Modul> query = em.createNamedQuery("Modul.findAll", Modul.class);
		modlist = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowEdit(RowEditEvent<Modul> event) {
        //MessageForPrimefaces msg = new MessageForPrimefaces("Modul Edited", event.getObject().getModID());
        //FacesMessage msg = new FacesMessage("Modul Edited", event.getObject().getModID());
        FacesMessage msg = new FacesMessage("Modul Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        Modul newmod = new Modul();
        newmod = event.getObject();
        
        try {
	        ut.begin();
	        EntityManager em = emf.createEntityManager();
	        //em.joinTransaction();
	        em.find(Modul.class, 279);
	        //em.persist(q)
	        
	        modul.setModID(newmod.getModID());
	        modul.setModName(newmod.getModName());
	        modul.setModKuerzel(newmod.getModKuerzel());
	        modul.setPcid(newmod.getPcid());
	        
	        
	        em.merge(modul);
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
     
    public void onRowCancel(RowEditEvent<Modul> event) {
        //MessageForPrimefaces msg = new MessageForPrimefaces("Modul Cancelled", event.getObject().getModID());
        //FacesMessage msg = new FacesMessage("Modul Cancelled", event.getObject().getModID());
    	FacesMessage msg = new FacesMessage("Modul Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteModul() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        modlist.remove(selectedmodul);
        //selectedmodul = null;
        //updateModul(modlist);
        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Modul> q = em.createNamedQuery("Modul.findByModID",Modul.class);
        q.setParameter("modID", selectedmodul.getModID());
        modul = (Modul)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        //em.persist(q);
	        em.remove(modul);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
        selectedmodul = null;
		em.close();
    }
    
   // ---------------------------------------------------------------------------------------------------------------------
    
	
	  
		//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}