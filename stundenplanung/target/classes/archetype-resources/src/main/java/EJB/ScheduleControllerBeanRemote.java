package EJB;

import java.util.List;

import javax.ejb.Remote;

import model.Stundenplaneintrag;

@Remote
public interface ScheduleControllerBeanRemote {

	void create(Stundenplaneintrag studenplaneintrag);

    void edit(Stundenplaneintrag studenplaneintrag);

    void remove(Stundenplaneintrag studenplaneintrag);

    Stundenplaneintrag find(Object id);

    List<Stundenplaneintrag> findAll();

    List<Stundenplaneintrag> findRange(int[] range);

    int count();
}

