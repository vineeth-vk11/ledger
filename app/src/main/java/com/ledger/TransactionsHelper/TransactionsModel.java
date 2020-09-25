package com.ledger.TransactionsHelper;

import java.util.Date;

public class TransactionsModel implements Comparable<TransactionsModel>{

    String date;
    String credit;
    String debit;
    String particular;
    Date dateD;
    String voucher;

    public TransactionsModel(){
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getParticular() {
        return particular;
    }

    public void setParticular(String particular) {
        this.particular = particular;
    }

    public Date getDateD() {
        return dateD;
    }

    public void setDateD(Date dateD) {
        this.dateD = dateD;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    @Override
    public int compareTo(TransactionsModel o) {
        return this.getDateD().compareTo(o.dateD);
    }
}
