package dto;

public class CategoryDTO {
    private long id;
    private String name;
    private String imageName;

    public CategoryDTO() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

}
