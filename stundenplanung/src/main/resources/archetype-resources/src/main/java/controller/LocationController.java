package controller;

import model.Location;
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
	
	@PostConstruct
    public void init() {
        locList = getLocationList();
    }
	
	private String lCity;
	private String lStreet;
	private boolean lCityOk = false;
	private boolean lStreetOk = false;
	
	private Location selectedLocation;
	
	List<Location> locList;
	
	
    public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public List<Location> getLocList() {
		return locList;
	}

	public Location getSelectedLocation() {
		return selectedLocation;
	}

	public void setSelectedLocation(Location selectedLocation) {
		this.selectedLocation = selectedLocation;
	}

	public String getLCity() {
		return this.lCity;
	}

	public void setLCity(String lCity) {
		if(lCity != null){
			this.lCity = lCity;
			lCityOk = true;
		}
		else{
			FacesMessage message = new FacesMessage("Stadt bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("LocationForm:modKuerzel_reg", message);
	    }
	}

	public String getLStreet() {
		return this.lStreet;
	}

	public void setLStreet(String lStreet) {
		if(lStreet != null){
			this.lStreet = lStreet;
			lStreetOk = true;
		}
		else{
			FacesMessage message = new FacesMessage("Straße bereits vorhanden.");
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
	public void createLocation() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Location loc = new Location();  
		loc.setLCity(lCity);    
		loc.setLStreet(lStreet);      
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(loc);  
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
	
	public void createDoLocation() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(lCityOk == true && lStreetOk == true) {
			createLocation();
			locList = getLocationList();
			
		}
	}
	
	public List<Location> getLocationList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Location> query = em.createNamedQuery("Location.findAll", Location.class);
		locList = query.getResultList();
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteLocation() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        locList.remove(selectedLocation);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Location> q = em.createNamedQuery("Location.findByLid",Location.class);
        q.setParameter("lid", selectedLocation.getLid());
        location = (Location)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(location);
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
    
    //----------------------------------------------------------------------------------------------------------------------------------------------
    public void onRowSelect(SelectEvent<Location> e) {
    	FacesMessage msg = new FacesMessage("Standort ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        selectedLocation = e.getObject();
        
    }
    
    public void addLocation(){
    	 try {
 	        ut.begin();
 	        EntityManager em = emf.createEntityManager();
 	        em.find(Location.class, selectedLocation.getLid());
 	        location.setLid(selectedLocation.getLid());
 	        location.setLCity(selectedLocation.getLCity());
 	        location.setLStreet(selectedLocation.getLStreet());
 	        em.merge(location);
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
}
