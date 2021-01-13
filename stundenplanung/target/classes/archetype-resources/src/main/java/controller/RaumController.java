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
	
	@PostConstruct
    public void init() {
        roomList = getRaumList();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Location.findAll");
        List LList = q.getResultList();
        for (Object LListitem : LList)
        {
            Location loc =(Location)LListitem;
            locationList.add(loc.getLStreet());
        }
    }
 
    ArrayList<String> locationList = new ArrayList<>();
    private String locationStreet;

	private Integer capacity;
	private String roomNeighbor;
	private String roomName;
	private boolean capacityOk = false;
	private boolean roomNameOk = false;
	
	List<Raum> roomList;
	
	private Raum roomSelected;
	
	
	public String getLocationStreet() {
		return locationStreet;
	}

	public void setLStreet(String locationStreet) {
		this.locationStreet = locationStreet;
	}

	public ArrayList<String> getLocationList() {
		return locationList;
	}

	public void setLocationList(ArrayList<String> locationList) {
		this.locationList = locationList;
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
	public void createRoom() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Raum rau = new Raum();  
		rau.setRName(roomName);
		rau.setKapazitaet(capacity);
		rau.setNachbarRaum(roomNeighbor);
		rau.setLocation(findLoc(locationStreet));
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(rau);  
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
	
	public void onRowSelect(SelectEvent<Raum> e) {
    	FacesMessage msg = new FacesMessage("Raum ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        roomSelected = e.getObject();
        
        locationStreet = roomSelected.getLocation().getLStreet();
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteRoom() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        roomList.remove(roomSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Raum> q = em.createNamedQuery("Raum.findByRid",Raum.class);
        q.setParameter("rid", roomSelected.getRid());
        room = (Raum)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(room);
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
    
    private Location findLoc(String LStreet) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Location> query
                = em.createNamedQuery("Location.findByLStreet",Location.class);
            query.setParameter("LStreet", LStreet);
            location = (Location)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return location;
    }
    
    public void addRoom(){
   	 try {
   		ut.begin();
        EntityManager em = emf.createEntityManager();
        em.find(Raum.class, roomSelected.getRid());
        room.setRid(roomSelected.getRid());
        room.setRName(roomSelected.getRName());
        room.setKapazitaet(roomSelected.getKapazitaet());
        room.setNachbarRaum(roomSelected.getNachbarRaum());
        room.setLocation(findLoc(locationStreet));
        em.merge(room);
        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
   	roomList = getRaumList();
   }
  
}
