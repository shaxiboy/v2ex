package com.hjx.v2ex.server;

import com.hjx.v2ex.server.common.model._MappingKit;
import com.hjx.v2ex.server.userreply.UserReplyController;
import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

/**
 * Created by shaxiboy on 2017/5/17 0017.
 */
public class V2EXConfig extends JFinalConfig {
    @Override
    public void configConstant(Constants me) {
        me.setDevMode(true);
    }

    @Override
    public void configRoute(Routes me) {
        me.add("/", V2EXController.class);
        me.add("/userreply", UserReplyController.class);
    }

    @Override
    public void configEngine(Engine me) {
        me.addSharedFunction("/common/_layout.html");
        me.addSharedFunction("/common/_paginate.html");
    }

    @Override
    public void configPlugin(Plugins me) {
        DruidPlugin dp = new DruidPlugin("jdbc:sqlite:v2ex.db", null, null);
        dp.setDriverClass("org.sqlite.JDBC");
        me.add(dp);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        me.add(arp);
        _MappingKit.mapping(arp);
    }

    @Override
    public void configInterceptor(Interceptors me) {

    }

    @Override
    public void configHandler(Handlers me) {

    }

    public static DruidPlugin createDruidPlugin() {
        return new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
    }
}
