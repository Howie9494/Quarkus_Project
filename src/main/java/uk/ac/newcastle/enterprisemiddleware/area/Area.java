package uk.ac.newcastle.enterprisemiddleware.area;

import java.io.Serializable;
/**
 * <p>Simple POJO representing AreaCode objects</p>
 *
 * @author hugofirth
 */
public class Area implements Serializable {

    private static final long serialVersionUID = 249872301293L;

    private int id;
    private String state;
    private String abbr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Area)) return false;

        Area area = (Area) o;

        if (id != area.id) return false;
        if (!state.equals(area.state)) return false;
        return abbr.equals(area.abbr);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + state.hashCode();
        result = 31 * result + abbr.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Area{" +
                "id=" + id +
                ", state='" + state + '\'' +
                ", abbr='" + abbr + '\'' +
                '}';
    }
}

