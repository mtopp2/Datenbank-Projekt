package controller;

import model.Benutzergruppe;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

import java.util.List;



import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import javax.persistence.PersistenceUnit;

import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;


import org.primefaces.event.SelectEvent;
import javax.ejb.EJB;
import EJB.BenutzergruppeFacadeLocal;


/**
*
* @author Anil
*/

@Named(value="benutzergruppeController")
@SessionScoped
public class BenutzergruppeController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Benutzergruppe userGroup;
	
	@EJB
	private BenutzergruppeFacadeLocal userGroupFacadeLocal;
	
	/**
	 * Initialisierung
	 */
	@PostConstruct
    public void init() {
        userGroupList = getBenutzergruppeList();
    }
 
	
	
	private String userGroupName;
	private String userGroupShortName;
	private Integer userGroupRights;
	private boolean userGroupNameOk = false;
	private boolean userGroupShortNameOk = false;
	private boolean userGroupRightsOk = false;
	
	List<Benutzergruppe> userGroupList;
	
	private Benutzergruppe userGroupSelected;
	
	// Getter und Setter
	public Benutzergruppe getUserGroupSelected() {
		return userGroupSelected;
	}
	  
	public void setUserGroupSelected(Benutzergruppe userGroupSelected) {
		this.userGroupSelected = userGroupSelected;
	}
	  
    public List<Benutzergruppe> getUserGroupList() {
        return userGroupList;
    }
    
	public Benutzergruppe getUserGroup() {
		return userGroup;
	}
	  
	public void setUserGroup(Benutzergruppe userGroup) {
		this.userGroup = userGroup;
	}
	  
	public String getUserGroupName() {
		return userGroupName;
	}
	  
	public void setUserGroupName(String userGroupName) {
		if(userGroupName!=null){
			this.userGroupName = userGroupName;
			userGroupNameOk = true;
		}
		else{
			FacesMessage message = new FacesMessage("Benutzergruppe ist bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("BenutzergruppeForm:BGName_reg", message);
	    }
	}
	  
	public String getUserGroupShortName() {
		return userGroupShortName;
	}
	  
	public void setUserGroupShortName(String userGroupShortName) {
	    if(userGroupShortName!=null){
	        this.userGroupShortName = userGroupShortName;
	        userGroupShortNameOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Benutzergruppenkürzel ist bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("BenutzergruppeForm:BGShortName_reg", message);
	    }
	}
	  
	public Integer getUserGroupRights() {
		return userGroupRights;
	}
	  
	public void setUserGroupRights(Integer userGroupRights) {
		if(userGroupRights!=null){
	        this.userGroupRights = userGroupRights;
	        userGroupRightsOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Benutzerrechte ist bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("BenutzergruppeForm:BGRechte_reg", message);
	    }
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;
	
	/**
	 * Erstellen eines Benutzergruppeneintrags
	 * @throws Exception
	 */
	public void createBenutzergruppe() throws Exception  {
		String msg;
		EntityManager em = emf.createEntityManager();
		Benutzergruppe bg = new Benutzergruppe();  
		bg.setBGName(userGroupName);    
		bg.setBGShortName(userGroupShortName);      
		bg.setBGRechte(userGroupRights);
		try {
			userGroupFacadeLocal.create(bg);
			msg = "Eintrag wurde erstellt.";
            addMessage("messages", msg);
	    }
	    catch (Exception e) {
	    	msg = "Eintrag wurde nicht erstellt.";
            addMessage("messages", msg);
	        
	    }
		em.close();
	}
	
	/**
	 * Schaut ob Benutzergruppenname, Benutzergruppenkurzname und Benutzergruppenrechte gesetzt worden ist, danach wird der Benutzergruppeneintrag erstellt und zum Schluß wird die Liste aktualisiert.
	 * @throws SecurityException
	 * @throws SystemException
	 * @throws NotSupportedException
	 * @throws RollbackException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws Exception
	 */
	public void createDoBenutzergruppe() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(userGroupNameOk == true && userGroupShortNameOk == true && userGroupRightsOk == true) {
			createBenutzergruppe();
			userGroupList = getBenutzergruppeList();
		}
		
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Laden der Benutzergruppenliste
	 * @return
	 */
	public List<Benutzergruppe> getBenutzergruppeList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Benutzergruppe> query = em.createNamedQuery("Benutzergruppe.findAll", Benutzergruppe.class);
		return query.getResultList();
	}
	
	
	
	
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Löschen eines Benutzergruppeneintrags
     * @throws Exception
     */
    public void deleteBenutzergruppe() throws Exception {
    	String msg;
        userGroupList.remove(userGroupSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Benutzergruppe> q = em.createNamedQuery("Benutzergruppe.findByID",Benutzergruppe.class);
        q.setParameter("groupID", userGroupSelected.getGroupID());
        userGroup = (Benutzergruppe)q.getSingleResult();
        
        try {
        	userGroupFacadeLocal.remove(userGroup);
        	msg = "Eintrag wurde gelöscht.";
            addMessage("messages", msg);
	    }
	    catch (Exception e) {
	    	msg = "Eintrag wurde nicht gelöscht.";
            addMessage("messages", msg);
	        
	    }
		em.close();
    }
    
    /**
     * Ausgewählte Zeile wird in userGroupSelected gespeichert
     * @param e
     */
    public void onRowSelect(SelectEvent<Benutzergruppe> e) {
    	FacesMessage msg = new FacesMessage("Benutzergruppe ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        userGroupSelected = e.getObject();
        
    }
    
    /**
     * Bearbeiten eines Benutzergruppeneintrags
     */
    public void addBenutzergruppe(){
    	EntityManager em = emf.createEntityManager();
    	String msg;
    	try {
 	       em.find(Benutzergruppe.class, userGroupSelected.getGroupID());
 	       userGroup.setGroupID(userGroupSelected.getGroupID());
 	       userGroup.setBGName(userGroupSelected.getBGName());
 	       userGroup.setBGShortName(userGroupSelected.getBGShortName());
 	       userGroup.setBGRechte(userGroupSelected.getBGRechte());
 	       userGroupFacadeLocal.edit(userGroup);
 	       msg = "Eintrag wurde bearbeitet.";
           addMessage("messages", msg);
 	    }
 	    catch (Exception e) {
 	    	msg = "Eintrag wurde nicht bearbeitet.";
            addMessage("messages", msg);
 	    }
    	userGroupList = getBenutzergruppeList();
    	em.close();
    }
   // ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Nachrichten an die View senden
	 * @param loginformidName
	 * @param msg
	 */
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}

	
}