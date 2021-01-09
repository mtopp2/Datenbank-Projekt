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
import javax.faces.bean.ManagedBean;
import controller.MessageForPrimefaces;

/**
*
* @author Anil
*/

//@ManagedBean(name="RaumController")
@Named(value="raumController")
@SessionScoped
public class RaumController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Raum raum;
	private Location location;
	
	@PostConstruct
    public void init() {
        raumlist = getRaumList();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Location.findAll");
        List LList = q.getResultList();
        for (Object LListitem : LList)
        {
            Location loc =(Location)LListitem;
            LocationListe.add(loc.getLStreet());
        }
    }
 
    ArrayList<String> LocationListe = new ArrayList<>();
    private String LStreet;

	private Integer kapazitaet;
	private String nachbarRaum;
	private String RName;
	private boolean kapazitaet_ok = false;
	private boolean RName_ok = false;
	
	List<Raum> raumlist;
	
	private Raum selectedraum;
	
	
	public String getLStreet() {
		return LStreet;
	}

	public void setLStreet(String lStreet) {
		LStreet = lStreet;
	}

	public ArrayList<String> getLocationListe() {
		return LocationListe;
	}

	public void setLocationListe(ArrayList<String> locationListe) {
		LocationListe = locationListe;
	}

	public Raum getRaum() {
		return raum;
	}

	public void setRaum(Raum raum) {
		this.raum = raum;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Integer getKapazitaet() {
		return kapazitaet;
	}

	public void setKapazitaet(Integer kapazitaet) {
		if(kapazitaet!=null){
			this.kapazitaet = kapazitaet;
			kapazitaet_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Kapazitaet konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("RaumForm:kapazitaet_reg", message);
	    }
	}

	public String getNachbarRaum() {
		return nachbarRaum;
	}

	public void setNachbarRaum(String nachbarRaum) {
			this.nachbarRaum = nachbarRaum;
	}

	public String getRName() {
		return RName;
	}

	public void setRName(String rName) {
		
		if(rName!=null){
			RName = rName;
	        RName_ok=true;
	    }
		else{
	    	FacesMessage message = new FacesMessage("Raumname konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("RaumForm:RName_reg", message);
	    }
	}

	public List<Raum> getRaumlist() {
		return raumlist;
	}

	public void setRaumlist(List<Raum> raumlist) {
		this.raumlist = raumlist;
	}

	public Raum getSelectedraum() {
		return selectedraum;
	}

	public void setSelectedraum(Raum selectedraum) {
		this.selectedraum = selectedraum;
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createRaum() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Raum rau = new Raum();  
		rau.setRName(RName);
		rau.setKapazitaet(kapazitaet);
		rau.setNachbarRaum(nachbarRaum);
		rau.setLocation(findLoc(LStreet));
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
	
	public String createDoRaum() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(RName_ok == true && kapazitaet_ok == true) {
			createRaum();
			return "showraum.xhtml";
		}
		else{
			return "createraum.xhtml";
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Raum> getRaumList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Raum> query = em.createNamedQuery("Raum.findAll", Raum.class);
		raumlist = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowEdit(RowEditEvent<Raum> event) {
        FacesMessage msg = new FacesMessage("Raum Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        Raum newrau = new Raum();
        newrau = event.getObject();
        
        try {
	        ut.begin();
	        EntityManager em = emf.createEntityManager();
	        em.find(Raum.class, newrau.getRid());
	        raum.setRid(newrau.getRid());
	        raum.setRName(newrau.getRName());
	        raum.setKapazitaet(newrau.getKapazitaet());
	        raum.setNachbarRaum(newrau.getNachbarRaum());
	        raum.setLocation(findLoc(newrau.getLocation().getLStreet()));
	        em.merge(raum);
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
     
    public void onRowCancel(RowEditEvent<Raum> event) {
    	FacesMessage msg = new FacesMessage("Raum Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteRaum() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        raumlist.remove(selectedraum);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Raum> q = em.createNamedQuery("Raum.findByRid",Raum.class);
        q.setParameter("rid", selectedraum.getRid());
        raum = (Raum)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(raum);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
        selectedraum = null;
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
  
}
