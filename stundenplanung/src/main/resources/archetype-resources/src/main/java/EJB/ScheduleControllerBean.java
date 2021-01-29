package EJB;

import model.Stundenplaneintrag;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ScheduleControllerBean
 */
@Stateless
@LocalBean
public class ScheduleControllerBean implements ScheduleControllerBeanRemote {

    /**
     * Default constructor. 
     */
    public ScheduleControllerBean() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public void create(Stundenplaneintrag studenplaneintrag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void edit(Stundenplaneintrag studenplaneintrag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Stundenplaneintrag studenplaneintrag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Stundenplaneintrag find(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Stundenplaneintrag> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Stundenplaneintrag> findRange(int[] range) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int count() {
		// TODO Auto-generated method stub
		return 0;
	}

}
