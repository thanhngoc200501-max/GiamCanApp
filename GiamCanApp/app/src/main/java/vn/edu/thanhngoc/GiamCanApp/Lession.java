package vn.edu.thanhngoc.GiamCanApp;

import java.io.Serializable;

public class Lession implements Serializable {
    private String TieuDe;
    private String ThoiGian;
    private String link;
    private String picPath;
    public Lession() {
    }
    public Lession(String tieuDe, String link, String thoiGian, String picPath) {
        this.TieuDe = tieuDe;
        this.link = link;
        this.ThoiGian = thoiGian;
        this.picPath = picPath;
    }

    public String getTieuDe() {
        return TieuDe;
    }

    public void setTieuDe(String tieuDe) {
        TieuDe = tieuDe;
    }

    public String getThoiGian() {
        return ThoiGian;
    }

    public void setThoiGian(String thoiGian) {
        ThoiGian = thoiGian;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
}