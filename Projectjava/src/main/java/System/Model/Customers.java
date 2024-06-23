package System.Model;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Entity
public class Customers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Cusid;

    private String Cusname;

    private double Total;

    private Date date;


    public Customers() {
    }

    public Customers( String cusname, double total, Date date) {
        this.Cusname = cusname;
        this.Total = total;
        this.date = date;
    }

    public int getCusid() {
        return Cusid;
    }

    public void setCusid(int cusid) {
        Cusid = cusid;
    }

    public String getCusname() {
        return Cusname;
    }

    public void setCusname(String cusname) {
        Cusname = cusname;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}