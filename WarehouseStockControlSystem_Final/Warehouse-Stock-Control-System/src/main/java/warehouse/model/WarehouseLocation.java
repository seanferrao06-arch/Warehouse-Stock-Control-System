package warehouse.model;

public class WarehouseLocation {
    private int locationId;
    private String name;
    public WarehouseLocation() { }
    public WarehouseLocation(int locationId, String name) { this.locationId = locationId; this.name = name; }
    public WarehouseLocation(String name) { this(0, name); }
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
