package no.arasoft.portalcollector.portalreceiver;

/**
 * Created by Henrik on 03.04.2014.
 */
public class Portal {
    private long Id;
    private float Lat;
    private float Lon;
    private String Title;

    public float getLat() {
        return Lat;
    }

    public void setLat(float lat) {
        Lat = lat;
    }

    public float getLon() {
        return Lon;
    }

    public void setLon(float lon) {
        Lon = lon;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }
}
