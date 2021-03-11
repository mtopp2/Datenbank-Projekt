package controller;

import model.Studiengang;
import model.Pruefcode;
import model.Modul;
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

import com.sun.javafx.logging.Logger;

import org.primefaces.event.CellEditEvent;
//import org.primefaces.event.


import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import controller.MessageForPrimefaces;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import EJB.ModulFacadeLocal;
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
	public void createPruefcode() throws Exception  {
		EntityManager em = emf.createEntityManager();
		Pruefcode pCode = new Pruefcode();  
		pCode.setPflichtOderWahl(dutyOrChoice);    
		pCode.setPrCode(verifyCode);      
		pCode.setVertiefungsrichtungShortName(specializationShort);
		pCode.setStudiengang(findSg(courseId));
		try {
			pruefcodeFacadeLocal.create(pCode);
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
	
	public void createDoPruefcode() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
			createPruefcode();
			codeList = getCodeListAll();
		
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Pruefcode> getCodeListAll(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Pruefcode> query = em.createNamedQuery("Pruefcode.findAll", Pruefcode.class);
		codeList = query.getResultList();
		return query.getResultList();
	}
	
	
	
	
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deletePruefcode() throws Exception {
        codeList.remove(codeSelected);
        EntityManager em = emf.createEntityManager();
        TypedQuery<Pruefcode> q = em.createNamedQuery("Pruefcode.findByPcid",Pruefcode.class);
        q.setParameter("pcid", codeSelected.getPcid());
        code = (Pruefcode)q.getSingleResult();
        
        try {
        	pruefcodeFacadeLocal.remove(code);
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
    
    public void onRowSelect(SelectEvent<Pruefcode> e) {
    	FacesMessage msg = new FacesMessage("Pruefcode ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        codeSelected = e.getObject();
        courseId = codeSelected.getStudiengang().getSgid();
        
    }
    
    public void addPruefcode(){
    	 try {
 	        EntityManager em = emf.createEntityManager();
 	        em.find(Pruefcode.class, codeSelected.getPcid());
 	        code.setPflichtOderWahl(codeSelected.getPflichtOderWahl());
 	        code.setPrCode(codeSelected.getPrCode());
 	        code.setVertiefungsrichtungShortName(codeSelected.getVertiefungsrichtungShortName());
 	        code.setPcid(codeSelected.getPcid());
 	        code.setStudiengang(findSg(courseId));
 	        pruefcodeFacadeLocal.edit(code);
 	    }
 	    catch (Exception e) {
 	        try {
 	            ut.rollback();
 	        } 
 	        catch (IllegalStateException | SecurityException | SystemException ex) {
 	        }
 	    }
    	 codeList = getCodeListAll();
    }
    
    
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
    
	
	  
	//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}