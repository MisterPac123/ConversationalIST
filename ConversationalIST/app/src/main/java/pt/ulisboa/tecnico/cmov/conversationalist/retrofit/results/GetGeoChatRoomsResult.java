package pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results;

public class GetGeoChatRoomsResult {

    private String name;
    private String type;
    private String description;
    private String coordinates;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoordinates() {return coordinates;}

    public void setCoordinates(String coordinates) {this.coordinates = coordinates;}
}
