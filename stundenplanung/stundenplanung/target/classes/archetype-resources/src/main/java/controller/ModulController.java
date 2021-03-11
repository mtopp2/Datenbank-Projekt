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
import org.primefaces.event.SelectEvent;

import com.sun.javafx.logging.Logger;

import org.primefaces.event.CellEditEvent;
//import org.primefaces.event.


import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import controller.MessageForPrimefaces;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import EJB.ModulFacadeLocal;

/**
*
* @author Anil
*/

@Named(value="modulController")
@SessionScoped
public class ModulController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Modul modul;
	
	@EJB
	private ModulFacadeLocal modulFacadeLocal;
	
	@PostConstruct
    public void init() {
        modulList = getModulListAll();
    }
 
	
	
	private String modulShort;
	private String modulName;
	private Integer pcId;
	private boolean modulShortOk = false;
	private boolean modulNameOk = false;
	private boolean pcIdOk = false;
	
	List<Modul> modulList;
	
	
	private Modul modulSelected;
	
	public Modul getModulSelected() {
		return modulSelected;
	}
	  
	public void setModulSelected(Modul modulSelected) {
		this.modulSelected = modulSelected;
	}
	
	
	  
    public List<Modul> getModulList() {
        return modulList;
    }
    
	public Modul getModul() {
		return modul;
	}
	  
	public void setModul(Modul modul) {
		this.modul = modul;
	}
	  
	public String getModulShort() {
		return modulShort;
	}
	  
	public void setModulShort(String modulShort) {
		if(modulShort!=null){
			this.modulShort = modulShort;
			modulShortOk = true;
		}
		else{
			FacesMessage message = new FacesMessage("Modulkürzel bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("ModulForm:modKuerzel_reg", message);
	    }
	}
	  
	public String getModulName() {
		return modulName;
	}
	  
	public void setModulName(String modulName) {
	    if(modulName!=null){
	        this.modulName = modulName;
	        modulNameOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Modulname bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("ModulForm:modName_reg", message);
	    }
	}
	  
	public Integer getPcId() {
		return pcId;
	}
	  
	public void setPcId(Integer pcId) {
		if(pcId!=null){
	        this.pcId = pcId;
	        pcIdOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Prüfcodeid bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("ModulForm:pcid_reg", message);
	    }
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createModul() throws Exception  {
		EntityManager em = emf.createEntityManager();
		Modul mod = new Modul();  
		mod.setModName(modulName);    
		mod.setModKuerzel(modulShort);      
		mod.setPcid(pcId);
		try {
			modulFacadeLocal.create(mod);
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
	
	public void createDoModul() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(modulNameOk == true && modulShortOk == true && pcIdOk == true) {
			createModul();
			modulList = getModulListAll();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Modul> getModulListAll(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Modul> query = em.createNamedQuery("Modul.findAll", Modul.class);
		modulList = query.getResultList();
		return query.getResultList();
	}
	
	
	
	
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteModul() throws Exception {
        modulList.remove(modulSelected);
        EntityManager em = emf.createEntityManager();
        TypedQuery<Modul> q = em.createNamedQuery("Modul.findByModID",Modul.class);
        q.setParameter("modID", modulSelected.getModID());
        modul = (Modul)q.getSingleResult();
        
        try {
        	modulFacadeLocal.remove(modul);
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
    
    public void onRowSelect(SelectEvent<Modul> e) {
    	FacesMessage msg = new FacesMessage("Modul ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        modulSelected = e.getObject();
        
    }
    
    public void addModul(){
    	 try {
 	        EntityManager em = emf.createEntityManager();
 	        em.find(Modul.class, modulSelected.getModID());
 	        modul.setModID(modulSelected.getModID());
 	        modul.setModName(modulSelected.getModName());
 	        modul.setModKuerzel(modulSelected.getModKuerzel());
 	        modul.setPcid(modulSelected.getPcid());
 	        modulFacadeLocal.edit(modul);
 	    }
 	    catch (Exception e) {
 	        try {
 	            ut.rollback();
 	        } 
 	        catch (IllegalStateException | SecurityException | SystemException ex) {
 	        }
 	    }
    }
    
    
   // ---------------------------------------------------------------------------------------------------------------------
    
	
	  
	//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}