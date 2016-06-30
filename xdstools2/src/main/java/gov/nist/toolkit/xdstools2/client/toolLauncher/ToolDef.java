package gov.nist.toolkit.xdstools2.client.toolLauncher;

/**
 *
 *
 * */
public class ToolDef {
    String menuName;
    String tabName;
    String activityName;

    public ToolDef(String menuName, String tabName, String activityName) {
        this.menuName = menuName;
        this.tabName = tabName;
        this.activityName = activityName;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getTabName() {
        return tabName;
    }

    public String getActivityName() {
        return activityName;
    }

    public boolean isNamed(String name) {
        if (name == null) return false;
        if (name.equals(menuName)) return true;
        if (name.equals(tabName)) return true;
        if (name.equalsIgnoreCase(activityName)) return true;
        return false;
    }
}
