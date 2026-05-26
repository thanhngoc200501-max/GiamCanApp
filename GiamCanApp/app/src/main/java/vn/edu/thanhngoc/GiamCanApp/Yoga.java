package vn.edu.thanhngoc.GiamCanApp;

import java.io.Serializable;
import java.util.ArrayList;

public class Yoga implements Serializable {
    private String TieuDe;
    private String mota;
    private String picPath;
    private int calo;
    private ArrayList<Lession> lessions; // Đã sửa thành Lession chuẩn

    public String getTieuDe() {
        return TieuDe;
    }

    public void setTieuDe(String tieuDe) {
        TieuDe = tieuDe;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public int getCalo() {
        return calo;
    }

    public void setCalo(int calo) {
        this.calo = calo;
    }

    public ArrayList<Lession> getLessions() {
        return lessions;
    }

    public void setLessions(ArrayList<Lession> lessions) {
        this.lessions = lessions;
    }
}