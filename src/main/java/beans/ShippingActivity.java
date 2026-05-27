package beans;

public class ShippingActivity {
    private String title;
    private String description;
    private String timestamp;
    private String icon;
    private String dotColor;

    public ShippingActivity() {}

    public ShippingActivity(String title, String description, String timestamp, String icon, String dotColor) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.icon = icon;
        this.dotColor = dotColor;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getDotColor() { return dotColor; }
    public void setDotColor(String dotColor) { this.dotColor = dotColor; }
}
