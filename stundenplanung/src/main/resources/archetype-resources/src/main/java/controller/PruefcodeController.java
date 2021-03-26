package controller;

import model.Studiengang;
import model.Pruefcode;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;


import org.primefaces.event.SelectEvent;
import javax.ejb.EJB;
import EJB.PruefcodeFacadeLocal;

/**
*
* @author Anil
*/

@Named(value="pruefcodeController")
@SessionScoped
public class PruefcodeController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Pruefcode code;
	private Studiengang course;
	
	@EJB
	private PruefcodeFacadeLocal pruefcodeFacadeLocal;
	
	/**
	 * Initialisierung
	 */
	@PostConstruct
    public void init() {
        codeList = getCodeListAll();
        EntityManager em = emf.createEntityManager();
        
        Query s = em.createNamedQuery("Studiengang.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Studiengang sg =(Studiengang)SListitem;
        	courseList.add(sg);
        }
    }
 
	ArrayList<Studiengang> courseList = new ArrayList<>();
	
	private int courseId;
	private String dutyOrChoice;
	private int verifyCode;
	private String specializationShort;

	List<Pruefcode> codeList;
	
	private Pruefcode codeSelected;
	
	// Getter und Setter
	public Pruefcode getCodeSelected() {
		return codeSelected;
	}
	  
	public void setCodeSelected(Pruefcode codeSelected) {
		this.codeSelected = codeSelected;
	}
	  
    public List<Pruefcode> getCodeList() {
        return codeList;
    }
    
	public Pruefcode getCode() {
		return code;
	}
	  
	public void setCode(Pruefcode code) {
		this.code = code;
	}
	
	public ArrayList<Studiengang> getCourseList() {
		return courseList;
	}

	public void setCourseList(ArrayList<Studiengang> courseList) {
		this.courseList = courseList;
	}
	  
	public String getSpecializationShort() {
		return specializationShort;
	}
	  
	public void setSpecializationShort(String specializationShort) {	
			this.specializationShort = specializationShort;			
	}
	  
	public String getDutyOrChoice() {
		return dutyOrChoice;
	}
	  
	public void setDutyOrChoice(String dutyOrChoice) {
	        this.dutyOrChoice = dutyOrChoice;
	}
	  
	public Integer getVerifyCode() {
		return verifyCode;
	}
	  
	public void setVerifyCode(Integer verifyCode) {
	        this.verifyCode = verifyCode;
	}
	
	public Studiengang getCourse() {
		return course;
	}

	public void setCourse(Studiengang course) {
		this.course = course;
	}
	
	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
			this.courseId = courseId;
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;
	
	/**
	 * Erstellen eines Prüfcodeeintrag
	 * @throws Exception
	 */
	public void createPruefcode() throws Exception  {
		String msg;
		EntityManager em = emf.createEntityManager();
		Pruefcode pCode = new Pruefcode();  
		pCode.setPflichtOderWahl(dutyOrChoice);    
		pCode.setPrCode(verifyCode);      
		pCode.setVertiefungsrichtungShortName(specializationShort);
		pCode.setStudiengang(findSg(courseId));
		try {
			pruefcodeFacadeLocal.create(pCode);
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
	 * Erstellt einen Prüfcodeeintrag und danach wird die Liste aktualisiert
	 * @throws SecurityException
	 * @throws SystemException
	 * @throws NotSupportedException
	 * @throws RollbackException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws Exception
	 */
	public void createDoPruefcode() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
			createPruefcode();
			codeList = getCodeListAll();
		
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Laden der Prüfcodeliste
	 * @return
	 */
	public List<Pruefcode> getCodeListAll(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Pruefcode> query = em.createNamedQuery("Pruefcode.findAll", Pruefcode.class);
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Löschen eines Prüfcodes
     * @throws Exception
     */
    public void deletePruefcode() throws Exception {
    	String msg;
        codeList.remove(codeSelected);
        EntityManager em = emf.createEntityManager();
        TypedQuery<Pruefcode> q = em.createNamedQuery("Pruefcode.findByPcid",Pruefcode.class);
        q.setParameter("pcid", codeSelected.getPcid());
        code = (Pruefcode)q.getSingleResult();
        
        try {
        	pruefcodeFacadeLocal.remove(code);
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
     * Ausgewählte Zeile wird in codeSelected gespeichert sowie der Fremdschlüssel
     * @param e
     */
    public void onRowSelect(SelectEvent<Pruefcode> e) {
    	FacesMessage msg = new FacesMessage("Pruefcode ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        codeSelected = e.getObject();
        courseId = codeSelected.getStudiengang().getSgid();
        
    }
    
    /**
     * Bearbeiten eines Prüfcodeeintrages
     */
    public void addPruefcode(){
    	String msg;
    	EntityManager em = emf.createEntityManager();
    	 try { 
	       em.find(Pruefcode.class, codeSelected.getPcid());
	       code.setPflichtOderWahl(codeSelected.getPflichtOderWahl());
	       code.setPrCode(codeSelected.getPrCode());
	       code.setVertiefungsrichtungShortName(codeSelected.getVertiefungsrichtungShortName());
	       code.setPcid(codeSelected.getPcid());
	       code.setStudiengang(findSg(courseId));
	       pruefcodeFacadeLocal.edit(code);
 	       msg = "Eintrag wurde bearbeitet.";
           addMessage("messages", msg);
 	    }
 	    catch (Exception e) {
 	    	msg = "Eintrag wurde nicht bearbeitet.";
            addMessage("messages", msg);
 	    }
    	 codeList = getCodeListAll();
    	 em.close();
    }
    
    /**
     * Finden eines Studienganges anhand der ID
     * @param sg
     * @return
     */
    private Studiengang findSg(int sg) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Studiengang> query
                = em.createNamedQuery("Studiengang.findBySgid",Studiengang.class);
            query.setParameter("sgid", sg);
            course = (Studiengang)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return course;
    }
    
   // ---------------------------------------------------------------------------------------------------------------------
	  
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