package controller;

import model.Modul;
import model.Dozenten;
import model.Studiengang;
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
* @author Anil
*/

//@ManagedBean(name="SgmodulController")
@Named(value="sgmodulController")
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
        	ModulListe.add(mod.getModName());
        }
        Query s = em.createNamedQuery("Studiengang.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Studiengang sg =(Studiengang)SListitem;
        	StudiengangListe.add(sg.getSGName());
        }
        Query d = em.createNamedQuery("Dozenten.findAll");
        List DList = d.getResultList();
        for (Object DListitem : DList)
        {
        	Dozenten doz =(Dozenten)DListitem;
        	DozentenListe.add(doz.getDName());
        }
    }
 
    ArrayList<String> DozentenListe = new ArrayList<>();
    ArrayList<String> ModulListe = new ArrayList<>();
    ArrayList<String> StudiengangListe = new ArrayList<>();
    
    private int modSemester;
	private String SGMNotiz;
    private String SGName;
	private String DName;
	private String modName;
	
	
	List<Sgmodul> sgmodullist;
	
	private Sgmodul selectedsgmodul;
	
	
	public String getSGMNotiz() {
		return SGMNotiz;
	}

	public void setSGMNotiz(String SGMNotiz) {
		this.SGMNotiz = SGMNotiz;
	}

	public ArrayList<String> getDozentenListe() {
		return DozentenListe;
	}

	public void setDozentenListe(ArrayList<String> dozentenListe) {
		this.DozentenListe = dozentenListe;
	}
	
	public ArrayList<String> getStudiengangListe() {
		return StudiengangListe;
	}

	public void setStudiengangListe(ArrayList<String> studiengangListe) {
		this.StudiengangListe = studiengangListe;
	}
	public ArrayList<String> getModulListe() {
		return ModulListe;
	}

	public void setModulListe(ArrayList<String> modulListe) {
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

	public String getDName() {
		return DName;
	}

	public void setDName(String DName) {
			this.DName = DName;
	}
	
	public String getModName() {
		return modName;
	}

	public void setModName(String modName) {
			this.modName = modName;
	}

	public int getModSemester() {
		return modSemester;
	}

	public void setModSemester(int modSemester) {
		this.modSemester = modSemester;
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
		sgm.setSGMNotiz(SGMNotiz);
		sgm.setModSemester(modSemester);
		sgm.setModul(findMod(modName));
		sgm.setDozenten(findDoz(DName));
		sgm.setStudiengang(findSg(SGName));
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
		
			createSgmodul();
			return "showsgmodul.xhtml";
	
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
	        sgmodul.setModSemester(newsgm.getModSemester());
	        sgmodul.setSGMNotiz(newsgm.getSGMNotiz());
	        sgmodul.setModul(findMod(newsgm.getModul().getModName()));
	        sgmodul.setDozenten(findDoz(newsgm.getDozenten().getDName()));
	        sgmodul.setStudiengang(findSg(newsgm.getStudiengang().getSGName()));
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
    
    private Modul findMod(String mod) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Modul> query
                = em.createNamedQuery("Modul.findByModName",Modul.class);
            query.setParameter("modName", mod);
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
    
    private Dozenten findDoz(String doz) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Dozenten> query
                = em.createNamedQuery("Dozenten.findByDName",Dozenten.class);
            query.setParameter("DName", doz);
            dozenten = (Dozenten)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return dozenten;
    }
}
