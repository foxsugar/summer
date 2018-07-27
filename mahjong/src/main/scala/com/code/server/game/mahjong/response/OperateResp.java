package com.code.server.game.mahjong.response;

/**
 * Created by T420 on 2016/12/1.
 */
public class OperateResp {
    private boolean isCanChi;
    private boolean isCanGang;
    private boolean isCanPeng;
    private boolean isCanTing;
    private boolean isCanHu;
    private boolean isCanChiTing;
    private boolean isCanPengTing;
    private boolean isCanXuanfengDan;
    private boolean isCanBufeng;


    public void setCanBeOperate(boolean chi,boolean peng,boolean gang,boolean ting,boolean hu,boolean chiTing,boolean pengTing){
        this.isCanChi = chi;
        this.isCanPeng = peng;
        this.isCanGang = gang;
        this.isCanTing = ting;
        this.isCanHu = hu;
        this.isCanChiTing = chiTing;
        this.isCanPengTing = pengTing;
    }

    public boolean isCanGang() {
        return isCanGang;
    }

    public void setIsCanGang(boolean isCanGang) {
        this.isCanGang = isCanGang;
    }

    public boolean isCanPeng() {
        return isCanPeng;
    }

    public void setIsCanPeng(boolean isCanPeng) {
        this.isCanPeng = isCanPeng;
    }

    public boolean isCanTing() {
        return isCanTing;
    }

    public void setIsCanTing(boolean isCanTing) {
        this.isCanTing = isCanTing;
    }

    public boolean isCanHu() {
        return isCanHu;
    }

    public void setIsCanHu(boolean isCanHu) {
        this.isCanHu = isCanHu;
    }

    public boolean isCanChi() {
        return isCanChi;
    }

    public OperateResp setCanChi(boolean canChi) {
        isCanChi = canChi;
        return this;
    }

    public boolean isCanChiTing() {
        return isCanChiTing;
    }

    public OperateResp setCanChiTing(boolean canChiTing) {
        isCanChiTing = canChiTing;
        return this;
    }

    public boolean isCanPengTing() {
        return isCanPengTing;
    }

    public OperateResp setCanPengTing(boolean canPengTing) {
        isCanPengTing = canPengTing;
        return this;
    }

    public boolean isCanXuanfengDan() {
        return isCanXuanfengDan;
    }

    public OperateResp setCanXuanfengDan(boolean canXuanfengDan) {
        isCanXuanfengDan = canXuanfengDan;
        return this;
    }

    public boolean isCanBufeng() {
        return isCanBufeng;
    }

    public OperateResp setCanBufeng(boolean canBufeng) {
        isCanBufeng = canBufeng;
        return this;
    }
}
