package controller;

import model.Faculty;
import model.Studiengang;
import model.Lehrveranstaltungsart;
import model.Raum;
import model.Sgmodul;
import model.Stundenplaneintrag;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import java.util.Calendar;

/**
*
* @author Manuel
*/
@Named(value="scheduleController")
@SessionScoped
public class ScheduleController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Raum raum;
	private Lehrveranstaltungsart lehrveranstaltungsart;
	private Sgmodul sgmodul;
	private Faculty faculty;
	//private Stundenplaneintrag stundenplaneintrag;
	
	@PostConstruct
    public void init() {
        auswahl();
        lazyEventModel = new LazyScheduleModel() {
        	@Override
            public void loadEvents(LocalDateTime start, LocalDateTime end) { 
                eventLaden();
                moduleLaden();
            }   
        };
    }
	
	
	private Date SPEStartZeit;
	private Date SPEEndZeit;
	private int SPTermin;
	private int studierendenzahl;
	private Timestamp zeitStempel;
	
	private ScheduleModel eventModel;
    private ScheduleModel lazyEventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private Date wochenStart;
    
    private List<Stundenplaneintrag> eintragListe;
    private Stundenplaneintrag selectedEvent;
	
	ArrayList<String> LehrveranstaltungsListe = new ArrayList<>();
    private String lvname;
    
    ArrayList<Integer> RaumListe = new ArrayList<>();
    private int rid;
    
    ArrayList<Integer> SgmodulListe = new ArrayList<>();
    private int sgmid;
    
    ArrayList<Integer> semesterList = new ArrayList<>();
    private int semester;
    private int semester_auswahl;
    
    ArrayList<String> studiengangList = new ArrayList<>();
    private String studiengang_auswahl;
    
    ArrayList<String> facultyList = new ArrayList<>();
    private String faculty_auswahl;
    
    ArrayList<Sgmodul> sgsemModulList = new ArrayList<>();
    ArrayList<String> modulList = new ArrayList<>();
    
    LocalDateTime l;
    LocalDateTime l2;
    LocalDateTime l3;
    LocalDateTime l4;
    Calendar cal;
    Calendar t;
    Calendar t2;
    Date te;
    Date te2;
    
  //--------------------------------------------------------------
    public void moduleLaden() {
		/*try{
	         EntityManager em = emf.createEntityManager();
	         Query q = em.createNamedQuery("Sgmodul.findBySemesterAndStudiengang",Sgmodul.class);
	         q.setParameter("semester", semester_auswahl);
	         q.setParameter("SGName", studiengang_auswahl);
	         
	         List liste = q.getResultList();
	         
	         
	         for(Object obj : liste){
	             Sgmodul r = (Sgmodul) obj;
	             modulList.add(r.getModul().getModName());                        
	             sgsemModulList.add(r);
	                             
	         }
	     }
	     catch(Exception e){
	     }*/
    	
    	try{
	        EntityManager em = emf.createEntityManager();
	        Query q = em.createNamedQuery("Sgmodul.findBySemesterAndStudiengangAndFaculty",Sgmodul.class);
	        q.setParameter("semester", semester_auswahl);
	        q.setParameter("SGName", studiengang_auswahl);
	        q.setParameter("facName", faculty_auswahl);
	        
	        List liste = q.getResultList();
	        
	        
	        for(Object obj : liste){
	            Sgmodul r = (Sgmodul) obj;
	            modulList.add(r.getModul().getModName());                        
	            sgsemModulList.add(r);
	                            
	        }
	    }
	    catch(Exception e){
	    }
	}
    
	public void eventLaden() {
		int dayOffset;
        wochenStart = getWeekStartDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(wochenStart);
        
        try{/* Laden der Datenbank*/
            EntityManager em = emf.createEntityManager();
            Query query = em.createNamedQuery("Stundenplaneintrag.findAllPlan", Stundenplaneintrag.class);
            query.setParameter("semester", semester_auswahl);
            query.setParameter("stgang", studiengang_auswahl);
            eintragListe = query.getResultList();
        }
        catch(Exception e){}
        
        for(Object eintrag : eintragListe){
            //Eventeinträge
            Stundenplaneintrag spe = (Stundenplaneintrag) eintrag;
            dayOffset = spe.getSPEStartZeit().getDay()-3;
            
            t = (Calendar) cal.clone();
            //Zeiten
            t.add(Calendar.DATE, dayOffset);
            //t.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
            t.set(Calendar.HOUR_OF_DAY, spe.getSPEStartZeit().getHours());
            t.set(Calendar.MINUTE, spe.getSPEStartZeit().getMinutes());
            t.set(Calendar.SECOND, spe.getSPEStartZeit().getSeconds());
            LocalDateTime ltime = convertToLocalDateTimeViaInstant(t.getTime());
            
            t2 = (Calendar) t.clone();
            t2.set(Calendar.HOUR_OF_DAY, spe.getSPEEndZeit().getHours());
            t2.set(Calendar.MINUTE, spe.getSPEEndZeit().getMinutes());
            t2.set(Calendar.SECOND, spe.getSPEEndZeit().getSeconds());
            LocalDateTime ltime2 = convertToLocalDateTimeViaInstant(t2.getTime());
            
            String eintragString = (String) (spe.getSgmodul().getModul().getModKuerzel() +" / "+
                                             spe.getLehrveranstaltungsart().getLvkurz()+ " / " +
                                             spe.getSPTermin() + " \n " +
                                             spe.getRaum().getRName()
                                             );
            
            //Ereignis hinzufügen
            /*lazyEventModel.addEvent(new DefaultScheduleEvent(
                            eintragString,
                            ltime,
                            ltime2,
                            spe));*/
            
            /*lazyEventModel.addEvent(new DefaultScheduleEvent(
                    eintragString,
                    ltime,
                    ltime2));*/
            
            
            DefaultScheduleEvent event = DefaultScheduleEvent.builder()
                    .title(eintragString)
                    .startDate(ltime)
                    .endDate(ltime2)
                    .data(spe)
                    .build();
            lazyEventModel.addEvent(event);
            
            /*eventModel.addEvent(new DefaultScheduleEvent(
                    eintragString,
                    ltime,
                    ltime2,
                    spe));*/
            
        }
        
	}
	
	public void auswahl() {
		try{
            EntityManager em = emf.createEntityManager();
            Query q = em.createNamedQuery("Sgmodul.findAll",Sgmodul.class);
            List liste = q.getResultList();
            
            for(Object obj : liste){
                Sgmodul r = (Sgmodul) obj;
                
                boolean semNichtVorhanden = false;
                if(semesterList.isEmpty()){
                    semesterList.add(r.getModSemester());
                }else{
                    semNichtVorhanden = true;
                    for(Integer sem : semesterList){
                        if(sem.equals(r.getModSemester())){ 
                            semNichtVorhanden = false;
                            break;
                        }
                    }
                    if(semNichtVorhanden)
                        semesterList.add(r.getModSemester());
                }
            }
           
        }
        catch(Exception e){
        	
        }
		
		EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Lehrveranstaltungsart.findAll");
        List LList = q.getResultList();
        for (Object LListitem : LList)
        {
        	Lehrveranstaltungsart lva =(Lehrveranstaltungsart)LListitem;
            LehrveranstaltungsListe.add(lva.getLvname());
        }
        
        EntityManager em2 = emf.createEntityManager();
        Query r = em2.createNamedQuery("Raum.findAll");
        List RList = r.getResultList();
        for (Object RListitem : RList)
        {
        	Raum ra =(Raum)RListitem;
            RaumListe.add(ra.getRid());
        }
        
        EntityManager em3 = emf.createEntityManager();
        Query s = em3.createNamedQuery("Sgmodul.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Sgmodul sg =(Sgmodul)SListitem;
            SgmodulListe.add(sg.getSgmid());
        }
		
		EntityManager em4 = emf.createEntityManager();
        Query s1 = em4.createNamedQuery("Studiengang.findAll");
        List S1List = s1.getResultList();
        for (Object S1Listitem : S1List)
        {
        	Studiengang sg1 =(Studiengang)S1Listitem;
            studiengangList.add(sg1.getSGName());
        }
        
        EntityManager em5 = emf.createEntityManager();
        Query f = em5.createNamedQuery("Faculty.findAll");
        List FList = f.getResultList();
        for (Object FListitem : FList)
        {
        	Faculty f1 =(Faculty)FListitem;
            facultyList.add(f1.getFacName());
        }
        
		faculty_auswahl = facultyList.get(8);
        semester = 6;
        semester_auswahl = semester;
        studiengang_auswahl = studiengangList.get(0);

	}
	
	/* Bearbeiten oder Einfügen der Events*/
    public void addEvent() throws NotSupportedException, 
            SystemException, RollbackException, HeuristicMixedException, 
            HeuristicRollbackException, javax.transaction.RollbackException {
        
        String msg;
        EntityManager em;
        em = emf.createEntityManager();
        
        try{
            if(getEvent().getId() == null){             
                selectedEvent.setSPEStartZeit(asDate(event.getStartDate()));
                selectedEvent.setSPEEndZeit(asDate(event.getEndDate()));
                selectedEvent.setSPTermin(SPTermin);
                selectedEvent.setSgmodul(findSgm(sgmid));
                selectedEvent.setLehrveranstaltungsart(findLva(lvname));
                selectedEvent.setRaum(findRau(rid));
                selectedEvent.setStudierendenzahl(studierendenzahl);
                Date date= new Date();
                long time = date.getTime();
                zeitStempel = new Timestamp(time);
                selectedEvent.setZeitStempel(zeitStempel);
                
                te = selectedEvent.getSPEStartZeit();
                te2 = selectedEvent.getSPEEndZeit();
                
                l3 = event.getStartDate();
                //l3 = l3.plusDays(2);
                l4 = event.getEndDate();
                //l4 = l4.plusDays(2);
                
                String eintragString = (String) selectedEvent.getSgmodul().getModul().getModKuerzel() +" / "+
        				selectedEvent.getLehrveranstaltungsart().getLvkurz()+ " / " +
        				selectedEvent.getSPTermin() + " \n " +
        				selectedEvent.getRaum().getRName();
				       
                DefaultScheduleEvent event = DefaultScheduleEvent.builder()
                        .title(eintragString)
                        .startDate(l3)
                        .endDate(l4)
                        .data(selectedEvent)
                        .build();
                lazyEventModel.addEvent(event);
                
                // Titel wird eintragen                
                /*event = new DefaultScheduleEvent(
                		selectedEvent.getSgmodul().getModul().getModKuerzel() +" / "+
        				selectedEvent.getLehrveranstaltungsart().getLvkurz()+ " / " +
        				selectedEvent.getSPTermin() + " \n " +
        				selectedEvent.getRaum().getRName(),
                		l3, l4, selectedEvent);*/
                
                /*event = new DefaultScheduleEvent(
                		selectedEvent.getSgmodul().getModul().getModKuerzel() +" / "+
        				selectedEvent.getLehrveranstaltungsart().getLvkurz()+ " / " +
        				selectedEvent.getSPTermin() + " \n " +
        				selectedEvent.getRaum().getRName(),
                		l3, l4, selectedEvent);
                                
                // Hinzufügen des neuen Events
                lazyEventModel.addEvent(event);*/
                //eventModel.addEvent(event);
                
                /*String eintragString = (String) selectedEvent.getSgmodul().getModul().getModKuerzel() +" / "+
        				selectedEvent.getLehrveranstaltungsart().getLvkurz()+ " / " +
        				selectedEvent.getSPTermin() + " \n " +
        				selectedEvent.getRaum().getRName();

				//Ereignis hinzufügen
				lazyEventModel.addEvent(new DefaultScheduleEvent(
				       eintragString,
				       l3,
				       l4,
				       selectedEvent));*/
                
             
                
                
                ut.begin();
            	em.joinTransaction();
                em.persist(selectedEvent);
                ut.commit();
                msg = "Ereignis wurde hinzugefügt!";
                //eventLaden();
                //moduleLaden();
                
               /* int dayOffset;
                wochenStart = getWeekStartDate();
                cal = Calendar.getInstance();
                cal.setTime(wochenStart);
                
                dayOffset = selectedEvent.getSPEStartZeit().getDay()-3;
                Calendar t = (Calendar) cal.clone();
                //Zeiten
                t.add(Calendar.DATE, dayOffset);
                
                //t.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
                t.set(Calendar.HOUR_OF_DAY, SPEStartZeit.getHours());
                t.set(Calendar.MINUTE, SPEStartZeit.getMinutes());
                t.set(Calendar.SECOND, SPEStartZeit.getSeconds());
                l3 = convertToLocalDateTimeViaInstant(t.getTime());
                
                Calendar t2 = (Calendar) t.clone();
                t2.set(Calendar.HOUR_OF_DAY, SPEEndZeit.getHours());
                t2.set(Calendar.MINUTE, SPEEndZeit.getMinutes());
                t2.set(Calendar.SECOND, SPEEndZeit.getSeconds());
                l4 = convertToLocalDateTimeViaInstant(t2.getTime());*/
                
                //te = t.getTime();
                
                //te2 = t2.getTime();
                
               /* String eintragString = (String) (selectedEvent.getSgmodul().getModul().getModKuerzel() +" / "+
                		selectedEvent.getLehrveranstaltungsart().getLvkurz()+ " / " +
                		selectedEvent.getSPTermin() + " \n " +
                		selectedEvent.getRaum().getRName()
                        );

				//Ereignis hinzufügen
				lazyEventModel.addEvent(new DefaultScheduleEvent(
				       eintragString,
				       l3,
				       l4,
				       selectedEvent));*/
                
                
                
                
            }
            else{/* ID schon vorhanden*/
                lazyEventModel.updateEvent(getEvent());
            	//eventModel.updateEvent(getEvent());
                ut.begin();
                //em = emf.createEntityManager();
                em.find(Stundenplaneintrag.class, selectedEvent.getSpid());
                
                selectedEvent.setSPEStartZeit(selectedEvent.getSPEStartZeit());
                selectedEvent.setSPEEndZeit(selectedEvent.getSPEEndZeit());
                selectedEvent.setSPTermin(selectedEvent.getSPTermin());
                selectedEvent.setSgmodul(findSgm(sgmid));
                selectedEvent.setLehrveranstaltungsart(findLva(lvname));
                selectedEvent.setRaum(findRau(rid));
                selectedEvent.setStudierendenzahl(selectedEvent.getStudierendenzahl());
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                zeitStempel = new Timestamp(time);
                
                selectedEvent.setZeitStempel(zeitStempel);
                em.merge(selectedEvent);
                ut.commit();
                msg = "Ereignis wurde geändert!";
                
            }
            event = new DefaultScheduleEvent();
            addMessage("messages", msg);
        }
        catch(NotSupportedException | SystemException | RollbackException | 
                HeuristicMixedException | HeuristicRollbackException | 
                SecurityException | IllegalStateException e){
            msg = "Ereignis-Daten sind noch nicht vorhanden!";
            addMessage("messages", msg);
        }
    }
    
    //Löschen von Events
    public void deleteEvent() throws javax.transaction.RollbackException{
        
        String msg = "";        
        
        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Stundenplaneintrag> q = em.createNamedQuery("Stundenplaneintrag.findById",Stundenplaneintrag.class);
        q.setParameter("spid", selectedEvent.getSpid());
        selectedEvent = (Stundenplaneintrag)q.getSingleResult();
        
        try{
            ut.begin();
	        em.joinTransaction();  
            em.remove(selectedEvent);
            ut.commit();
            msg = "Ereignis wurde gelöscht!";
        }
        catch(  NotSupportedException | SystemException | 
                RollbackException | HeuristicMixedException | 
                HeuristicRollbackException | SecurityException | 
                IllegalStateException e){
            msg = "Ereignis konnte nicht gelöscht werden!";    
        }
        addMessage("messages", msg);
        lazyEventModel.deleteEvent(event);
        //eventModel.deleteEvent(event);
    }
    
    //Auswahl der Events
    public void onEventSelect(SelectEvent<ScheduleEvent> e) {
        event = (ScheduleEvent) e.getObject();
        selectedEvent = (Stundenplaneintrag) event.getData();
        
        sgmid = selectedEvent.getSgmodul().getSgmid();
        lvname = selectedEvent.getLehrveranstaltungsart().getLvname();
        rid = selectedEvent.getRaum().getRid();
    }
    
    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        this.selectedEvent = new Stundenplaneintrag();
        //event = DefaultScheduleEvent.builder().startDate(selectEvent.getObject()).endDate(selectEvent.getObject().plusHours(1)).build();
        
        l = selectEvent.getObject();
        l2 = selectEvent.getObject().plusHours(1);
        //event = DefaultScheduleEvent.builder().startDate(l).endDate(l2).build();
        
        event = DefaultScheduleEvent.builder()
                .title("")
                .startDate(l)
                .endDate(l2)
                .data(selectedEvent)
                .build();
        
        
        /*int dayOffset;
        wochenStart = getWeekStartDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(wochenStart);
        
        dayOffset = selectedEvent.getSPEStartZeit().getDay()-3;
        
        Calendar t = (Calendar) cal.clone();
        //Zeiten
        t.add(Calendar.DATE, dayOffset);
        //t.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        t.set(Calendar.HOUR_OF_DAY, SPEStartZeit.getHours());
        t.set(Calendar.MINUTE, SPEStartZeit.getMinutes());
        t.set(Calendar.SECOND, SPEStartZeit.getSeconds());
        LocalDateTime l = convertToLocalDateTimeViaInstant(t.getTime());
        
        Calendar t2 = (Calendar) t.clone();
        t2.set(Calendar.HOUR_OF_DAY, SPEEndZeit.getHours());
        t2.set(Calendar.MINUTE, SPEEndZeit.getMinutes());
        t2.set(Calendar.SECOND, SPEEndZeit.getSeconds());
        LocalDateTime l2 = convertToLocalDateTimeViaInstant(t2.getTime());*/
        
       /*event = new DefaultScheduleEvent("",
               (LocalDateTime) selectEvent.getObject(),
               (LocalDateTime) selectEvent.getObject().plusHours(1),
                selectedEvent);
       
       l = (LocalDateTime) selectEvent.getObject();
       l2 = (LocalDateTime) selectEvent.getObject().plusHours(1);*/
        /*event = new DefaultScheduleEvent("",
                l,
                l2,
                selectedEvent);*/
        
    }
    
    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Delta:" + event.getDeltaAsDuration());
        String msg1 = "Event moved."; 
        addMessage("messages", msg1);
        
        selectedEvent = (Stundenplaneintrag) event.getScheduleEvent().getData();
        SPEStartZeit = selectedEvent.getSPEStartZeit();
        SPEEndZeit = selectedEvent.getSPEEndZeit();
        
        
        long dura = event.getDeltaAsDuration().getSeconds();
        dura = dura * 1000;
        long sum = SPEStartZeit.getTime() + dura;
        Date date1 = new Date(sum);
        
        long sum1 = SPEEndZeit.getTime() + dura;
        Date date2 = new Date(sum1);
        
        String msg;
        EntityManager em;
        em = emf.createEntityManager();
        
        try{
                ut.begin();
                em.find(Stundenplaneintrag.class, selectedEvent.getSpid());
                selectedEvent.setSPEStartZeit(date1);
                selectedEvent.setSPEEndZeit(date2);
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                zeitStempel = new Timestamp(time);
                selectedEvent.setZeitStempel(zeitStempel);
                em.merge(selectedEvent);
                ut.commit();
                msg = "Ereignis wurde geändert!";
                addMessage("messages", msg);
        }
        catch(NotSupportedException | SystemException | RollbackException | 
                HeuristicMixedException | HeuristicRollbackException | 
                SecurityException | IllegalStateException e){
            msg = "Ereignis-Daten sind noch nicht vorhanden!";
            addMessage("messages", msg);
        }
    }
    
    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Start-Delta:" + event.getDeltaStartAsDuration() + ", End-Delta: " + event.getDeltaEndAsDuration());
        String msg1 = "Event resized"; 
        addMessage("message", msg1);
        selectedEvent = (Stundenplaneintrag) event.getScheduleEvent().getData();
        SPEStartZeit = selectedEvent.getSPEStartZeit();
        SPEEndZeit = selectedEvent.getSPEEndZeit();
        
        
        long dura = event.getDeltaEndAsDuration().getSeconds();
        dura = dura * 1000;
        long sum = SPEEndZeit.getTime() + dura;
        Date date1 = new Date(sum);
        
        String msg;
        EntityManager em;
        em = emf.createEntityManager();
        
        try{
                ut.begin();
                em.find(Stundenplaneintrag.class, selectedEvent.getSpid());
                selectedEvent.setSPEEndZeit(date1);
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                zeitStempel = new Timestamp(time);
                selectedEvent.setZeitStempel(zeitStempel);
                em.merge(selectedEvent);
                ut.commit();
                msg = "Ereignis wurde geändert!";
                addMessage("messages", msg);
        }
        catch(NotSupportedException | SystemException | RollbackException | 
                HeuristicMixedException | HeuristicRollbackException | 
                SecurityException | IllegalStateException e){
            msg = "Ereignis-Daten sind noch nicht vorhanden!";
            addMessage("messages", msg);
        }
        
        
    }
    
    
    // Fehler ausgeben
    public void addMessage(String toComponent, String message){
        FacesMessage msg = new FacesMessage(message);
        FacesContext cxt = FacesContext.getCurrentInstance();
        cxt.addMessage(toComponent, msg);
    }
    
    //--------------------------------------------------------------
    
    public ArrayList<String> getLehrveranstaltungsListe() {
		return LehrveranstaltungsListe;
	}
	public void setLehrveranstaltungsListe(ArrayList<String> lehrveranstaltungsListe) {
		LehrveranstaltungsListe = lehrveranstaltungsListe;
	}
	public String getLvname() {
		return lvname;
	}
	public void setLvname(String lvname) {
		this.lvname = lvname;
	}
	
	//--------------------------------------------------------------
	
	public ArrayList<Integer> getRaumListe() {
		return RaumListe;
	}
	public void setRaumListe(ArrayList<Integer> raumListe) {
		RaumListe = raumListe;
	}
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	
	//--------------------------------------------------------------
    
	public ArrayList<Integer> getSgmodulListe() {
		return SgmodulListe;
	}
	public void setSgmodulListe(ArrayList<Integer> sgmodulListe) {
		SgmodulListe = sgmodulListe;
	}
	
	public int getSgmid() {
		return sgmid;
	}
	public void setSgmid(int sgmid) {
		this.sgmid = sgmid;
	}
	
	//--------------------------------------------------------------
	
	public Raum getRaum() {
		return raum;
	}
	public void setRaum(Raum raum) {
		this.raum = raum;
	}
	
	public Lehrveranstaltungsart getLehrveranstaltungsart() {
		return lehrveranstaltungsart;
	}
	public void setLehrveranstaltungsart(Lehrveranstaltungsart lehrveranstaltungsart) {
		this.lehrveranstaltungsart = lehrveranstaltungsart;
	}
	
	public Sgmodul getSgmodul() {
		return sgmodul;
	}
	public void setSgmodul(Sgmodul sgmodul) {
		this.sgmodul = sgmodul;
	}
	
	public Faculty getFaculty() {
		return faculty;
	}
	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}	
	
	public Date getSPEStartZeit() {
		return SPEStartZeit;
	}
	public void setSPEStartZeit(Date SPEStartZeit) {
		this.SPEStartZeit = SPEStartZeit;
	}
	
	public Date getSPEEndZeit() {
		return SPEEndZeit;
	}
	public void setSPEEndZeit(Date SPEEndZeit) {
		this.SPEEndZeit = SPEEndZeit;
	}
	
	public int getSPTermin() {
		return SPTermin;
	}
	public void setSPTermin(int SPTermin) {
		this.SPTermin = SPTermin;
	}
	
	public int getStudierendenzahl() {
		return studierendenzahl;
	}
	public void setStudierendenzahl(int studierendenzahl) {
		this.studierendenzahl = studierendenzahl;
	}
	
	public Timestamp getZeitStempel() {
		return zeitStempel;
	}
	public void setZeitStempel(Timestamp zeitStempel) {
		this.zeitStempel = zeitStempel;
	}	
	
	//--------------------------------------------------------------
	
	public ScheduleModel getLazyEventModel() {
        return lazyEventModel;
    }
    public void setLazyEventModel(ScheduleModel lazyEventModel) {
        this.lazyEventModel = lazyEventModel;
    }
    
    public ScheduleEvent getEvent() { 
    	return event; 
    }
    public void setEvent(ScheduleEvent event) { 
    	this.event = event; 
    }
    
    public Stundenplaneintrag getSelectedEvent() {
		return selectedEvent;
	}
	public void setSelectedEvent(Stundenplaneintrag selectedEvent) {
		this.selectedEvent = selectedEvent;
	}
	
    //--------------------------------------------------------------
	
	public ArrayList<Integer> getSemesterList() {
		return semesterList;
	}

	public void setSemesterList(ArrayList<Integer> semesterList) {
		this.semesterList = semesterList;
	}

	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public int getSemester_auswahl() {
		return semester_auswahl;
	}

	public void setSemester_auswahl(int semester_auswahl) {
		this.semester_auswahl = semester_auswahl;
	}

	public ArrayList<String> getStudiengangList() {
		return studiengangList;
	}

	public void setStudiengangList(ArrayList<String> studiengangList) {
		this.studiengangList = studiengangList;
	}

	public String getStudiengang_auswahl() {
		return studiengang_auswahl;
	}

	public void setStudiengang_auswahl(String studiengang_auswahl) {
		this.studiengang_auswahl = studiengang_auswahl;
	}
	
	public ArrayList<String> getFacultyList() {
		return facultyList;
	}

	public void setFacultyList(ArrayList<String> facultyList) {
		this.facultyList = facultyList;
	}

	public String getFaculty_auswahl() {
		return faculty_auswahl;
	}

	public void setFaculty_auswahl(String faculty_auswahl) {
		this.faculty_auswahl = faculty_auswahl;
	}
	
	
	// Listen Objekt finden
	private Raum findRau(int rid) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Raum> query
                = em.createNamedQuery("Raum.findByRid",Raum.class);
            query.setParameter("rid", rid);
            raum = (Raum)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return raum;
    }

	private Lehrveranstaltungsart findLva(String lvname) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Lehrveranstaltungsart> query
                = em.createNamedQuery("Lehrveranstaltungsart.findByLvname",Lehrveranstaltungsart.class);
            query.setParameter("lvname", lvname);
            lehrveranstaltungsart = (Lehrveranstaltungsart)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return lehrveranstaltungsart;
    }
	
	private Sgmodul findSgm(int sgmid) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Sgmodul> query
                = em.createNamedQuery("Sgmodul.findBySgmid",Sgmodul.class);
            query.setParameter("sgmid", sgmid);
            sgmodul = (Sgmodul)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return sgmodul;
    }
	
	public static LocalDateTime asLocalDateTime(Date date) {
	    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	public static Date asDate(LocalDateTime localDateTime) {
	    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	//--------------------------------------------------------------
	
	public Date getWochenStart() {
		return wochenStart;
	}


	public void setWochenStart(Date wochenStart) {
		this.wochenStart = wochenStart;
	}


	public List<Stundenplaneintrag> getEintragListe() {
		return eintragListe;
	}


	public void setEintragListe(List<Stundenplaneintrag> eintragListe) {
		this.eintragListe = eintragListe;
	}


	public ArrayList<Sgmodul> getSgsemModulList() {
		return sgsemModulList;
	}


	public void setSgsemModulList(ArrayList<Sgmodul> sgsemModulList) {
		this.sgsemModulList = sgsemModulList;
	}


	public ArrayList<String> getModulList() {
		return modulList;
	}


	public void setModulList(ArrayList<String> modulList) {
		this.modulList = modulList;
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public static String formatDuration(Duration duration) {
	    long seconds = duration.getSeconds();
	    long absSeconds = Math.abs(seconds);
	    String positive = String.format(
	        "%d:%02d:%02d",
	        absSeconds / 3600,
	        (absSeconds % 3600) / 60,
	        absSeconds % 60);
	    return seconds < 0 ? "-" + positive : positive;
	}
	
	public static Date addSeconds(Date date, Integer seconds) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.SECOND, seconds);
	    return cal.getTime();
	  }
	
	public static Date getWeekStartDate() {
	    Calendar calendar = Calendar.getInstance();
	    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
	        calendar.add(Calendar.DATE, -1);
	    }
	    return calendar.getTime();
	}
	
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}

	public LocalDateTime getL() {
		return l;
	}

	public void setL(LocalDateTime l) {
		this.l = l;
	}

	public LocalDateTime getL2() {
		return l2;
	}

	public void setL2(LocalDateTime l2) {
		this.l2 = l2;
	}

	public LocalDateTime getL3() {
		return l3;
	}

	public void setL3(LocalDateTime l3) {
		this.l3 = l3;
	}

	public LocalDateTime getL4() {
		return l4;
	}

	public void setL4(LocalDateTime l4) {
		this.l4 = l4;
	}

	public Calendar getCal() {
		return cal;
	}

	public void setCal(Calendar cal) {
		this.cal = cal;
	}

	public Date getTe() {
		return te;
	}

	public void setTe(Date te) {
		this.te = te;
	}

	public Date getTe2() {
		return te2;
	}

	public void setTe2(Date te2) {
		this.te2 = te2;
	}
	
	private Date ConvertToDate(String dateString){
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date convertedDate = new Date();
	    try {
	        convertedDate = dateFormat.parse(dateString);
	    } catch (ParseException e) {
	        // TODO Auto-generated catch block
	    }
	    return convertedDate;
	}

	public Calendar getT() {
		return t;
	}

	public void setT(Calendar t) {
		this.t = t;
	}

	public Calendar getT2() {
		return t2;
	}

	public void setT2(Calendar t2) {
		this.t2 = t2;
	}	
	
}
