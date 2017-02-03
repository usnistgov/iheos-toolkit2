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
    // Private constructor whose sole is to hide the implicit public one.
    private CookiesServices(){}

    /**
     * This method retrieves the list of favorite PIDs from the cookies.
     * @return set of favorite PIDs stored in cookies.
     */
    public static Set<Pid> retrievePidFavoritesFromCookies() {
        return new PidSet(Cookies.getCookie(CookieManager.FAVORITEPIDSCOOKIENAME)).get();
    }

    /**
     * This method stores the list of favorite PIDs in Cookies
     * @param favorites list of favorite PIDs to save in Cookies.
     */
    public static void savePidFavoritesToCookies(List<Pid> favorites){
        if (!favorites.isEmpty()){
            Cookies.setCookie(CookieManager.FAVORITEPIDSCOOKIENAME, new PidSet(new HashSet(favorites)).asParsableString());
        }else {
            Cookies.setCookie(CookieManager.FAVORITEPIDSCOOKIENAME, " ");
        }
    }
}
