package controller;

import model.Modul;
import model.Dozenten;
import model.Studiengang;
import model.Stundenplansemester;
import model.Sgmodul;
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
* @author Manuel
*/

@ManagedBean(name="SgmodulController")
@SessionScoped
public class SgmodulController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Sgmodul sgmodul;
	private Studiengang studiengang;
	private Modul modul;
	private Dozenten dozenten;
	
	@PostConstruct
    public void init() {
        sgmodullist = getSgmodulList();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Modul.findAll");
		List FList = q.getResultList();
        for (Object FListitem : FList)
        {
        	Modul mod =(Modul)FListitem;
        	ModulListe.add(mod.getModID());
        }
        EntityManager em1 = emf.createEntityManager();
        Query s = em1.createNamedQuery("Studiengang.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Studiengang sg =(Studiengang)SListitem;
        	StudiengangListe.add(sg.getSGName());
        }
        EntityManager em2 = emf.createEntityManager();
        Query d = em2.createNamedQuery("Dozenten.findAll");
        List DList = d.getResultList();
        for (Object DListitem : DList)
        {
        	Dozenten doz =(Dozenten)DListitem;
        	DozentenListe.add(doz.getDid());
        }
    }
 
    ArrayList<Integer> DozentenListe = new ArrayList<>();
    ArrayList<Integer> ModulListe = new ArrayList<>();
    ArrayList<String> StudiengangListe = new ArrayList<>();
    
    private Integer modSemester;
    private boolean modSemester_ok = false;
	private String SGMNotiz;
    private String SGName;
	private int did;
	private int mid;
	
	
	List<Sgmodul> sgmodullist;
	
	private Sgmodul selectedsgmodul;
	
	
	public String getSGMNotiz() {
		return SGMNotiz;
	}

	public void setSGMNotiz(String SGMNotiz) {
		this.SGMNotiz = SGMNotiz;
	}

	public ArrayList<Integer> getDozentenListe() {
		return DozentenListe;
	}

	public void setDozentenListe(ArrayList<Integer> dozentenListe) {
		this.DozentenListe = dozentenListe;
	}
	
	public ArrayList<String> getStudiengangListe() {
		return StudiengangListe;
	}

	public void setStudiengangListe(ArrayList<String> studiengangListe) {
		this.StudiengangListe = studiengangListe;
	}
	public ArrayList<Integer> getModulListe() {
		return ModulListe;
	}

	public void setModulListe(ArrayList<Integer> modulListe) {
		this.ModulListe = modulListe;
	}

	public Studiengang getStudiengang() {
		return studiengang;
	}

	public void setStudiengang(Studiengang studiengang) {
		this.studiengang = studiengang;
	}
	
	public Modul getModul() {
		return modul;
	}

	public void setModul(Modul modul) {
		this.modul = modul;
	}
	
	public Dozenten getDozenten() {
		return dozenten;
	}

	public void setDozenten(Dozenten dozenten) {
		this.dozenten = dozenten;
	}
	
	public Sgmodul getSgmodul() {
		return sgmodul;
	}

	public void setSgmodul(Sgmodul sgmodul) {
		this.sgmodul = sgmodul;
	}

	public String getSGName() {
		return SGName;
	}

	public void setSGName(String SGName) {
			this.SGName = SGName;
	}			

	public int getDid() {
		return did;
	}

	public void setDid(int did) {
			this.did = did;
	}
	
	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
			this.mid = mid;
	}

	public Integer getModSemester() {
		return modSemester;
	}

	public void setModSemester(Integer modSemester) {
		if(modSemester!=null){
			this.modSemester = modSemester;
			modSemester_ok=true;
	    }
		else{
	    	FacesMessage message = new FacesMessage("Modulsemester konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("SgmodulForm:modSemester_reg", message);
	    }
	}

	public List<Sgmodul> getSgmodullist() {
		return sgmodullist;
	}
	
	public void setSgmodullist(List<Sgmodul> sgmodullist) {
		this.sgmodullist = sgmodullist;
		
	}

	
	public Sgmodul getSelectedsgmodul() {
		return selectedsgmodul;
	}

	public void setSelectedsgmodul(Sgmodul selectedsgmodul) {
		this.selectedsgmodul = selectedsgmodul;
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createSgmodul() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Sgmodul sgm = new Sgmodul();  
		sgm.setModul(findMod(mid));
		sgm.setDozenten(findDoz(did));
		sgm.setStudiengang(findSg(SGName));
		sgm.setModSemester(modSemester);
		sgm.setSGMNotiz(SGMNotiz);
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(sgm);  
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
	
	public String createDoSgmodul() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(modSemester_ok == true) {
			createSgmodul();
			return "showsgmodul.xhtml";
		}else {
			return "createsgmodul.xhtml";
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Sgmodul> getSgmodulList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Sgmodul> query = em.createNamedQuery("Sgmodul.findAll", Sgmodul.class);
		sgmodullist = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowEdit(RowEditEvent<Sgmodul> event) {
        FacesMessage msg = new FacesMessage("Sgmodul Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        Sgmodul newsgm = new Sgmodul();
        newsgm = event.getObject();
        
        try {
	        ut.begin();
	        EntityManager em = emf.createEntityManager();
	        em.find(Sgmodul.class, newsgm.getSgmid());
	        sgmodul.setSgmid(newsgm.getSgmid());
	        sgmodul.setModul(findMod(newsgm.modul.getModID()));
	        //sgmodul.setModul(findMod2());
	        sgmodul.setStudiengang(findSg(newsgm.studiengang.getSGName()));
	        sgmodul.setDozenten(findDoz(newsgm.dozenten.getDid()));
	        sgmodul.setModSemester(newsgm.getModSemester());
	        sgmodul.setSGMNotiz(newsgm.getSGMNotiz());
	        em.merge(sgmodul);
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
     
    public void onRowCancel(RowEditEvent<Sgmodul> event) {
    	FacesMessage msg = new FacesMessage("Sgmodul Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteSgmodul() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        sgmodullist.remove(selectedsgmodul);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Sgmodul> q = em.createNamedQuery("Sgmodul.findBySgmid",Sgmodul.class);
        q.setParameter("sgmid", selectedsgmodul.getSgmid());
        sgmodul = (Sgmodul)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(sgmodul);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
        selectedsgmodul = null;
		em.close();
    }
    
    private Modul findMod(int mid) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Modul> query
                = em.createNamedQuery("Modul.findByModID",Modul.class);
            query.setParameter("modID", mid);
            modul = (Modul)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return modul;
    }
    
    private Studiengang findSg(String sg) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Studiengang> query
                = em.createNamedQuery("Studiengang.findBySGName",Studiengang.class);
            query.setParameter("SGName", sg);
            studiengang = (Studiengang)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return studiengang;
    }
    
    private Dozenten findDoz(int did) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Dozenten> query
                = em.createNamedQuery("Dozenten.findByDid",Dozenten.class);
            query.setParameter("did", did);
            dozenten = (Dozenten)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return dozenten;
    }
    
}
