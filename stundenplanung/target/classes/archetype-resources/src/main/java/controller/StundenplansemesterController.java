package controller;

import model.Stundenplansemester;
import model.Stundenplanstatus;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
* @author Manuel
*/

@ManagedBean(name="StundenplansemesterController")
@SessionScoped
public class StundenplansemesterController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Stundenplansemester stundenplansemester;
	private Stundenplanstatus stundenplanstatus;

	
	@PostConstruct
    public void init() {
        spslist = getStundenplansemesterList();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Stundenplanstatus.findAll");
        List SList = q.getResultList();
        for (Object SListitem : SList)
        {
            Stundenplanstatus sps =(Stundenplanstatus)SListitem;
            StundenplanstatusListe.add(sps.getSPSTBezeichnung());
        }
    }
 
    ArrayList<String> StundenplanstatusListe = new ArrayList<>();

    private int SPJahr;
	private Integer SPKw;
	private String SPSemester;
	private Date startDatum;
	private boolean SPKw_ok = false;
	private boolean SPSemester_ok = false;
	private boolean startDatum_ok = false;
	
	private String SPSTBezeichnung;
	
	List<Stundenplansemester> spslist;
	
	private Stundenplansemester selectedsps;
	
	

	public ArrayList<String> getStundenplanstatusListe() {
		return StundenplanstatusListe;
	}

	public void setStundenplanstatusListe(ArrayList<String> stundenplanstatusListe) {
		StundenplanstatusListe = stundenplanstatusListe;
	}

	public String getSPSTBezeichnung() {
		return SPSTBezeichnung;
	}

	public void setSPSTBezeichnung(String sPSTBezeichnung) {
		SPSTBezeichnung = sPSTBezeichnung;
	}

	public Stundenplansemester getStundenplansemester() {
		return stundenplansemester;
	}

	public void setStundenplansemester(Stundenplansemester stundenplansemester) {
		this.stundenplansemester = stundenplansemester;
	}

	public Stundenplanstatus getStundenplanstatus() {
		return stundenplanstatus;
	}

	public void setStundenplanstatus(Stundenplanstatus stundenplanstatus) {
		this.stundenplanstatus = stundenplanstatus;
	}

	public int getSPJahr() {
		return SPJahr;
	}

	public void setSPJahr(int sPJahr) {
		this.SPJahr = sPJahr;
	}

	public Integer getSPKw() {
		return SPKw;
	}

	public void setSPKw(Integer SPKw) {
		if(SPKw!=null){
			this.SPKw = SPKw;
	        SPKw_ok=true;
	    }
		else{
	    	FacesMessage message = new FacesMessage("Stundenplankalenderwoche konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("StundenplansemesterForm:SPKw_reg", message);
	    }
	}

	public String getSPSemester() {
		return SPSemester;
	}

	public void setSPSemester(String SPSemester) {
		if(SPSemester!=null){
			this.SPSemester = SPSemester;
	        SPSemester_ok=true;
	    }
		else{
	    	FacesMessage message = new FacesMessage("Stundenplansemester konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("StundenplansemesterForm:SPSemester_reg", message);
	    }
	}

	public Date getStartDatum() {
		return startDatum;
	}

	public void setStartDatum(Date startDatum) {
		if(startDatum!=null){
			this.startDatum = startDatum;
	        startDatum_ok=true;
	    }
		else{
	    	FacesMessage message = new FacesMessage("Startdatum konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("StundenplansemesterForm:startDatum_reg", message);
	    }
	}

	public List<Stundenplansemester> getSpslist() {
		return spslist;
	}

	public void setSpslist(List<Stundenplansemester> spslist) {
		this.spslist = spslist;
		
	}

	public Stundenplansemester getSelectedsps() {
		return selectedsps;
	}

	public void setSelectedsps(Stundenplansemester selectedsps) {
		this.selectedsps = selectedsps;
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createStundenplansemester() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Stundenplansemester sps = new Stundenplansemester();
		sps.setSPSemester(SPSemester);
		sps.setSPJahr(SPJahr);
		sps.setSPKw(SPKw);
		sps.setStartDatum(startDatum);
		sps.setStundenplanstatus(findSps(SPSTBezeichnung));
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(sps);  
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
	
	public String createDoStundenplansemester() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(SPSemester_ok == true && SPKw_ok == true && startDatum_ok) {
			createStundenplansemester();
			return "showstundenplansemester.xhtml";
		}
		else{
			return "createstundenplansemester.xhtml";
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Stundenplansemester> getStundenplansemesterList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Stundenplansemester> query = em.createNamedQuery("Stundenplansemester.findAll", Stundenplansemester.class);
		spslist = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowEdit(RowEditEvent<Stundenplansemester> event) {
        FacesMessage msg = new FacesMessage("Stundenplansemester Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        Stundenplansemester newsps = new Stundenplansemester();
        newsps = event.getObject();
        
        try {
	        ut.begin();
	        EntityManager em = emf.createEntityManager();
	        em.find(Stundenplansemester.class, newsps.getSpsid());
	        stundenplansemester.setSpsid(newsps.getSpsid());
	        stundenplansemester.setSPSemester(newsps.getSPSemester());
	        stundenplansemester.setSPJahr(newsps.getSPJahr());
	        stundenplansemester.setSPKw(newsps.getSPKw());
	        stundenplansemester.setStartDatum(newsps.getStartDatum());
	        stundenplansemester.setStundenplanstatus(findSps(newsps.stundenplanstatus.getSPSTBezeichnung()));
	        em.merge(stundenplansemester);
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
     
    public void onRowCancel(RowEditEvent<Stundenplansemester> event) {
    	FacesMessage msg = new FacesMessage("Stundenplansemester Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteStundenplansemester() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        spslist.remove(selectedsps);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Stundenplansemester> q = em.createNamedQuery("Stundenplansemester.findBySpsid",Stundenplansemester.class);
        q.setParameter("spsid", selectedsps.getSpsid());
        stundenplansemester = (Stundenplansemester)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(stundenplansemester);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
        selectedsps = null;
		em.close();
    }
    
    private Stundenplanstatus findSps(String SPSTBezeichnung) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Stundenplanstatus> query
                = em.createNamedQuery("Stundenplanstatus.findBySPSTBezeichnung",Stundenplanstatus.class);
            query.setParameter("SPSTBezeichnung", SPSTBezeichnung);
            stundenplanstatus = (Stundenplanstatus)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return stundenplanstatus;
    }
  
}