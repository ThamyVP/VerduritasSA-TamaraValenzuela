package tamara.valenzuela.verduritassa;

public class Cultivo {
    private String id;
    private String alias;
    private String fecha;

    public Cultivo(String idDocumento, String alias, String fechaCosecha) {
        this.id = idDocumento;
        this.alias = alias;
        this.fecha = fechaCosecha;
    }

    public String getIdDocumento() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public String getFechaCosecha() {
        return fecha;
    }
}
