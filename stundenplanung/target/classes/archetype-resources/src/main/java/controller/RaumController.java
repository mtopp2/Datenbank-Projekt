package controller;

import model.Location;
import model.Raum;
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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;

import EJB.FacultyFacadeLocal;
import EJB.RaumFacadeLocal;

/**
*
* @author Anil
*/

@Named(value="raumController")
@SessionScoped
public class RaumController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Raum room;
	private Location location;
	
	@EJB
	private RaumFacadeLocal raumFacadeLocal;
	
	@PostConstruct
    public void init() {
        roomList = getRaumList();
        locationList = getLocationList();
    }
 
    List<Location> locationList;
    private int locationId;

	private Integer capacity;
	private String roomNeighbor;
	private String roomName;
	private boolean capacityOk = false;
	private boolean roomNameOk = false;
	
	List<Raum> roomList;
	
	private Raum roomSelected;
	
	
	
	
	
	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public Raum getRoom() {
		return room;
	}

	public void setRoom(Raum room) {
		this.room = room;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		if(capacity!=null){
			this.capacity = capacity;
			capacityOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Kapazitaet konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("RaumForm:kapazitaet_reg", message);
	    }
	}

	public String getRoomNeighbor() {
		return roomNeighbor;
	}

	public void setRoomNeighbor(String roomNeighbor) {
			this.roomNeighbor = roomNeighbor;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		
		if(roomName!=null){
			this.roomName = roomName;
	        roomNameOk=true;
	    }
		else{
	    	FacesMessage message = new FacesMessage("Raumname konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("RaumForm:RName_reg", message);
	    }
	}

	public List<Raum> getRoomList() {
		return roomList;
	}

	public void setRoomList(List<Raum> roomList) {
		this.roomList = roomList;
	}

	public Raum getRoomSelected() {
		return roomSelected;
	}

	public void setRoomSelected(Raum roomSelected) {
		this.roomSelected = roomSelected;
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createRoom() {
		Raum rau = new Raum();  
		rau.setRName(roomName);
		rau.setKapazitaet(capacity);
		rau.setNachbarRaum(roomNeighbor);
		rau.setLocation(findLoc(locationId));
	    raumFacadeLocal.create(rau);
	}
	
	public void createDoRoom() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(roomNameOk == true && capacityOk == true) {
			createRoom();
			roomList = getRaumList();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Raum> getRaumList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Raum> query = em.createNamedQuery("Raum.findAll", Raum.class);
		roomList = query.getResultList();
		return query.getResultList();
	}
	
	public List<Location> getLocationList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Location> query = em.createNamedQuery("Location.findAll", Location.class);
		locationList = query.getResultList();
		return locationList;
	}
	
	public void onRowSelect(SelectEvent<Raum> e) {
    	FacesMessage msg = new FacesMessage("Raum ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        roomSelected = e.getObject();
        
        locationId = roomSelected.getLocation().getLid();
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteRoom() {
        roomList.remove(roomSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Raum> q = em.createNamedQuery("Raum.findByRid",Raum.class);
        q.setParameter("rid", roomSelected.getRid());
        room = (Raum)q.getSingleResult();
        this.raumFacadeLocal.remove(room);
		em.close();
    }
    
    private Location findLoc(int locationId) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Location> query
                = em.createNamedQuery("Location.findByLid",Location.class);
            query.setParameter("lid", locationId);
            location = (Location)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return location;
    }
    
    public void addRoom(){
        EntityManager em = emf.createEntityManager();
        em.find(Raum.class, roomSelected.getRid());
        room.setRid(roomSelected.getRid());
        room.setRName(roomSelected.getRName());
        room.setKapazitaet(roomSelected.getKapazitaet());
        room.setNachbarRaum(roomSelected.getNachbarRaum());
        room.setLocation(findLoc(locationId));
        raumFacadeLocal.edit(room);
        roomList = getRaumList();
		em.close();
   }
  
}
