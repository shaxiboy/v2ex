package com.hjx.v2ex.server.userreply;

import com.hjx.v2ex.server.common.model.Userreply;
import com.jfinal.core.Controller;

/**
 * Created by shaxiboy on 2017/5/17 0017.
 */
public class UserReplyController extends Controller {

    static UserReplyService service = new UserReplyService();

    public void index() {
        setAttr("replyPage", service.paginate(getParaToInt(0, 1), 10));
        render("userreply.html");
    }

    public void save() {
        boolean success = false;
        success = getModel(Userreply.class, "").save();
        if(success) renderText("success");
        else renderText("failed");
    }

    public void delete() {
        service.deleteById(getParaToInt());
        redirect("/userreply");
    }
}
