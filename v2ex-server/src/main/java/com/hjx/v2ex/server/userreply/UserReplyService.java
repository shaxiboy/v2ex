package com.hjx.v2ex.server.userreply;

import com.hjx.v2ex.server.common.model.Userreply;
import com.jfinal.plugin.activerecord.Page;

/**
 * Created by shaxiboy on 2017/5/18 0018.
 */
public class UserReplyService {

    private static final Userreply dao = new Userreply().dao();

    public Page<Userreply> paginate(int pageNumber, int pageSize) {
        return dao.paginate(pageNumber, pageSize, "select *", "from userreply order by id asc");
    }

    public void deleteById(int id) {
        dao.deleteById(id);
    }
}
