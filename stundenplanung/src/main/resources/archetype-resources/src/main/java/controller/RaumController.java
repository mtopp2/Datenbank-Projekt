package controller;

import model.Location;
import model.Raum;
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
	
	/**
	 * Initialisierung
	 */
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
	
	// Getter und Setter
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
	
	/**
	 * Erstellen eines Raumes
	 * @throws Exception
	 */
	public void createRoom() throws Exception  {
		String msg;
		EntityManager em = emf.createEntityManager();
		Raum rau = new Raum();  
		rau.setRName(roomName);
		rau.setKapazitaet(capacity);
		rau.setNachbarRaum(roomNeighbor);
		rau.setLocation(findLoc(locationId));
		try {
	        raumFacadeLocal.create(rau);
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
	 * Schaut ob Raumname und Kapazität gesetzt worden ist, danach wird der Raum erstellt und zum Schluß wird die Liste aktualisiert.
	 * @throws SecurityException
	 * @throws SystemException
	 * @throws NotSupportedException
	 * @throws RollbackException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws Exception
	 */
	public void createDoRoom() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(roomNameOk == true && capacityOk == true) {
			createRoom();
			roomList = getRaumList();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Laden der Raumliste
	 * @return
	 */
	public List<Raum> getRaumList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Raum> query = em.createNamedQuery("Raum.findAll", Raum.class);
		return query.getResultList();
	}
	
	/**
	 * Laden der Standortsliste
	 * @return
	 */
	public List<Location> getLocationList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Location> query = em.createNamedQuery("Location.findAll", Location.class);
		return query.getResultList();
	}
	
	/**
	 * Ausgewählte Zeile wird in roomSelected gespeichert, sowie der Fremdschlüssel
	 * @param e
	 */
	public void onRowSelect(SelectEvent<Raum> e) {
    	FacesMessage msg = new FacesMessage("Raum ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        roomSelected = e.getObject();
        
        locationId = roomSelected.getLocation().getLid();
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Löscht einen Raum
     * @throws Exception
     */
    public void deleteRoom() throws Exception {
    	String msg;
        roomList.remove(roomSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Raum> q = em.createNamedQuery("Raum.findByRid",Raum.class);
        q.setParameter("rid", roomSelected.getRid());
        room = (Raum)q.getSingleResult();
        
        try {
        	this.raumFacadeLocal.remove(room);
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
     * Finden eines Standortes anhand der ID
     * @param locationId
     * @return
     */
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
    
    /**
     * Bearbeiten eines Raumes
     */
    public void addRoom(){
    	EntityManager em = emf.createEntityManager();
    	String msg;
	   	 try {
	        em.find(Raum.class, roomSelected.getRid());
	        room.setRid(roomSelected.getRid());
	        room.setRName(roomSelected.getRName());
	        room.setKapazitaet(roomSelected.getKapazitaet());
	        room.setNachbarRaum(roomSelected.getNachbarRaum());
	        room.setLocation(findLoc(locationId));
	        raumFacadeLocal.edit(room);
	        msg = "Eintrag wurde bearbeitet.";
	        addMessage("messages", msg);
		    }
		    catch (Exception e) {
		    	msg = "Eintrag wurde nicht bearbeitet.";
	            addMessage("messages", msg);
		    }
	   	roomList = getRaumList();
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
