package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.user.client.Cookies;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidSet;
import gov.nist.toolkit.xdstools2.client.CookieManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by onh2 on 7/20/16.
 */
public class CookiesServices {
    public static final Set<Pid> retrievePidFavoritesFromCookies() {
//        Window.alert("Read :"+new PidSet(Cookies.getCookie(CookieManager.FAVORITEPIDSCOOKIENAME)).asParsableString());
        return new PidSet(Cookies.getCookie(CookieManager.FAVORITEPIDSCOOKIENAME)).get();
    }

    public static void savePidFavoritesToCookies(List<Pid> favorites){
        if (!favorites.isEmpty()){
            Cookies.setCookie(CookieManager.FAVORITEPIDSCOOKIENAME, new PidSet(new HashSet(favorites)).asParsableString());
//            Window.alert("Write: "+new PidSet(new HashSet(favorites)).asParsableString());
        }else {
            Cookies.setCookie(CookieManager.FAVORITEPIDSCOOKIENAME, " ");
        }
    }
}
