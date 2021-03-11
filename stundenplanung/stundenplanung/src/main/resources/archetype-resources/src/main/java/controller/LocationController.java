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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
			FacesMessage message = new FacesMessage("Stadt bereits vorhanden.");
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
	public void createLocation() throws Exception  {
		EntityManager em = emf.createEntityManager();
		Location loc = new Location();  
		loc.setLCity(locationCity);    
		loc.setLStreet(locationStreet);      
		try {
			locationFacadeLocal.create(loc);
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
	
	public void createDoLocation() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(locationCityOk == true && locationStreetOk == true) {
			createLocation();
			locationList = getLocationListAll();
			
		}
	}
	
	public List<Location> getLocationListAll(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Location> query = em.createNamedQuery("Location.findAll", Location.class);
		locationList = query.getResultList();
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteLocation() throws Exception {
    	locationList.remove(locationSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Location> q = em.createNamedQuery("Location.findByLid",Location.class);
        q.setParameter("lid", locationSelected.getLid());
        location = (Location)q.getSingleResult();
        
        try {
        	locationFacadeLocal.remove(location);
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
    
    //----------------------------------------------------------------------------------------------------------------------------------------------
    public void onRowSelect(SelectEvent<Location> e) {
    	FacesMessage msg = new FacesMessage("Standort ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        locationSelected = e.getObject();
        
    }
    
    public void addLocation(){
    	 try {
 	        EntityManager em = emf.createEntityManager();
 	        em.find(Location.class, locationSelected.getLid());
 	        location.setLid(locationSelected.getLid());
 	        location.setLCity(locationSelected.getLCity());
 	        location.setLStreet(locationSelected.getLStreet());
 	        locationFacadeLocal.edit(location);
 	    }
 	    catch (Exception e) {
 	        try {
 	            ut.rollback();
 	        } 
 	        catch (IllegalStateException | SecurityException | SystemException ex) {
 	        }
 	    }
    }
}
