package System.Model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sizeID;

    String sizeName;
    private int sizeQuantityS;
    private int sizeQuantityM;
    private int sizeQuantityL;
    private int sizeQuantityXL;

    public Size() {
    }

    public Size(String sizeName, int sizeQuantityS, int sizeQuantityM, int sizeQuantityL) {
        this.sizeName = sizeName;
        this.sizeQuantityS = sizeQuantityS;
        this.sizeQuantityM = sizeQuantityM;
        this.sizeQuantityL = sizeQuantityL;
    }

    public int getSizeID() {
        return sizeID;
    }

    public void setSizeID(int sizeID) {
        this.sizeID = sizeID;
    }

    public int getSizeQuantityS() {
        return sizeQuantityS;
    }

    public void setSizeQuantityS(int sizeQuantityS) {
        this.sizeQuantityS = sizeQuantityS;
    }

    public int getSizeQuantityM() {
        return sizeQuantityM;
    }

    public void setSizeQuantityM(int sizeQuantityM) {
        this.sizeQuantityM = sizeQuantityM;
    }

    public int getSizeQuantityL() {
        return sizeQuantityL;
    }

    public void setSizeQuantityL(int sizeQuantityL) {
        this.sizeQuantityL = sizeQuantityL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Size size = (Size) o;
        return sizeID == size.sizeID;
    }

    public int getSizeQuantityXL() {
        return sizeQuantityXL;
    }

    public void setSizeQuantityXL(int sizeQuantityXL) {
        this.sizeQuantityXL = sizeQuantityXL;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sizeID);
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }
}