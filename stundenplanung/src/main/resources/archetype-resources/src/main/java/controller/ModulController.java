package controller;

import model.Account;
import model.Faculty;
import model.Modul;
import model.Pruefcode;
import model.Studiengang;

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
	private Pruefcode code;
	
	@EJB
	private ModulFacadeLocal modulFacadeLocal;
	
	@PostConstruct
    public void init() {
        modulList = getModulListAll();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Pruefcode.findAll");
		List FList = q.getResultList();
        for (Object FListitem : FList)
        {
        	Pruefcode pCode =(Pruefcode)FListitem;
        	codeList.add(pCode);
        }
    }
	
	ArrayList<Pruefcode> codeList = new ArrayList<>();
	
	
	private String modulShort;
	private String modulName;
	private int pcId;
	private boolean modulShortOk = false;
	private boolean modulNameOk = false;
	
	
	List<Modul> modulList;
	
	
	private Modul modulSelected;
	
	public Modul getModulSelected() {
		return modulSelected;
	}
	  
	public void setModulSelected(Modul modulSelected) {
		this.modulSelected = modulSelected;
	}
	
	public ArrayList<Pruefcode> getCodeList() {
		return codeList;
	}

	public void setCodeList(ArrayList<Pruefcode> codeList) {
		this.codeList = codeList;
	}
	
	public Pruefcode getCode() {
		return code;
	}

	public void setCode(Pruefcode code) {
		this.code = code;
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
	  
	public int getPcId() {
		return pcId;
	}
	  
	public void setPcId(int pcId) {
	        this.pcId = pcId;
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
		mod.setPruefcode(findCode(pcId));
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
		if(modulNameOk == true && modulShortOk == true) {
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
        pcId = modulSelected.getPruefcode().getPcid();
        
    }
    
    public void addModul(){
    	 try {
 	        EntityManager em = emf.createEntityManager();
 	        em.find(Modul.class, modulSelected.getModID());
 	        modul.setModID(modulSelected.getModID());
 	        modul.setModName(modulSelected.getModName());
 	        modul.setModKuerzel(modulSelected.getModKuerzel());
 	        modul.setPruefcode(findCode(pcId));
 	        modulFacadeLocal.edit(modul);
 	    }
 	    catch (Exception e) {
 	        try {
 	            ut.rollback();
 	        } 
 	        catch (IllegalStateException | SecurityException | SystemException ex) {
 	        }
 	    }
    	 modulList = getModulListAll();
    }
    
    private Pruefcode findCode(int pcid) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Pruefcode> query
                = em.createNamedQuery("Pruefcode.findByPcid",Pruefcode.class);
            query.setParameter("pcid", pcid);
            code = (Pruefcode)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return code;
    }
   // ---------------------------------------------------------------------------------------------------------------------
    
	
	  
	//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}