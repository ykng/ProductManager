package models;

public class Product {
    private int id;
    private String image_url;
    private String title;
    private String description;
    private int price;

    public void setId(int id) {
        this.id = id;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public void setParam(String param_name, Object param) {
        switch ( param_name ) {
            case "id" :
                setId((int) param);
                break;
            case "image_url" :
                setImage_url((String) param);
                break;
            case "title" :
                setTitle((String) param);
                break;
            case "description" :
                setDescription((String) param);
                break;
            case "price" :
                setPrice((int) param);
                break;
        }
    }

}
