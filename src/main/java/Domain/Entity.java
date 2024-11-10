package Domain;

// the ID is the unique identification of each entity
// ID is generic, so it can be replaced with multiple types and the class can be reused
// ID ~ primary key in a database

public class Entity<ID>  {

    private ID id;
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }
}


