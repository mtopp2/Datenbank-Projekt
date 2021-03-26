package controller;

import model.Location;
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
import EJB.LocationFacadeLocal;

/**
*
* @author Anil
*/

@Named(value="locationController")
@SessionScoped
public class LocationController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Location location;
	
	@EJB
	private LocationFacadeLocal locationFacadeLocal;
	
	/**
	 * Initialisierung
	 */
	@PostConstruct
    public void init() {
        locationList = getLocationListAll();
    }
	
	private String locationCity;
	private String locationStreet;
	private boolean locationCityOk = false;
	private boolean locationStreetOk = false;
	
	private Location locationSelected;
	
	List<Location> locationList;
	
	// Getter und Setter
    public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public List<Location> getLocationList() {
		return locationList;
	}

	public Location getLocationSelected() {
		return locationSelected;
	}

	public void setLocationSelected(Location locationSelected) {
		this.locationSelected = locationSelected;
	}

	public String getLocationCity() {
		return this.locationCity;
	}

	public void setLocationCity(String locationCity) {
		if(locationCity != null){
			this.locationCity = locationCity;
			locationCityOk = true;
		}
		else{
			FacesMessage message = new FacesMessage("Stadt ist bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("LocationForm:modKuerzel_reg", message);
	    }
	}

	public String getLocationStreet() {
		return this.locationStreet;
	}

	public void setLocationStreet(String locationStreet) {
		if(locationStreet != null){
			this.locationStreet = locationStreet;
			locationStreetOk = true;
		}
		else{
			FacesMessage message = new FacesMessage("Straße ist bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("LocationForm:modKuerzel_reg", message);
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
	 * Fügt einen neuen Standort hinzu.
	 * @throws Exception
	 */
	public void createLocation() throws Exception  {
		String msg;
		EntityManager em = emf.createEntityManager();
		Location loc = new Location();  
		loc.setLCity(locationCity);    
		loc.setLStreet(locationStreet);      
		try {
			locationFacadeLocal.create(loc);
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
	 * Schaut, ob die Variabeln Stadt und Straße gesetzt worden sind, fügt dann den Eintrag in die Datenbank hinzu und zum Schluß wird die die Liste aktualisiert.
	 * @throws SecurityException
	 * @throws SystemException
	 * @throws NotSupportedException
	 * @throws RollbackException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws Exception
	 */
	public void createDoLocation() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(locationCityOk == true && locationStreetOk == true) {
			createLocation();
			locationList = getLocationListAll();
			
		}
	}
	
	/**
	 * Standortliste wird geladen
	 * @return
	 */
	public List<Location> getLocationListAll(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Location> query = em.createNamedQuery("Location.findAll", Location.class);
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Löscht einen Standorteintrag
     * @throws Exception
     */
    public void deleteLocation() throws Exception {
    	String msg;
    	locationList.remove(locationSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Location> q = em.createNamedQuery("Location.findByLid",Location.class);
        q.setParameter("lid", locationSelected.getLid());
        location = (Location)q.getSingleResult();
        
        try {
        	locationFacadeLocal.remove(location);
        	msg = "Eintrag wurde gelöscht.";
            addMessage("messages", msg);
	    }
	    catch (Exception e) {
            msg = "Eintrag wurde nicht gelöscht.";
            addMessage("messages", msg);
	    }
		em.close();
    }
    
    //----------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Die ausgewählte Zeile wird in locationSelected gespeichert.
     * @param e
     */
    public void onRowSelect(SelectEvent<Location> e) {
    	FacesMessage msg = new FacesMessage("Standort ausgewählt.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        locationSelected = e.getObject();
        
    }
    
    // 
    /**
     * Bearbeitet ein Standorteintrag
     */
    public void addLocation(){
    	String msg;
    	EntityManager em = emf.createEntityManager();
    	 try {
 	        em.find(Location.class, locationSelected.getLid());
 	        location.setLid(locationSelected.getLid());
 	        location.setLCity(locationSelected.getLCity());
 	        location.setLStreet(locationSelected.getLStreet());
 	        locationFacadeLocal.edit(location);
 	        msg = "Eintrag wurde bearbeitet.";
            addMessage("messages", msg);
 	    }
 	    catch (Exception e) {
            msg = "Eintrag wurde nicht bearbeitet.";
            addMessage("messages", msg);
 	    }
    	locationList = getLocationListAll();
    	em.close();
    }
    
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
